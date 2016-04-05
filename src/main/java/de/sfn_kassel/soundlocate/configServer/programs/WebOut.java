package de.sfn_kassel.soundlocate.configServer.programs;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import de.sfn_kassel.soundlocate.configServer.log.Logger;
import de.sfn_kassel.soundlocate.configServer.log.Stream;
import de.sfn_kassel.soundlocate.configServer.program.Program;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;

/**
 * Created by jaro on 04.04.16.
 * a Server for the external WebOut
 */
public class WebOut implements Program {
    private HttpServer server;
    private HttpHandler fileHandler;

    public void start(int wsPort) {
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
                    String oben = position(0, 0, 47.63139722);
                    String mo1 = position(33.5272943, 29.87619793, 15.8771324);
                    String mo2 = position(9.10989924, -43.97358755, 15.8771324);
                    String mo3 = position(-42.63719352, 14.09738964, -15.8771324);
                    String mu1 = position(42.63719354, -14.09738964, -15.8771324);
                    String mu2 = position(-29.87619793, -33.5272943, -15.8771324);
                    String mu3 = position(-9.10989922, 43.97358756, -15.8771324);
                    String unten = position(0, 0, -47.63139722);

                    String mics = "";

                    mics += unten;
                    mics += ",";
                    mics += mu1;
                    mics += ",\n";

                    mics += unten;
                    mics += ",";
                    mics += mu2;
                    mics += ",\n";

                    mics += unten;
                    mics += ",";
                    mics += mu3;
                    mics += ",\n";

                    mics += mo1;
                    mics += ",";
                    mics += oben;
                    mics += ",\n";

                    mics += mo2;
                    mics += ",";
                    mics += oben;
                    mics += ",\n";

                    mics += mo3;
                    mics += ",";
                    mics += oben;
                    mics += ",\n";

                    mics += mu1;
                    mics += ",";
                    mics += mo2;
                    mics += ",\n";

                    mics += mu1;
                    mics += ",";
                    mics += mo3;
                    mics += ",\n";

                    mics += mu2;
                    mics += ",";
                    mics += mo1;
                    mics += ",\n";

                    mics += mu2;
                    mics += ",";
                    mics += mo3;
                    mics += ",\n";

                    mics += mu3;
                    mics += ",";
                    mics += mo1;
                    mics += ",\n";

                    mics += mu3;
                    mics += ",";
                    mics += mo2;
                    mics += ",\n";

                    bytes = new String(bytes)
                            .replace("/*@JAVA_PORT@*/", "localhost:" + wsPort)
                            .replace("/*@JAVA_MICS@*/", mics)
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
            server = HttpServer.create(new InetSocketAddress(8080), 0);
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
