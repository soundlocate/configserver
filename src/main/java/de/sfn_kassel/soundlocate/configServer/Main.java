package de.sfn_kassel.soundlocate.configServer;

import com.moandjiezana.toml.Toml;
import org.apache.commons.cli.*;

import java.io.File;

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

        Toml toml = new Toml().read(filename);
    }

    public static void main(String[] args) {
        new Main(args);
    }

    private void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("configServer", options);
    }
}
