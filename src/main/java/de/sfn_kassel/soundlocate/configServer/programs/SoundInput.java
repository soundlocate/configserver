package de.sfn_kassel.soundlocate.configServer.programs;

import de.sfn_kassel.soundlocate.configServer.program.SupervisedProgram;

/**
 * Created by jaro on 29.03.16.
 */
public class SoundInput extends SupervisedProgram {
    public SoundInput(int port) {
        super("soundInput", "" + port);
    }
}
