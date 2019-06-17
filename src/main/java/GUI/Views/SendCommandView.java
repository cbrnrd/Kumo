package GUI.Views;

import GUI.Components.BottomBar;
import GUI.Components.TopBar;
import GUI.Styler;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SendCommandView {
    private static TextArea console;
    private static JFXButton sendCommandButton;
    private static JFXTextField textField;

    public static TextArea getConsole() {
        return console;
    }

    public static JFXButton getsendCommandButton() {
        return sendCommandButton;
    }

    public static JFXTextField getTextField() {
        return textField;
    }

    public VBox getSendCommandView(Stage stage) {
        VBox vBox = new VBox();
        VBox.setVgrow(vBox, Priority.ALWAYS);
        vBox.getStylesheets().add(getClass().getResource(Styler.getCurrentStylesheet()).toExternalForm());
        VBox vBox2 = new VBox(5);
        vBox2.setId("settingsView");
        vBox2.setPadding(new Insets(0, 25, 0, 25));
        vBox2.setAlignment(Pos.CENTER);
        VBox.setVgrow(vBox2, Priority.ALWAYS);
        Label label = new Label("Command:");
        label = (Label) Styler.styleAdd(label, "label-bright");
        textField = new JFXTextField("");
        sendCommandButton = new JFXButton("Send Command");
        console = new TextArea("");
        console.setId("console");
        console.setEditable(false);
        console.setPrefRowCount(30);
        vBox2.getChildren().addAll(label, textField, sendCommandButton, Styler.vContainer(console));
        vBox.getChildren().addAll(new TopBar().getStrippedTopBar(stage), vBox2, new BottomBar().getBottomBar());
        return vBox;
    }
}
