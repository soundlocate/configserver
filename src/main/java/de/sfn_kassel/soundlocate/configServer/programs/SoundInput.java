package de.sfn_kassel.soundlocate.configServer.programs;

/**
 * Created by jaro on 29.03.16.
 */
public class SoundInput extends SupervisedProgram {
    public SoundInput(int port) {
        super("soundInput", "" + port);
    }
}
