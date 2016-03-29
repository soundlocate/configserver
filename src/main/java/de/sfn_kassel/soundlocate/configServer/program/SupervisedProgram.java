package de.sfn_kassel.soundlocate.configServer.program;

import de.sfn_kassel.soundlocate.configServer.log.LogThread;

import java.io.IOException;

/**
 * Created by jaro on 29.03.16.
 * the abstract Class, that represents an Supervised program
 * this Class is implements th glue logic for the Logger thread
 */

public abstract class SupervisedProgram implements Program {
    private final ProcessBuilder processBuilder;
    private Process process;
    private LogThread logThread;

    protected SupervisedProgram(String... command) {
        command[0] = "bin/" + command[0];
        processBuilder = new ProcessBuilder(command);
    }

    public void start(ProcessDiedListener pd) throws IOException {
        process = processBuilder.start();
        logThread = new LogThread(process, this.getClass(), pd);
        logThread.start();
    }

    public void kill() {
        process.destroy();
        logThread.interrupt();
    }
}
