package de.sfn_kassel.soundlocate.configServer.log;

import de.sfn_kassel.soundlocate.configServer.ConfigServer;

import java.io.*;

/**
 * Created by jaro on 29.03.16.
 * the main Logger Class
 */
public class Logger implements Closeable {
    private static final String FILENAME = "soundlocate.log";
    private static Logger instance;
    private PrintWriter out;

    private Logger(String logFileName) {
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(logFileName, true)));
        } catch (FileNotFoundException e) {
            log(e);
            System.exit(-1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void log(Class program, Stream stream, String msg) {
        getInstance().internalLog(program, stream, msg);
    }

    public static void log(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);

        getInstance().internalLog(ConfigServer.class, Stream.STD_ERR, sw.toString().replace("\n", "\t"));
    }

    private static Logger getInstance() {
        if (instance == null) {
            instance = new Logger(FILENAME);
        }
        return instance;
    }

    private synchronized void internalLog(Class program, Stream stream, String msg) {
        char cStream = stream == Stream.STD_OUT ? 'o' : 'e';
        String output = cStream + " " + System.currentTimeMillis() + " " + program.getName().split("\\.")[program.getName().split("\\.").length - 1] + "\t" + msg;
        System.out.println(output);
        out.println(output);
    }

    public void close() throws IOException {
        out.flush();
        out.close();
    }
}
