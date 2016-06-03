package de.sfn_kassel.soundlocate.configServer.programs;

import de.sfn_kassel.soundlocate.configServer.program.SupervisedProgram;
import de.sfn_kassel.soundlocate.configServer.program.Supervisor;

import java.io.IOException;

/**
 * Created by robin on 25.05.16.
 * a wrapper around the fpga bridge and normalizer script
 */
public class FpgaInput extends SupervisedProgram{
        public FpgaInput(Supervisor pd, String ip, String port) {
            super(pd, "fpga.sh", ip, "" + port);
        }

        public void start(int port) throws IOException {
            super.start("" + port);
        }
}
