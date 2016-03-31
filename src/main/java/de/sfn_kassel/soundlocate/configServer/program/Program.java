package de.sfn_kassel.soundlocate.configServer.program;

import java.io.IOException;

/**
 * Created by jaro on 29.03.16.
 * the abstract interface for representing a Program and its lifecycle
 */
public interface Program {
    static void multiKill(Program... ps) {
        Program[] pa = new Program[ps.length];
        for (int i = 0; i < ps.length; i++) {
            pa[ps.length - i - 1] = ps[i];
        }

        for (Program p : pa) {
            if (p.isNull())
                continue;
            p.kill();
        }
    }

    //void start(ProcessDiedListener p, ...) throws IOException; //TODO: find a solution
    void kill();
    boolean isNull();
}
