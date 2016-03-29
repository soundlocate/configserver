package de.sfn_kassel.soundlocate.configServer.programs;

import de.sfn_kassel.soundlocate.configServer.program.SupervisedProgram;

/**
 * Created by jaro on 29.03.16.
 * the class, that represents the SoundFFT program
 */
public class SoundFFT extends SupervisedProgram {
    public SoundFFT(int inPort, int outPort) {
        super("soundFFT", "localhost", "" + inPort, "" + outPort);
    }
}
