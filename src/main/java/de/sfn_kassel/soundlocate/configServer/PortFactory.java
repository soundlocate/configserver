package de.sfn_kassel.soundlocate.configServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Random;

/**
 * Created by jaro on 29.03.16.
 * a Class that can give you free TCP/IP ports
 */
public class PortFactory {
    private int now;

    public PortFactory(int lowest) {
        this.now = lowest;
    }

    public int getPort() {
        return (short)(Math.random() * (Short.MAX_VALUE - 1024) + 1024);
    }

    private boolean portUsable(int port) {
        try {
            ServerSocket testServerSocket = new ServerSocket(port);
            testServerSocket.setReuseAddress(true); //maybe fix
            testServerSocket.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
