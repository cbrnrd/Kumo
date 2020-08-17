package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;

public class Listeners {

    protected static HashMap<Integer, ServerSocket> listeners = new HashMap<>();

    public static ServerSocket getListener(int port){
        if (!listeners.containsKey(port)) return null;
        return listeners.get(port);
    }

    public static void addListener(ServerSocket sock, int port){
        listeners.put(port, sock);
    }

    public static boolean stopListener(int port) throws IOException {
        listeners.get(port).close();
        return listeners.get(port).isClosed();
    }
}
