package de.sfn_kassel.soundlocate.configServer.programs;

import de.sfn_kassel.soundlocate.configServer.program.SupervisedProgram;

/**
 * Created by jaro on 29.03.16.
 * the class, that represents the SoundSimulate program
 */
public class SoundSimulate extends SupervisedProgram {
    public SoundSimulate(int outPort, int inPort) {
        super("soundSimulate", "" + inPort, "" + outPort);
    }
}
