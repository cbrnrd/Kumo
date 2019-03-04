package GUI.Views;

import GUI.Components.BottomBar;
import GUI.Components.TopBar;
import GUI.Styler;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

@SuppressWarnings("Duplicates")
public class SetClipboardView {

    private static JFXButton setClipboard;
    private static JFXTextField textField;

    public static JFXTextField getTextField(){ return textField; }

    public static JFXButton getSetClipboardButton(){ return setClipboard; }

    public VBox getSetClipboardView(Stage stage) {
        VBox vBox = new VBox();
        VBox.setVgrow(vBox, Priority.ALWAYS);
        vBox.getStylesheets().add(getClass().getResource(Styler.getCurrentStylesheet()).toExternalForm());
        VBox vBox2 = new VBox(5);
        vBox2.setId("settingsView");
        vBox2.setPadding(new Insets(0, 25, 0, 25));
        vBox2.setAlignment(Pos.CENTER);
        VBox.setVgrow(vBox2, Priority.ALWAYS);
        Label label = new Label("Clipboard data:");
        label = (Label) Styler.styleAdd(label, "label-bright");
        textField = new JFXTextField("");
        setClipboard = new JFXButton("Set Clipboard");
        vBox2.getChildren().addAll(label, textField, setClipboard);
        vBox.getChildren().addAll(new TopBar().getTopBarSansOptions(stage), vBox2, new BottomBar().getBottomBar());
        return vBox;
    }
}
