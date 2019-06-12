package Server;

import java.net.ServerSocket;
import java.util.HashMap;

public class Listeners {

    public static HashMap<Integer, ServerSocket> listeners = new HashMap<>();

    public static ServerSocket getListener(int port){
        if (!listeners.containsKey(port)) return null;
        return listeners.get(port);
    }

    public static void addListener(ServerSocket sock, int port){
        listeners.put(port, sock);
    }
}
