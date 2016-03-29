package de.sfn_kassel.soundlocate.configServer;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by jaro on 29.03.16.
 */
public class PortFactory {
    private int now;

    public PortFactory(int lowest) {
        this.now = lowest;
    }

    public int getPort() {
        while (!portUsable(now)) {
            now++;
        }
        now++;
        return now - 1;
    }

    private boolean portUsable(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            serverSocket.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
