package de.sfn_kassel.soundlocate.configServer.test;

import de.sfn_kassel.soundlocate.configServer.programs.SoundInput;

import java.io.IOException;

/**
 * Created by jaro on 29.03.16.
 */
class SoundInputTest {
    public static void main(String[] args) throws IOException, InterruptedException {
        SoundInput si = new SoundInput(1234);
        si.start();
        Thread.sleep(10000);
    }
}