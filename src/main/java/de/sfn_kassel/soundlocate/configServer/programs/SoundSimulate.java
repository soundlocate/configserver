package de.sfn_kassel.soundlocate.configServer.programs;

import de.sfn_kassel.soundlocate.configServer.program.ProcessDiedListener;
import de.sfn_kassel.soundlocate.configServer.program.SupervisedProgram;

import java.io.IOException;

/**
 * Created by jaro on 29.03.16.
 * the class, that represents the SoundSimulate program
 */
public class SoundSimulate extends SupervisedProgram {
    public SoundSimulate(ProcessDiedListener pd) {
        super(pd, "soundSimulate");
    }

    public void start(int outPort, int inPort) throws IOException {
        super.start("" + outPort, "" + inPort);
    }
}
