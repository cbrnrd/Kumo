package GUI.Views;

import GUI.Components.BottomBar;
import GUI.Components.TopBar;
import GUI.Styler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ShowMessageboxView {

    private static TextField msg;
    private static Button goButton;

    public static TextField getMsg(){ return msg; }
    public static Button getGoButton(){ return goButton; }


    public VBox getShowMessageboxView(Stage stage){
        VBox vBox = new VBox();
        VBox.setVgrow(vBox, Priority.ALWAYS);
        vBox.getStylesheets().add(getClass().getResource("/css/global.css").toExternalForm());
        VBox vBox2 = new VBox(5);
        vBox2.setId("settingsView");
        vBox2.setAlignment(Pos.CENTER);
        VBox.setVgrow(vBox2, Priority.ALWAYS);
        Label label = new Label("Message:");
        label = (Label) Styler.styleAdd(label, "label-bright");
        msg = new TextField("");
        goButton = new Button("SHOW");
        vBox2.getChildren().addAll(label, msg, goButton);
        vBox.getChildren().addAll(new TopBar().getStrippedTopBar(stage), vBox2, new BottomBar().getBottomBar());
        return vBox;
    }

}
