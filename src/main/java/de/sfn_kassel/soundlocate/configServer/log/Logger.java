package de.sfn_kassel.soundlocate.configServer.log;

import java.io.*;

/**
 * Created by jaro on 29.03.16.
 */
public class Logger implements Closeable {
    private static String FILENAME = "soundlocate.log";
    private static Logger instance;
    private PrintWriter out;

    private Logger(String logFileName) {
        File logFile = new File(logFileName);
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(logFileName, true)));
        } catch (FileNotFoundException e) {
            log(e);
            System.exit(-1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger(FILENAME);
        }
        return instance;
    }

    public synchronized void log(String programm, Stream stream, String msg) {
        char cStream = stream == Stream.StdOut ? 'o' : 'e';
        String output = cStream + " " + System.currentTimeMillis() + " " + programm + "\t" + msg;
        System.out.println(output);
        out.println(output);
    }

    public synchronized void log(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);

        char cStream = 'e';
        String output = cStream + " " + System.currentTimeMillis() + " " + "configServer" + "\t" + sw.toString().replace("\n", "\t");
        System.out.println(output);
        out.println(output);
    }

    public void close() throws IOException {
        out.flush();
        out.close();
    }
}
