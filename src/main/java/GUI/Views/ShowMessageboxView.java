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

public class ShowMessageboxView {

    private static JFXTextField msg;
    private static JFXButton goButton;

    public static JFXTextField getMsg(){ return msg; }
    public static JFXButton getGoButton(){ return goButton; }


    public VBox getShowMessageboxView(Stage stage){
        VBox vBox = new VBox();
        VBox.setVgrow(vBox, Priority.ALWAYS);
        vBox.getStylesheets().add(getClass().getResource(Styler.getCurrentStylesheet()).toExternalForm());
        VBox vBox2 = new VBox(5);
        vBox2.setId("settingsView");
        vBox2.setAlignment(Pos.CENTER);
        vBox2.setPadding(new Insets(0, 25, 0, 25));
        VBox.setVgrow(vBox2, Priority.ALWAYS);
        Label label = new Label("Message:");
        label = (Label) Styler.styleAdd(label, "label-bright");
        msg = new JFXTextField("");
        goButton = new JFXButton("SHOW");
        vBox2.getChildren().addAll(label, msg, goButton);
        vBox.getChildren().addAll(new TopBar().getStrippedTopBar(stage), vBox2, new BottomBar().getBottomBar());
        return vBox;
    }

}
