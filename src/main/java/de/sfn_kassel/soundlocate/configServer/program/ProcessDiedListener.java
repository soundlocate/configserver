package de.sfn_kassel.soundlocate.configServer.program;

/**
 * Created by jaro on 29.03.16.
 * the Listener, that is called, when a process died
 */
public interface ProcessDiedListener {
    void onProcessDied(Process p);
}
