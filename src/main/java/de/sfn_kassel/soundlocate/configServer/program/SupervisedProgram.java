package de.sfn_kassel.soundlocate.configServer.program;

import de.sfn_kassel.soundlocate.configServer.ConfigServer;
import de.sfn_kassel.soundlocate.configServer.log.LogThread;
import de.sfn_kassel.soundlocate.configServer.log.Logger;
import de.sfn_kassel.soundlocate.configServer.log.Stream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jaro on 29.03.16.
 * the abstract Class, that represents an Supervised program
 * this Class is implements th glue logic for the Logger thread
 */

public abstract class SupervisedProgram implements Program {
    private final String[] baseProgram;
    private final Supervisor supervisor;
    private Process process;

    protected SupervisedProgram(Supervisor supervisor, String... command) {
        if (command[0].equals("java")) { //TODO: maybe better solution
            command[1] =  "bin/" + command[1];
        } else {
            command[0] = "bin/" + command[0];
        }
        baseProgram = command;
        this.supervisor = supervisor;
    }

    protected void start(String... args) throws IOException {
        List<String> programCall = new ArrayList<>();
        for (String s : baseProgram) {
            programCall.add(s);
        }
        programCall.addAll(Arrays.asList(args));

        for( String s : programCall) {
            System.out.print(s + " ");
        }

        ProcessBuilder processBuilderWithArgs = new ProcessBuilder(programCall);
        process = processBuilderWithArgs.start();
        new LogThread(process, this.getClass());
        supervisor.addProcess(process);
    }

    public void kill() {
        supervisor.removeProcess(process);
        process.destroy();
    }

    @Override
    public boolean isNull() {
        return this.process == null;
    }
}
