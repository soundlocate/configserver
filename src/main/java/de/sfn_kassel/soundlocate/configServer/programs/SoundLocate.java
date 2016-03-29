package de.sfn_kassel.soundlocate.configServer.programs;

import de.sfn_kassel.soundlocate.configServer.program.SupervisedProgram;

/**
 * Created by jaro on 29.03.16.
 * the class, that represents the SoundLocate program
 */
public class SoundLocate extends SupervisedProgram {
    public SoundLocate(int inPort, int outPort, int wsPort) {
        super("soundLocate", "localhost", "" + inPort, "localhost", "" + outPort, "" + wsPort);
    }
}
