package de.sfn_kassel.soundlocate.configServer;

import com.moandjiezana.toml.Toml;
import de.sfn_kassel.soundlocate.configServer.config.Config;
import de.sfn_kassel.soundlocate.configServer.log.Logger;
import de.sfn_kassel.soundlocate.configServer.log.Stream;
import de.sfn_kassel.soundlocate.configServer.program.ProcessDiedListener;
import de.sfn_kassel.soundlocate.configServer.program.Program;
import de.sfn_kassel.soundlocate.configServer.programs.SoundFFT;
import de.sfn_kassel.soundlocate.configServer.programs.SoundInput;
import de.sfn_kassel.soundlocate.configServer.programs.SoundLocate;
import de.sfn_kassel.soundlocate.configServer.programs.SoundSimulate;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;

/**
 * Created by jaro on 29.03.16.
 * the main Class of the ConfigServer
 */

public class ConfigServer {
    private final Options options;
    private String filename = "config.toml";
    private static Config config;

    private final SoundInput soundInput;
    private final SoundSimulate soundSimulate;
    private final SoundFFT soundFFT;
    private final SoundLocate soundLocate;

    public static void main(String[] args) {
        new ConfigServer(args);
    }


    public ConfigServer(String[] args) {
        options = new Options();
        options.addOption("help", "prints out the Help");
        options.addOption("f", "file", true, "specify the config file. default is \""+ filename +"\"");

        try {
            CommandLine cmd = new DefaultParser().parse(options, args);
            if (cmd.hasOption("help")) {
                printHelp();
                System.exit(0);
            } else if (cmd.hasOption('f')) {
                filename = cmd.getOptionValue('f');
            }

            if (!new File(filename).canRead()) {
                Logger.log(ConfigServer.class, Stream.STD_ERR, "failed to Open config file");
                System.exit(-42);
            }
        } catch (ParseException e) {
            Logger.log(ConfigServer.class, Stream.STD_ERR, "failed to parse Arguments!");
            printHelp();
            System.exit(-1);
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
                Program.multiKill();
                startPrograms();
            } catch (IOException e) {
                Logger.log(e);
            }
        };
        soundInput = new SoundInput(processDiedListener);
        soundSimulate = new SoundSimulate(processDiedListener);
        soundFFT = new SoundFFT(processDiedListener);
        soundLocate = new SoundLocate(processDiedListener);
        //created all the Objects

        try {
            startPrograms();
        } catch (IOException e) {
            Logger.log(e);
        }

        Runtime.getRuntime().addShutdownHook(new Thread() { //TODO(jaro) remove pointless shutdown error log messages -> shuld be removed now
            @Override
            public void run() {
                Logger.log(ConfigServer.class, Stream.STD_OUT, "shutting down...");
                Program.multiKill(soundLocate, soundFFT, soundSimulate, soundInput);
                Logger.log(ConfigServer.class, Stream.STD_OUT, "everything shut down successfully");
            }
        });
    }

    private void startPrograms() throws IOException{
        boolean real = config.general.real;
        Logger.log(ConfigServer.class, Stream.STD_OUT, "starting programs with real=" + real);

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
            soundInput.start(inToFft);
            soundSimulate.start(inToNull, locateToGui);
        }
        else {
            soundSimulate.start(inToFft, locateToGui);
        }
        soundFFT.start(inToFft, fftToLocate);
        soundLocate.start(fftToLocate, locateToGui, locateToWs);
    }

    private void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("configServer", options);
    }
}
