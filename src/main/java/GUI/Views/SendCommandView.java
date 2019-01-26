package GUI.Views;

import GUI.Components.BottomBar;
import GUI.Components.TopBar;
import GUI.Styler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SendCommandView {
    private static TextArea console;
    private static Button sendCommandButton;
    private static TextField textField;

    public static TextArea getConsole() {
        return console;
    }

    public static Button getsendCommandButton() {
        return sendCommandButton;
    }

    public static TextField getTextField() {
        return textField;
    }

    public VBox getSendCommandView(Stage stage) {
        VBox vBox = new VBox();
        VBox.setVgrow(vBox, Priority.ALWAYS);
        vBox.getStylesheets().add(getClass().getResource("/css/global.css").toExternalForm());
        VBox vBox2 = new VBox(5);
        vBox2.setId("settingsView");
        vBox2.setPadding(new Insets(0, 25, 0, 25));
        vBox2.setAlignment(Pos.CENTER);
        VBox.setVgrow(vBox2, Priority.ALWAYS);
        Label label = new Label("Command:");
        label = (Label) Styler.styleAdd(label, "label-bright");
        textField = new TextField("");
        sendCommandButton = new Button("Send Command");
        console = new TextArea("");
        console.setId("console");
        console.setEditable(false);
        console.setPrefRowCount(30);
        vBox2.getChildren().addAll(label, textField, sendCommandButton, Styler.vContainer(console));
        //vBox2.prefHeightProperty().bind(stage.widthProperty().multiply(0.6));
        vBox.getChildren().addAll(new TopBar().getStrippedTopBar(stage), vBox2, new BottomBar().getBottomBar());
        return vBox;
    }
}
