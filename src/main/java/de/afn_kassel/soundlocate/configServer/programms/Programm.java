package de.afn_kassel.soundlocate.configServer.programms;

import java.io.IOException;

/**
 * Created by jaro on 29.03.16.
 */
public interface Programm {
    void start() throws IOException;

    void kill();
}
