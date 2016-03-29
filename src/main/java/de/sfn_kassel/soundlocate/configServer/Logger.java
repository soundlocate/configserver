package de.sfn_kassel.soundlocate.configServer;

import java.io.*;

/**
 * Created by jaro on 29.03.16.
 */
public class Logger implements Closeable {
    private static String FILENAME = "soundlocate.log";
    private static Logger instance;
    private final PrintStream out;

    private Logger(String logFileName) throws FileNotFoundException {
        File logFile = new File(logFileName);
        out = new PrintStream(new FileOutputStream(logFile));
    }

    public static Logger getInstance() throws FileNotFoundException {
        if (instance == null) {
            instance = new Logger(FILENAME);
        }
        return instance;
    }

    public void log(String programm, Stream stream, String msg) {
        char cStream = stream == Stream.StdOut ? 'o' : 'e';
        out.println(cStream + " " + System.currentTimeMillis() + " " + programm + "\t" + msg);
    }

    public void close() throws IOException {
        out.flush();
        out.close();
    }
}
