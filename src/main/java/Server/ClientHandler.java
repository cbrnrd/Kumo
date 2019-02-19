package Server;

import GUI.Components.NotificationView;
import GUI.Controller;
import Logger.Level;
import Logger.Logger;
import Server.Data.CryptoUtils;
import Server.Data.PseudoBase;
import Server.Data.Repository;
import javafx.application.Platform;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

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
                        //client = new ClientObject(socket, "KUMO Machine " + (PseudoBase.getKumoData().size() + 1), ip);
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
            InputStream is = client.getClient().getInputStream();
            try {
                /* Begin listening for client commands via ProcessCommands */
                ProcessCommands.processCommands(is, client);
            } catch (SocketException s){
                Platform.runLater(() -> CONNECTIONS.remove(ip, client));
                Platform.runLater(() -> PseudoBase.getKumoData().remove(ip, client));
                Controller.updateStats();
                Controller.updateTable();
                Logger.log(Level.INFO, "New client! " + ip + " :: " + client.getNickName() + " :: " + client.getSYSTEM_OS());
                s.printStackTrace();
            }
        } catch (IOException e) {
            client.setOnlineStatus("offline");
            e.printStackTrace();
        }
    }
}