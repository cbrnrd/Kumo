package GUI.Views;

import GUI.Styler;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FirstRunView {
    private static JFXButton setClipboard;
    private static JFXTextField textField;

    public static JFXTextField getTextField(){ return textField; }

    public static JFXButton getSend(){ return setClipboard; }

    public VBox getFirstRunView(Stage stage) {
        VBox vBox = new VBox();
        VBox.setVgrow(vBox, Priority.ALWAYS);
        vBox.getStylesheets().add(getClass().getResource(Styler.getCurrentStylesheet()).toExternalForm());
        VBox vBox2 = new VBox(5);
        vBox2.setId("settingsView");
        vBox2.setPadding(new Insets(0, 25, 0, 25));
        vBox2.setAlignment(Pos.CENTER);
        VBox.setVgrow(vBox2, Priority.ALWAYS);
        Label label = new Label("Selly.gg order ID:");
        label = (Label) Styler.styleAdd(label, "label-bright");
        textField = new JFXTextField("");
        setClipboard = new JFXButton("Verify");
        vBox2.getChildren().addAll(label, textField, setClipboard);
        vBox.getChildren().addAll(vBox2);
        return vBox;
    }
}
