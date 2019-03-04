package GUI.Views;

import GUI.Styler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FirstRunView {
    private static Button setClipboard;
    private static TextField textField;

    public static TextField getTextField(){ return textField; }

    public static Button getSend(){ return setClipboard; }

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
        textField = new TextField("");
        setClipboard = new Button("Verify");
        vBox2.getChildren().addAll(label, textField, setClipboard);
        vBox.getChildren().addAll(vBox2);
        return vBox;
    }
}
