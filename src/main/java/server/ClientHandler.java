package server;

import gui.Controller;
import gui.components.NotificationView;
import javafx.application.Platform;
import logger.Level;
import logger.Logger;
import server.data.CryptoUtils;
import server.data.PseudoBase;
import server.data.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientHandler implements Runnable, Repository {
    private Socket socket;
    private ClientObject client;
    private String ip;

    ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            ip = (((InetSocketAddress) Server.getClient().getRemoteSocketAddress()).getAddress()).toString().replace("/", "");
            /* Check to ensure there's room left via Max Connections setting. */
            if (CONNECTIONS.size() < KumoSettings.MAX_CONNECTIONS) {
                if (!CONNECTIONS.containsKey(ip)) {
                    if(PseudoBase.getKumoData().containsKey(ip)){
                            client = new ClientObject(socket, PseudoBase.getKumoData().get(ip).getNickName(), ip);
                        } else {
                        //client = new ClientObject(socket, "kumo Machine " + (PseudoBase.getKumoData().size() + 1), ip);
                        client = new ClientObject(socket, CryptoUtils.randTextAlpha(12), ip);
                    }
                } else {
                    client = new ClientObject(socket, CONNECTIONS.get(ip).getNickName(), ip);
                }
                Platform.runLater(() -> CONNECTIONS.put(ip, client));
                Platform.runLater(() -> PseudoBase.getKumoData().put(ip, client));
            }
            Controller.updateStats();
            /* Notification on new client connection */
            if(KumoSettings.SHOW_NOTIFICATIONS) {
                Platform.runLater(() -> NotificationView.openNotification(client));
            }
            Controller.updateTable();
            InputStream is = client.getClient().getInputStream();
            try {
                /* Begin listening for client commands via ProcessCommands */
                ProcessCommands.processCommands(is, client);
            } catch (IOException s){ // Client exits somehow. Could improve py doing specific things for specific errors (SocketError, EOFException)
                Platform.runLater(() -> CONNECTIONS.remove(ip, client));
                Logger.log(Level.WARNING, "Lost client " + client.getNickName() + '.' + " Removing from connections list.");
                Platform.runLater(() -> PseudoBase.getKumoData().remove(ip, client));
                Controller.updateStats();
                Controller.updateTable();
                s.printStackTrace();

            } // EOFException = Client CTRL+C

        } catch (IOException e) {
            client.setOnlineStatus("offline");
            e.printStackTrace();
        }
    }
}