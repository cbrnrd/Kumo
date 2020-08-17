package gui.views;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import gui.Styler;
import gui.components.BottomBar;
import gui.components.TopBar;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SleepView {
    private static JFXButton sleepButton;
    private static JFXTextField textField;

    public static JFXTextField getTextField(){ return textField; }

    public static JFXButton getSleepButton(){ return sleepButton; }

    public VBox getSleepView(Stage stage) {
        VBox vBox = new VBox();
        VBox.setVgrow(vBox, Priority.ALWAYS);
        vBox.getStylesheets().add(getClass().getResource(Styler.getCurrentStylesheet()).toExternalForm());
        VBox vBox2 = new VBox(5);
        vBox2.setId("settingsView");
        vBox2.setPadding(new Insets(0, 25, 0, 25));
        vBox2.setAlignment(Pos.CENTER);
        VBox.setVgrow(vBox2, Priority.ALWAYS);
        Label label = new Label("Time (sec):");
        label = (Label) Styler.styleAdd(label, "label-bright");
        textField = new JFXTextField("60");
        sleepButton = new JFXButton("Sleep");
        vBox2.getChildren().addAll(label, textField, sleepButton);
        vBox.getChildren().addAll(new TopBar().getReflectiveTopBar(stage), vBox2, new BottomBar().getBottomBar());
        return vBox;
    }
}
