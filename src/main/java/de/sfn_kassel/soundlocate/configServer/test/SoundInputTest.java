package de.sfn_kassel.soundlocate.configServer.test;

import de.sfn_kassel.soundlocate.configServer.program.ProcessDiedListener;
import de.sfn_kassel.soundlocate.configServer.programs.SoundInput;

import java.io.IOException;

/**
 * Created by jaro on 29.03.16.
 * a Test class for the SoundInput Class
 */
class SoundInputTest {
    public static void main(String[] args) throws IOException, InterruptedException {
        SoundInput si = new SoundInput(1234);
        ProcessDiedListener pd = p -> System.out.println(p + "died :(");
        si.start(pd);
        Thread.sleep(10000);
    }
}