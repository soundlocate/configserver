package de.sfn_kassel.soundlocate.configServer.log;

import java.io.BufferedInputStream;
import java.io.IOException;

/**
 * Created by jaro on 29.03.16.
 */
public class LogThread extends Thread {
    private final BufferedInputStream std, err;
    private final String name;

    public LogThread(Process p, String name) {
        err = new BufferedInputStream(p.getErrorStream());
        std = new BufferedInputStream(p.getInputStream());
        this.name = name;
    }

    @Override
    public void run() {
        String errString = "";
        String stdString = "";
        while (true) {
            try {
                while (err.available() > 0) {
                    errString += (char) err.read();
                    if (errString.endsWith("\n")) {
                        Logger.getInstance().log(name, Stream.StdErr, errString.trim());
                        errString = "";
                    }
                }

                while (std.available() > 0) {
                    stdString += (char) std.read();
                    if (stdString.endsWith("\n")) {
                        Logger.getInstance().log(name, Stream.StdOut, stdString.trim());
                        stdString = "";
                    }
                }


            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}