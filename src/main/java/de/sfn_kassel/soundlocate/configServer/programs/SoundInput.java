package de.sfn_kassel.soundlocate.configServer.programs;

import de.sfn_kassel.soundlocate.configServer.program.ProcessDiedListener;
import de.sfn_kassel.soundlocate.configServer.program.SupervisedProgram;
import de.sfn_kassel.soundlocate.configServer.program.Supervisor;

import java.io.IOException;

/**
 * Created by jaro on 29.03.16.
 * the class, that represents the SoundInput program
 */
public class SoundInput extends SupervisedProgram {
    public SoundInput(Supervisor pd) {
        super(pd, "java", "-jar", "soundInput.jar");
    }

    public void start(int port) throws IOException {
        super.start("" + port);
    }
}
