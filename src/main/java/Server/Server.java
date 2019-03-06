package Server;

import Logger.Level;
import Logger.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {
    private static Socket client;

    public static Socket getClient() {
        return client;
    }

    private void startServer() {
        startServer(KumoSettings.PORT);
    }

    private void startServer(int port) {
        while (true) {
            try {
                ServerSocket server = new ServerSocket(port);
                client = server.accept();
                Runnable clientHandler = new ClientHandler(client);
                new Thread(clientHandler).start();
                Logger.log(Level.INFO, "Listening server started on " + KumoSettings.CONNECTION_IP + ":" + port);
            } catch (IOException ignored) {
            }
        }
    }

    @Override
    public void run() {
        startServer();
    }
}