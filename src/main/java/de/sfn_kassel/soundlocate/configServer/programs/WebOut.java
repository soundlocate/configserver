package de.sfn_kassel.soundlocate.configServer.programs;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import de.sfn_kassel.soundlocate.configServer.log.Logger;
import de.sfn_kassel.soundlocate.configServer.log.Stream;
import de.sfn_kassel.soundlocate.configServer.program.Program;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.List;

/**
 * Created by jaro on 04.04.16.
 * a Server for the external WebOut
 */
public class WebOut implements Program {
    public final List<Double> mics;
    private HttpServer server;
    private HttpHandler fileHandler;

    public WebOut(List<Double> mics) {
        this.mics = mics;
    }

    public void start(int wsPort, int outPort) {
        fileHandler = httpExchange -> {
            //    byte[] bytes = "something went wrong...".getBytes();
            //    httpExchange.sendResponseHeaders(500, bytes.length);
            byte[] bytes;
            try {
                File f = null;
                System.out.println(httpExchange.getRequestURI().getPath());
                if (httpExchange.getRequestURI().getPath().equals("/")) {
                    f = new File("bin/webOut/index.html");
                } else {
                    f = new File("bin/webOut/" + httpExchange.getRequestURI().getPath());
                }
                bytes = Files.readAllBytes(f.toPath());
                if (httpExchange.getRequestURI().getPath().endsWith(".js")) {
                    String micString = "";
                    for (int i = 0; i < mics.size(); i++) {
                        micString += mics.get(i);
                        if (i % 3 == 2) {
                            micString += "\n";
                        } else {
                            micString += ", ";
                        }
                    }

                    bytes = new String(bytes)
                            .replace("/*@JAVA_PORT@*/", InetAddress.getLocalHost().getHostAddress() + ":" + wsPort)
                            .replace("/*@JAVA_MICS@*/", micString)
                            .getBytes();

                }
                httpExchange.sendResponseHeaders(200, bytes.length);
            } catch (NoSuchFileException e) {
                bytes = "404 - file not found".getBytes();
                httpExchange.sendResponseHeaders(404, bytes.length);
            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                bytes = sw.toString().getBytes();
                httpExchange.sendResponseHeaders(500, bytes.length);
            }
            OutputStream os = httpExchange.getResponseBody();
            os.write(bytes);
            os.close();
        };

        try {
            server = HttpServer.create(new InetSocketAddress(outPort), 0);
            server.createContext("/", fileHandler);
            server.start();
        } catch (IOException e) {
            Logger.log(WebOut.class, Stream.STD_ERR, "failed to start server :(");
            Logger.log(e, WebOut.class);
        }
    }

    String position(double x, double y, double z) {
        return "new THREE.Vector3(" + x + ", " + y + ", " + z + ")";
    }

    @Override
    public void kill() {
        server.stop(0);
    }

    @Override
    public boolean isNull() {
        return false;
    }
}
