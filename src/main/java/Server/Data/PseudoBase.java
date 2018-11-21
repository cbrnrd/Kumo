package Server.Data;


import Logger.Level;
import Logger.Logger;
import Server.KumoSettings;
import KUMO.ClientBuilder;
import Server.ClientObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.io.*;

public class PseudoBase implements Repository {
    /* Collection of client connections by representation of ClientObjects*/
    private static ObservableMap<String, ClientObject> xratData = FXCollections.observableHashMap();

    public synchronized static ObservableMap<String, ClientObject> getXratData() {
        return xratData;
    }

    /* Serializes client objects  & writes server settings */
    public static void writeKumoData() throws IOException {
        for (ClientObject o : xratData.values()) {
            if (o != null) {
                o.serialize();
            }
        }

        // Make the base directory
        new File(System.getProperty("user.home") + "/Kumo/").mkdir();

        File data = new File(System.getProperty("user.home") + "/Kumo/.serverSettings");
        try (BufferedWriter writer =
                     new BufferedWriter(new FileWriter(data))) {
            writer.write(KumoSettings.CONNECTION_IP + " ");
            writer.write(KumoSettings.SHOW_NOTIFICATIONS + " ");
            writer.write(KumoSettings.BACKGROUND_PERSISTENT + " ");
            writer.write(KumoSettings.MAX_CONNECTIONS + " ");
            writer.write(KumoSettings.PORT + " ");
            writer.write(KumoSettings.SOUND + " ");
        } catch (IOException i) {
            Logger.log(Level.ERROR, i.toString());
        }

        File xrcs = new File(System.getProperty("user.home") + "/Kumo/.xrst");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(xrcs))) {
            writer.write(KumoSettings.CONNECTION_IP + "\n" + " ");
            writer.write("" + KumoSettings.PORT + "\n" + " ");
            writer.write("" + ClientBuilder.isPersistent + "\n" + " ");
            writer.write("" + ClientBuilder.autoSpread);
        } catch (IOException i) {
            Logger.log(Level.ERROR, i.toString());
        }
    }

    /* Creates necessary files for Xrat to run. Including the directories for client + server setting data. */
    public void createXratData() throws IOException {
        final File parent = new File(System.getProperty("user.home") + "/Kumo/clients");
        final File parent2 = new File(System.getProperty("user.home") + "/Kumo/Client");
        if (!parent.mkdirs() && !parent2.mkdirs()) {
            Logger.log(Level.WARNING, "Unable to make necessary directories, may already exist.");
        }
        File settings = new File(System.getProperty("user.home") + "/Kumo/.serverSettings");
        if (!settings.exists()) {
            settings.createNewFile();
        }
    }

    /* Loads .KumoSettings file to Xrat server settings*/
    private void loadServerSettings() {
        try (BufferedReader reader = new BufferedReader(new FileReader(System.getProperty("user.home") + "/Kumo/.serverSettings"))
        ) {
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            String[] settings = stringBuilder.toString().split(" ");
            if (settings.length == 6) {
                KumoSettings.CONNECTION_IP = (settings[0].trim());
                KumoSettings.SHOW_NOTIFICATIONS = (Boolean.valueOf(settings[1].trim()));
                KumoSettings.BACKGROUND_PERSISTENT = (Boolean.valueOf(settings[2].trim()));
                KumoSettings.MAX_CONNECTIONS = (Integer.parseInt(settings[3].trim()));
                KumoSettings.PORT = (Integer.parseInt(settings[4].trim()));
                KumoSettings.SOUND = (Boolean.valueOf(settings[5].trim()));
            }
            Logger.log(Level.INFO,"Kumo server settings loaded.");
        } catch (IOException e) {
            Logger.log(Level.ERROR, e.toString());
        }
    }

    /* Loads data for server settings and from .client objects (serialized) and adds them to XratData & CONNECTIONS */
    public void loadData() throws IOException, ClassNotFoundException {
        loadServerSettings();
        File folder = new File(System.getProperty("user.home") + "/Kumo/clients/");
        File[] listOfFiles = folder.listFiles();
        assert listOfFiles != null;
        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile()) {
                String file = listOfFile.toString();
                if (file.contains(".client")) {
                    ClientObject o;
                    ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
                    try {
                        o = (ClientObject) in.readObject();
                        xratData.put(o.getIP(), o);
                        in.close();
                    } catch (InvalidClassException e){
                        deleteXratData(System.getProperty("user.home") + "/Kumo/clients/");
                    }
                    listOfFile.delete();
                }
            } else {
                break;
            }
        }
    }

    /* Deletes serialized client objects in the event that they become corrupted / out dated (mostly used for development)*/
    private void deleteXratData(String directory){
       File folder = new File(directory);
       File[] listOfFiles = folder.listFiles();
       assert listOfFiles != null;
       for (File listOfFile : listOfFiles) {
           if (listOfFile.isFile()) {
               String file = listOfFile.toString();
               if (file.contains(".client")) {
                   listOfFile.delete();
               }
           } else {
               break;
           }
       }
    }
}
