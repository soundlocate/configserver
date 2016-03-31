package de.sfn_kassel.soundlocate.configServer.programs;

import de.sfn_kassel.soundlocate.configServer.program.SupervisedProgram;
import de.sfn_kassel.soundlocate.configServer.program.Supervisor;

import java.io.IOException;

/**
 * Created by jaro on 29.03.16.
 * the class, that represents the SoundLocate program
 */
public class SoundLocate extends SupervisedProgram {
    public SoundLocate(Supervisor pd, String dataAlgorithms, double accuracy, double maxClusterSize, int maxKeep, int meanWindow, String logfile, String positionFile) {
        super(pd, "soundLocate", "-d " + dataAlgorithms, "-a " + accuracy, "-c " + maxClusterSize, "-k" + maxKeep, "-m " + meanWindow, logfile != null ? "-l " + logfile : "", "-p " + positionFile);
    }

    public void start(int inPort, int outPort, int wsPort) throws IOException {
        super.start("" + inPort, "localhost", "" + outPort, "" + wsPort);
    }
}
