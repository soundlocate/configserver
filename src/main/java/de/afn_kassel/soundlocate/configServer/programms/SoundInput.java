package de.afn_kassel.soundlocate.configServer.programms;

import java.io.File;
import java.io.IOException;

/**
 * Created by jaro on 29.03.16.
 */

public class SoundInput implements Programm {
    private final ProcessBuilder processBuilder;
    private Process process;

    public SoundInput(short outputPort) {
        processBuilder = new ProcessBuilder("soundInput", "" + outputPort);
        processBuilder.directory(new File("bin"));
    }

    public void start() throws IOException {
        process = processBuilder.start();
    }

    public void kill() {
        process.destroy();
    }
}
