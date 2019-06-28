package Server;


import GUI.Controller;
import Logger.Level;
import Logger.Logger;
import Server.Data.CryptoUtils;
import Server.Data.NetUtils;
import Server.Data.Repository;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.io.*;
import java.net.Socket;

public class ClientObject implements Serializable, Repository {
    private String SYSTEM_OS;
    transient private Socket client = new Socket();
    private String onlineStatus = client.isConnected() ? "Online" : "Offline";
    private String nickName;
    private String IP;
    private String uname;
    private String countryCode = "Fetching...";
    private transient PrintWriter clientOutput;
    private transient DataOutputStream dis;

    ClientObject(Socket client, String nickName, String IP) {
        this.client = client;
        this.nickName = nickName;
        this.IP = IP;
        new Thread(() -> {
            this.countryCode = NetUtils.sendHttpRequest("http://api.hostip.info/country.php?ip="+IP);
            if (countryCode.equals("")) countryCode = "N/A"; // eg localhost, 127.0.0.1, 192.168.1.1, etc
            //countryCodeImage = new CustomImage(new ImageView("https://www.countryflags.io/" + countryCode + "/flat/32.png"));
            updateStatus();
        }, "CountryCodeFetchThread").start();

        try {
            this.clientOutput = new PrintWriter(client.getOutputStream(), true);
            dis = new DataOutputStream(client.getOutputStream());
            clientCommunicate("SYS");
        } catch (IOException e) {
            Logger.log(Level.WARNING, "Exception thrown: " + e);
        }
        CONNECTIONS.put(IP, this);
        Timeline fiveSecondTime = new Timeline(new KeyFrame(Duration.millis(1000), event -> updateStatus()));
        fiveSecondTime.setCycleCount(Timeline.INDEFINITE);
        fiveSecondTime.play();
    }

    private void updateStatus() {
        String oldStatus = onlineStatus;
        onlineStatus = client.isConnected() ? "Online" : "Offline";
        if(!oldStatus.equals(onlineStatus)){
            try {
                Controller.updateTable();
            } catch (Exception ignored){}
        }
        if(onlineStatus.equals("Offline")){
            CONNECTIONS.remove(getIP());
        }
    }

    public String getSYSTEM_OS() {
        return SYSTEM_OS;
    }

    public void setSYSTEM_OS(String SYSTEM_OS) {
        this.SYSTEM_OS = SYSTEM_OS;
    }

    public String getUname() { return uname; }

    public void setUname(String uname) { this.uname = uname; }

    public Socket getClient() {
        return client;
    }

    public void setClient(Socket client) {
        this.client = client;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getIP() {
        return IP;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public String getCountryCode() { return countryCode; }

    public void setOnlineStatus(String status) {
        onlineStatus = status;
    }

    public void serialize() {
        final File parent = new File(System.getProperty("user.home") + "/Kumo/clients");
        if (!parent.mkdirs()) {
            Logger.log(Level.WARNING, "Unable to make necessary directories, may already exist.");
        }
        if (getIP() != null) {
            try {
                FileOutputStream fileOut = new FileOutputStream(new File(parent, getNickName() + getIP() + ".client"));
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(this);
                out.close();
                fileOut.close();
                Logger.log(Level.INFO, "Serialized data is saved in " + parent.getAbsolutePath() + "**.client");
            } catch (IOException i) {
                i.printStackTrace();
            }
        }
    }

    public void clientCommunicate(String msg) throws IOException {
        String toSend = CryptoUtils.encrypt(msg, KumoSettings.AES_KEY);
        dis.writeUTF(toSend);
    }

    public void clientCommunicate(byte[] msg) throws IOException {
        dis.write(msg);
    }

    public void clientCommunicate(int msg) throws IOException{
        dis.writeInt(msg);
    }

    @Override
    protected void finalize() throws Throwable {
        clientOutput.close();
        client.close();
    }

}

