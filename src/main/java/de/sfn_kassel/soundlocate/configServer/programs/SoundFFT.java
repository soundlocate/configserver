package de.sfn_kassel.soundlocate.configServer.programs;

import de.sfn_kassel.soundlocate.configServer.program.SupervisedProgram;
import de.sfn_kassel.soundlocate.configServer.program.Supervisor;

import java.io.IOException;

/**
 * Created by jaro on 29.03.16.
 * the class, that represents the SoundFFT program
 */
public class SoundFFT extends SupervisedProgram {
    public SoundFFT(Supervisor pd, int fftSize, int samplerate, int fftPerSec, String windowingFunction, double threshold) {
        super(pd, "soundFFT", "-f", "" + fftSize, "-s", "" + samplerate, "-r", "" + fftPerSec, "-w", "" + windowingFunction, "-t", "" + threshold);
    }

    public void start(int inPort, int outPort) throws IOException {
        super.start("localhost", "" + inPort, "" + outPort);
    }
}
