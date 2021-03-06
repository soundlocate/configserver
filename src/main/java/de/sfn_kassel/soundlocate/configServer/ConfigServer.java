package de.sfn_kassel.soundlocate.configServer;

import com.moandjiezana.toml.Toml;
import de.sfn_kassel.soundlocate.configServer.config.Config;
import de.sfn_kassel.soundlocate.configServer.log.Logger;
import de.sfn_kassel.soundlocate.configServer.log.Stream;
import de.sfn_kassel.soundlocate.configServer.program.ProcessDiedListener;
import de.sfn_kassel.soundlocate.configServer.program.Program;
import de.sfn_kassel.soundlocate.configServer.program.Supervisor;
import de.sfn_kassel.soundlocate.configServer.programs.*;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by jaro on 29.03.16.
 * the main Class of the ConfigServer
 */

public class ConfigServer {
    private static Config config;
    private final Options options;
    private String filename = "config.toml";
    private SoundInput soundInput = null;
    private FpgaInput fpgaInput = null;
    private SoundSimulate soundSimulate = null;
    private SoundFFT soundFFT = null;
    private SoundLocate soundLocate = null;
    private WebOut webOut = null;

    public ConfigServer(String[] args) {
        options = new Options();
        options.addOption("help", "prints out the Help");
        options.addOption("f", "file", true, "specify the config file. default is \""+ filename +"\"");
        Logger.beginLog();

        try {
            CommandLine cmd = new DefaultParser().parse(options, args);
            if (cmd.hasOption("help")) {
                printHelp();
                Logger.log(ConfigServer.class, Stream.STD_OUT, "print help sucessfully");
                Logger.getInstance().close();
                System.exit(0);
            } else if (cmd.hasOption('f')) {
                filename = cmd.getOptionValue('f');
            }

            if (!new File(filename).canRead()) {
                Logger.log(ConfigServer.class, Stream.STD_ERR, "failed to Open config file");
                Logger.getInstance().close();
                System.exit(-42);
            }
        } catch (ParseException e) {
            Logger.log(ConfigServer.class, Stream.STD_ERR, "failed to parse Arguments!");
            printHelp();
            try {
                Logger.getInstance().close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            System.exit(-1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //argument parsing stage finished

        try {
            Toml toml = new Toml().read(new File(filename));
            config = toml.to(Config.class);
        } catch (Exception e) {
            Logger.log(e);
            System.exit(-123);
        }
        //config reading finished


        ProcessDiedListener processDiedListener = p -> {
            try {
                Logger.log(ConfigServer.class, Stream.STD_ERR, p + "s :( restarting everything...");
                Program.multiKill(soundInput, soundSimulate, soundFFT, soundLocate, webOut);
                startPrograms();
            } catch (IOException e) {
                Logger.log(e);
            }
        };
        Supervisor su = new Supervisor(processDiedListener);

        String positionFileName = "positions.csv";
        try {
            PrintWriter fw = new PrintWriter(new FileWriter(new File(positionFileName)));
            String out = "";
            for (int i = 0; i < config.general.micPositions.size(); i++) {
                out += config.general.micPositions.get(i);
                if (i % 3 == 2) {
                    out += "\n";
                } else {
                    out += ", ";
                }
            }
            fw.print(out);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            Logger.log(e);
        }

        positionFileName = new File(positionFileName).getAbsolutePath();

        soundInput = new SoundInput(su, config.general.samplerate, config.soundInput.deviceName, config.general.micPositions.size() / 3);
        fpgaInput = new FpgaInput(su, config.soundInput.ip, config.soundInput.port);
        soundSimulate = new SoundSimulate(su, config.general.samplerate, config.soundSimulate.soundFile.equals("") ? null : config.soundSimulate.soundFile, config.general.log ? config.general.logfileBaseName + "_simulate.log" : null, positionFileName);
        soundFFT = new SoundFFT(su, config.soundFFT.fftSize, config.general.samplerate, config.soundFFT.fftPerSec, config.soundFFT.windowingFunction, config.soundFFT.threshold);
        soundLocate = new SoundLocate(su, config.soundLocate.algorithms, config.soundLocate.accuracy, config.soundReduce.maxClusterSize, config.soundReduce.maxKeep, config.soundReduce.meanWindow, config.general.log ? config.general.logfileBaseName + "_locate.log" : null, positionFileName, config.soundReduce.dissimilarityFunction);
        webOut = new WebOut(config.general.micPositions);

        //created all the Objects
        try {
            startPrograms();
        } catch (IOException e) {
            Logger.log(e);
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                Logger.log(ConfigServer.class, Stream.STD_OUT, "shutting down...");
                Program.multiKill(soundLocate, soundFFT, soundSimulate, soundInput);
                Logger.log(ConfigServer.class, Stream.STD_OUT, "everything shut down successfully");
                try {
                    Logger.getInstance().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void main(String[] args) {
        new ConfigServer(args);
    }

    private void startPrograms() throws IOException{
        boolean real = config.general.real;
        Logger.log(ConfigServer.class, Stream.STD_OUT, "starting programs with real=" + real);
        Logger.log(ConfigServer.class, Stream.STD_OUT, "out port is: " + config.general.outPort);

        PortFactory pf = new PortFactory(49151);//magic constant
        int inToFft, inToNull, fftToLocate, locateToGui, locateToWs;
        inToFft = pf.getPort();
        inToNull = pf.getPort();
        fftToLocate = pf.getPort();
        locateToGui = pf.getPort();
        locateToWs = pf.getPort();

        Logger.log(ConfigServer.class, Stream.STD_OUT, "ports are: " +
                "{inToFFt: " + inToFft +

                ", inToNull: " + inToNull +
                ", fftToLocate: " + fftToLocate +
                ", locateToGui: " + locateToGui +
                ", locateToWs: " + locateToWs +
                "}");
        if (real) {
            if(config.soundInput.fpga) {
                fpgaInput.start(inToFft);
            } else {
                soundInput.start(inToFft);
            }
            soundSimulate.start(inToNull, locateToGui);
        }
        else {
            soundSimulate.start(inToFft, locateToGui);
        }
        waitInBetween();
        soundFFT.start(inToFft, fftToLocate);
        waitInBetween();
        soundLocate.start(fftToLocate, locateToGui, locateToWs);
        waitInBetween();
        webOut.start(locateToWs, config.general.outPort == 0 ? 8080 : config.general.outPort);
    }

    private void waitInBetween() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Logger.log(e);
        }
    }

    private void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("configServer", options);
    }
}
