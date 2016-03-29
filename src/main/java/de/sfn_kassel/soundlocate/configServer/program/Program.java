package de.sfn_kassel.soundlocate.configServer.program;

import java.io.IOException;

/**
 * Created by jaro on 29.03.16.
 * the abstract interface for representing a Program and its lifecycle
 */
public interface Program {
    static void multiStart(ProcessDiedListener pd, Program... ps) throws IOException {
        for (Program p : ps) {
            p.start(pd);
        }
    }

    static void multiKill(Program... ps) {
        for (Program p : ps) {
            p.kill();
        }
    }

    static void multiRestart(ProcessDiedListener pd, Program... ps) throws IOException {
        multiKill(ps);
        Program[] pa = new Program[ps.length];
        for (int i = 0; i < ps.length; i++) {
            pa[ps.length - i - 1] = ps[i];
        }
        multiStart(pd, pa);
    }

    void start(ProcessDiedListener p) throws IOException;

    void kill();

    default void restart(ProcessDiedListener pd) throws IOException {
        kill();
        start(pd);
    }
}
