package gui.components;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import kumo.Kumo;
import logger.Level;
import logger.Logger;
import server.ClientObject;

import java.io.*;

public class FileContextMenu {
    public static String selectedDirectory;

    public static void getFileContextMenu(HBox fileIcon, String fileName, MouseEvent e, ClientObject client) {
        ContextMenu cm = new ContextMenu();
        MenuItem sb1 = new MenuItem("Delete File");
        sb1.setOnAction( event -> {
            try {
                client.clientCommunicate("DELFILE " + fileName);
            } catch (IOException ioe){
                Logger.log(Level.ERROR, ioe.toString(), ioe);
            }
        });
        MenuItem sb2 = new MenuItem("Download File");
        sb1.setId("fileMenuItem");

        sb2.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select download location");
            File selectedDirectory =
                    directoryChooser.showDialog(Kumo.getPrimaryStage());
            FileContextMenu.selectedDirectory = selectedDirectory.getAbsolutePath();
            try {
                client.clientCommunicate("DOWNLOAD");
                client.clientCommunicate(fileName);
            } catch (IOException e1) {
                Logger.log(Level.ERROR, e1.toString(), e1);
            }
        });
        cm.getItems().addAll(sb1, sb2);
        cm.show(fileIcon, e.getScreenX(), e.getScreenY());
    }

    public static void getDirectoryMenu(HBox fileIcon, String fileName, MouseEvent e, ClientObject client) {
        ContextMenu cm = new ContextMenu();
        MenuItem sb2 = new MenuItem("Open Folder");
        sb2.setOnAction(event -> {
            try {
                client.clientCommunicate("CHNGDIR");
                DataOutputStream dos = new DataOutputStream(client.getClient().getOutputStream());
                client.clientCommunicate(fileName);
            } catch (IOException e1) {
                Logger.log(Level.ERROR, e1.toString(), e1);
            }
        });
        MenuItem sb3 = new MenuItem("Upload to folder");
        sb3.setOnAction( event -> {
            try{
                // Have user select file to upload
                FileChooser chooser = new FileChooser();
                chooser.setTitle("Select file to upload");
                File fToUpload = chooser.showOpenDialog(Kumo.getPrimaryStage());

                // FULOAD
                // path to upload to
                // Filename
                // File length
                client.clientCommunicate("FULOAD");
                client.clientCommunicate(fileName); // Dir to write to
                client.clientCommunicate(fToUpload.getName()); // Filename

                // Send file over the wire
                Long length = fToUpload.length();
                DataOutputStream dos = new DataOutputStream(client.getClient().getOutputStream());
                dos.writeLong(length);
                FileInputStream fis = new FileInputStream(fToUpload);
                BufferedInputStream bs = new BufferedInputStream(fis);

                int fbyte;
                while ((fbyte = bs.read()) != -1) dos.writeInt(fbyte);
                bs.close();
                fis.close();

            } catch (IOException ioe){
                Logger.log(Level.ERROR, "Error in file upload: ", ioe);
            }
        });
        cm.getItems().addAll(sb2, sb3);
        cm.show(fileIcon, e.getScreenX(), e.getScreenY());
    }
}
