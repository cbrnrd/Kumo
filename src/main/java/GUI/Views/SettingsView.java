package GUI.Views;


import GUI.Components.BottomBar;
import GUI.Components.NotificationView;
import GUI.Components.TopBar;
import GUI.Controller;
import GUI.Styler;
import Logger.Level;
import Logger.Logger;
import Server.KumoSettings;
import Server.Listeners;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import javax.crypto.Cipher;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class SettingsView {

    public BorderPane getSettingsView() {
        BorderPane borderPane = new BorderPane();
        borderPane.getStylesheets().add(getClass().getResource(Styler.getCurrentStylesheet()).toExternalForm());
        borderPane.setTop(new TopBar().getTopBar(KUMO.Kumo.getPrimaryStage()));
        borderPane.setLeft(settingsViewLeft());
        borderPane.setCenter(settingsViewCenter());
        borderPane.setBottom(new BottomBar().getBottomBar());
        return borderPane;
    }

    private HBox settingsViewLeft() {
        HBox hBox = Styler.hContainer(20);
        hBox.getStylesheets().add(getClass().getResource(Styler.getCurrentStylesheet()).toExternalForm());
        hBox.setId("clientBuilder");
        hBox.setPadding(new Insets(20, 20, 20, 20));
        Label title = (Label) Styler.styleAdd(new Label("Server Settings"), "title");
        hBox.getChildren().add(Styler.vContainer(20, title));
        return hBox;
    }

    private HBox settingsViewCenter() {
        HBox hBox = Styler.hContainer(20);
        hBox.getStylesheets().add(getClass().getResource(Styler.getCurrentStylesheet()).toExternalForm());
        hBox.setId("settingsView");
        hBox.setPadding(new Insets(20, 20, 20, 20));
        Label title = (Label) Styler.styleAdd(new Label(" "), "title");
        RequiredFieldValidator fieldValidator = new RequiredFieldValidator("This field is required");



        Label listeningPortLabel = (Label) Styler.styleAdd(new Label("Listening Port: "), "label-bright");
        JFXTextField listeningPort = new JFXTextField("" + KumoSettings.PORT);
        listeningPort.setTooltip(new Tooltip("The TCP port to listen for connections on"));
        HBox listeningPortBox = Styler.hContainer(listeningPortLabel, listeningPort);
        listeningPortBox.setPadding(new Insets(5, 5, 5, 5));
        listeningPort.setEditable(true);
        listeningPort.textProperty().addListener(((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                listeningPort.setText(newValue.replaceAll("[^\\d]", ""));
            }
        }));

        Label maxConnectionsLabel = (Label) Styler.styleAdd(new Label("Max Connections: "), "label-bright");
        JFXTextField maxConnections = new JFXTextField("" + KumoSettings.MAX_CONNECTIONS);
        maxConnections.setTooltip(new Tooltip("The maximum number of connections to allow"));
        HBox maxConnectionsBox = Styler.hContainer(maxConnectionsLabel, maxConnections);
        maxConnectionsBox.setPadding(new Insets(5, 5, 5, 5));
        maxConnections.setEditable(true);
        maxConnections.textProperty().addListener(((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                maxConnections.setText(newValue.replaceAll("[^\\d]", ""));
            }
        }));

        // Add required field to all fields
        JFXTextField[] fields = new JFXTextField[] {listeningPort, maxConnections};
        for (JFXTextField f : fields){
            f.getValidators().add(fieldValidator);
            f.focusedProperty().addListener((o, old, newVal) -> {
                if (!newVal) f.validate();
            });
        }

        Label aesLabel = (Label) Styler.styleAdd(new Label("Encryption Key: "), "label-bright");
        JFXTextField aesKey = new JFXTextField("" + KumoSettings.AES_KEY);
        aesKey.setTooltip(new Tooltip("The encryption key for all client/server communication"));
        HBox aesBox = Styler.hContainer(aesLabel, aesKey);
        aesKey.setEditable(false);
        aesKey.setStyle("-fx-text-fill: grey");


        JFXCheckBox soundToggle = new JFXCheckBox();
        soundToggle.setTooltip(new Tooltip("Play a sound on new connections"));
        soundToggle.setSelected(KumoSettings.SOUND);
        if (soundToggle.isSelected()) {
            soundToggle.setText("Sound (on) ");
        } else {
            soundToggle.setText("Sound (off) ");
        }
        soundToggle.setOnAction(event -> {
            if (soundToggle.isSelected()) {
                KumoSettings.SOUND = true;
                soundToggle.setText("Sound (on) ");
            } else {
                KumoSettings.SOUND = false;
                soundToggle.setText("Sound (off) ");
            }
        });

        JFXCheckBox notificaitonToggle = new JFXCheckBox();
        notificaitonToggle.setTooltip(new Tooltip("Show notifications on new connections"));
        notificaitonToggle.setSelected(KumoSettings.SHOW_NOTIFICATIONS);
        if (notificaitonToggle.isSelected()) {
            notificaitonToggle.setText("Notifications (on) ");
        } else {
            notificaitonToggle.setText("Notifications (off) ");
        }
        notificaitonToggle.setOnAction(event -> {
            if (notificaitonToggle.isSelected()) {
                KumoSettings.SHOW_NOTIFICATIONS = true;
                notificaitonToggle.setText("Notifications (on) ");
            } else {
                KumoSettings.SHOW_NOTIFICATIONS = false;
                notificaitonToggle.setText("Notifications (off) ");
            }
        });

        JFXCheckBox backgroundPersistentTogle = new JFXCheckBox();
        backgroundPersistentTogle.setTooltip(new Tooltip("Keep the server running after you close it"));
        backgroundPersistentTogle.setSelected(KumoSettings.BACKGROUND_PERSISTENT);
        if (backgroundPersistentTogle.isSelected()) {
            backgroundPersistentTogle.setText("Background Persistent (on) ");
        } else {
            backgroundPersistentTogle.setText("Background Persistent (off) ");
        }
        backgroundPersistentTogle.setOnAction(event -> {
            if (backgroundPersistentTogle.isSelected()) {
                KumoSettings.BACKGROUND_PERSISTENT = true;
                backgroundPersistentTogle.setText("Background Persistent (on) ");
            } else {
                KumoSettings.BACKGROUND_PERSISTENT = false;
                backgroundPersistentTogle.setText("Background Persistent (off) ");
            }
        });

        JFXCheckBox darkMode = new JFXCheckBox();
        darkMode.setSelected(KumoSettings.DARK_MODE);
        if (darkMode.isSelected()) {
            darkMode.setText("Dark Mode (on) ");
        } else {
            darkMode.setText("Dark Mode (off) ");
        }
        darkMode.setOnAction(event -> {
            if (darkMode.isSelected()) {
                KumoSettings.DARK_MODE = true;
                darkMode.setText("Dark Mode (on) ");
            } else {
                KumoSettings.DARK_MODE = false;
                darkMode.setText("Dark Mode (off) ");
            }
        });

        JFXButton applySettings = new JFXButton("Apply Settings");
        applySettings.setPrefWidth(150);
        applySettings.setPrefHeight(50);
        applySettings.setOnAction(event -> {
            if (Integer.parseInt(listeningPort.getText()) != KumoSettings.PORT) {
                int oldPort = KumoSettings.PORT;
                KumoSettings.PORT = (Integer.parseInt(listeningPort.getText()));

                // Start listening server on new port
                Runnable startServer = new Server.Server();
                new Thread(startServer).start();
                Logger.log(Level.INFO, "New listening server started on port " + KumoSettings.PORT);

                try {
                    // Close old server
                    if (Listeners.getListener(oldPort) != null) {
                        Listeners.getListener(oldPort).close();
                        Logger.log(Level.INFO, "Stopping listening server on port " + oldPort);
                    }
                } catch (IOException ioe){
                    Logger.log(Level.ERROR, "Could not stop listening server: ", ioe);
                }


            }
            if (Integer.parseInt(maxConnections.getText()) != KumoSettings.MAX_CONNECTIONS) {
                KumoSettings.MAX_CONNECTIONS = (Integer.parseInt(maxConnections.getText()));
            }
            if (!aesKey.getText().equals(KumoSettings.AES_KEY)){
                int len = 16;
                try{
                    len = Cipher.getMaxAllowedKeyLength("AES");
                } catch (NoSuchAlgorithmException e){
                    e.printStackTrace();
                }
                if (aesKey.getText().length() > len){ // Keys larger than 16 will cause an error on most JRE's
                } else {
                    KumoSettings.AES_KEY = aesKey.getText();
                }
            }
                Platform.runLater(() -> NotificationView.openNotification("Settings Applied"));
            Controller.changePrimaryStage(new SettingsView().getSettingsView(), 900, 600);
            Logger.log(Level.INFO, "New settings applied.");

        });
        hBox.getChildren().add(Styler.vContainer(20, title, listeningPortBox, maxConnectionsBox, aesBox, soundToggle,notificaitonToggle,backgroundPersistentTogle, darkMode, applySettings));
        return hBox;
    }
}
