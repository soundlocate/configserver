package de.sfn_kassel.soundlocate.configServer;

import com.moandjiezana.toml.Toml;
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

public class Main {
    final Options options;
    String filename = "config.toml";

    public Main(String[] args) {
        options = new Options();
        options.addOption("help", "prints out the Help");
        options.addOption("f", "file", true, "specify the config file. default is \"config.json\"");

        try {
            CommandLine cmd = new DefaultParser().parse(options, args);
            if (cmd.hasOption("help")) {
                printHelp();
                System.exit(0);
            } else if (cmd.hasOption('f')) {
                filename = cmd.getOptionValue('f');
            }

            if (!new File(filename).canRead()) {
                System.err.println("failed to Open config file");
                System.exit(-42);
            }
        } catch (ParseException e) {
            System.err.println("failed to parse Arguments!");
            printHelp();
            System.exit(-1);
        }
        //argument parsing stage finished
        Toml toml = null;
        try {
            toml = new Toml().read(new File(filename));
        } catch (Exception e) {
            Logger.getInstance().log(e);
            System.exit(-123);
        }
        boolean real = toml.getBoolean("general.real", false);

        Logger.getInstance().log("configServer", Stream.StdOut, "starting programs with real=" + real);

        PortFactory pf = new PortFactory(49151);//magic constant
        int inToFft, inToNull, fftToLocate, locateToGui, locateToWs;
        inToFft = pf.getPort();
        inToNull = pf.getPort();
        fftToLocate = pf.getPort();
        locateToGui = pf.getPort();
        locateToWs = pf.getPort();

        Logger.getInstance().log("configServer", Stream.StdOut, "ports are: " +
                "{inToFFt: " + inToFft +
                ", inToNull: " + inToNull +
                ", fftTolocate: " + fftToLocate +
                ", locateToGui: " + locateToGui +
                ", locateToWs: " + locateToWs +
                "}");

        SoundInput soundInput = new SoundInput(real ? inToFft : inToNull);
        SoundSimulate soundSimulate = new SoundSimulate(real ? inToNull : inToFft, locateToGui);
        SoundFFT soundFFT = new SoundFFT(inToFft, fftToLocate);
        SoundLocate soundLocate = new SoundLocate(fftToLocate, locateToGui, locateToWs);

        ProcessDiedListener pd = new ProcessDiedListener() {
            @Override
            public void onProcessDied(Process p) {
                try {
                    Logger.getInstance().log("configServer", Stream.StdErr, p + "died :( restarting everything...");
                    Program.multiRestart(this, soundInput, soundSimulate, soundFFT, soundLocate);
                } catch (IOException e) {
                    Logger.getInstance().log(e);
                }
            }
        };

        try {
            Program.multiStart(pd, soundInput, soundSimulate, soundFFT, soundLocate);
        } catch (IOException e) {
            Logger.getInstance().log(e);
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    Logger.getInstance().log("configServer", Stream.StdOut, "shutting down...");
                    Program.multiKill(soundLocate, soundFFT, soundSimulate, soundInput);
                    Logger.getInstance().log("configServer", Stream.StdOut, "everything shuted down sucessfully");
                } catch (IOException e) {
                    Logger.getInstance().log(e);
                }
            }
        });
    }


    public static void main(String[] args) {
        new Main(args);
    }

    private void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("configServer", options);
    }
}
