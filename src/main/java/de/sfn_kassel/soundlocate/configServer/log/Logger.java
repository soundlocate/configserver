package de.sfn_kassel.soundlocate.configServer.log;

import de.sfn_kassel.soundlocate.configServer.ConfigServer;
import de.sfn_kassel.soundlocate.configServer.programs.SoundFFT;
import de.sfn_kassel.soundlocate.configServer.programs.SoundInput;
import de.sfn_kassel.soundlocate.configServer.programs.SoundLocate;
import de.sfn_kassel.soundlocate.configServer.programs.SoundSimulate;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jaro on 29.03.16.
 * the main Logger Class
 */
public class Logger implements Closeable {
    private static final String FILENAME = "soundlocate.log";
    private static Logger instance;
    private final SimpleDateFormat f;
    private PrintWriter out;
    private int alternate = 1;
    private boolean isWriting = false;

    private Logger(String logFileName) {
        f = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss.SSS");
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

    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger(FILENAME);
        }
        return instance;
    }

    public static void beginLog() {
        getInstance().println("<hr><table>");
    }

    private void internalLog(Class program, Stream stream, String msg) {
        String bcolor = (alternate++ % 2 == 0) ? "F3F3F3" : "FFFFFF";
        String output = "<tr style=\"width:100%; background-color:#" + bcolor + "\">" +
                "<td style=\"white-space: nowrap; background-color: " + (stream == Stream.STD_OUT ? "B6FFB0" : "FFB5B0") + "\">" + f.format(new Date()) + "</td>" +
                "<td style=\"white-space: nowrap\">" + getFormattedProgramName(program) + "</td>" +
                "<td>" + msg + "</td>" +
                "</tr>";
        println(output);
    }

    private synchronized void println(String s) {
        isWriting = true;
        System.out.println(s);
        out.println(s);
        isWriting = false;
    }

    private String getFormattedProgramName(Class program) {
        return "<span style=\"color: " + getProgramColor(program) + ";font-weight: bold;\">" + (program.equals(SoundFFT.class) ? "\t" : "") + getProgramName(program) + "</span>";
    }

    private String getProgramColor(Class program) {
        Map<String, String> colors = new HashMap<>();
        colors.put(getProgramName(ConfigServer.class), "#B200E0");
        colors.put(getProgramName(SoundInput.class), "#1359ED");
        colors.put(getProgramName(SoundSimulate.class), "#00C2BD");
        colors.put(getProgramName(SoundFFT.class), "#E39600");
        colors.put(getProgramName(SoundLocate.class), "#DEDD00");

        return colors.getOrDefault(getProgramName(program), "#000000");
    }

    private String getProgramName(Class program) {
        return program.getName().split("\\.")[program.getName().split("\\.").length - 1];
    }

    public void close() throws IOException {
        while (isWriting) ;
        println("</table>");
        out.close();
    }
}
