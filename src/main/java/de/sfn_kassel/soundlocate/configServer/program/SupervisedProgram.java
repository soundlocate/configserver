package de.sfn_kassel.soundlocate.configServer.program;

import de.sfn_kassel.soundlocate.configServer.log.LogThread;

import java.io.IOException;

/**
 * Created by jaro on 29.03.16.
 */

public class SupervisedProgram implements Program {
    private final ProcessBuilder processBuilder;
    private Process process;
    private LogThread logThread;

    public SupervisedProgram(String... command) {
        command[0] = "bin/" + command[0];
        processBuilder = new ProcessBuilder(command);
    }

    public void start(ProcessDiedListener pd) throws IOException {
        process = processBuilder.start();
        logThread = new LogThread(process, processBuilder.command().get(0), pd);
        logThread.start();
    }

    public void kill() {
        process.destroy();
        logThread.interrupt();
    }
}
