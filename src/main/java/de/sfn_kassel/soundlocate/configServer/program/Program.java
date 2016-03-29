package de.sfn_kassel.soundlocate.configServer.program;

import java.io.IOException;

/**
 * Created by jaro on 29.03.16.
 */
public interface Program {
    void start() throws IOException;

    void kill();
}
