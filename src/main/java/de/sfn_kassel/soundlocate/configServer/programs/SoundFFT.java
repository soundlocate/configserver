package de.sfn_kassel.soundlocate.configServer.programs;

import de.sfn_kassel.soundlocate.configServer.program.ProcessDiedListener;
import de.sfn_kassel.soundlocate.configServer.program.SupervisedProgram;
import de.sfn_kassel.soundlocate.configServer.program.Supervisor;

import java.io.IOException;

/**
 * Created by jaro on 29.03.16.
 * the class, that represents the SoundFFT program
 */
public class SoundFFT extends SupervisedProgram {
    public SoundFFT(Supervisor pd) {
        super(pd, "soundFFT");
    }

    public void start(int inPort, int outPort) throws IOException {
        super.start("localhost", "" + inPort, "" + outPort);
    }
}
