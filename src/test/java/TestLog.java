import de.sfn_kassel.soundlocate.configServer.Logger;
import de.sfn_kassel.soundlocate.configServer.Stream;

import java.io.FileNotFoundException;

/**
 * Created by jaro on 29.03.16.
 */

public class TestLog {
    public static void main(String[] args) throws FileNotFoundException {
        Logger.getInstance().log("SoundInput", Stream.StdOut, "device found :)");
    }
}
