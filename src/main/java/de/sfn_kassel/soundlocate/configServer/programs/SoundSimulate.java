package de.sfn_kassel.soundlocate.configServer.programs;

import de.sfn_kassel.soundlocate.configServer.program.SupervisedProgram;
import de.sfn_kassel.soundlocate.configServer.program.Supervisor;

import java.io.IOException;

/**
 * Created by jaro on 29.03.16.
 * the class, that represents the SoundSimulate program
 */
public class SoundSimulate extends SupervisedProgram {
    public SoundSimulate(Supervisor pd, int samplerate, String soundFile, String logfile, String positionfile) {
        super(pd, "soundSimulate", "-s " + samplerate, "-f " + soundFile, logfile != null ? "-l " + logfile : "", "-p " + positionfile);
    }

    public void start(int outPort, int inPort) throws IOException {
        super.start("" + outPort, "" + inPort);
    }
}
