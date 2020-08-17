package gui.views;

import gui.Styler;
import gui.components.BottomBar;
import gui.components.TopBar;
import javafx.geometry.Pos;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class KeyloggerView {

    private static TextArea data;

    public static TextArea getData(){ return data; }

    public VBox getKeyloggerView(Stage stage){
        VBox vBox = new VBox();
        VBox.setVgrow(vBox, Priority.ALWAYS);
        vBox.getStylesheets().add(getClass().getResource(Styler.getCurrentStylesheet()).toExternalForm());
        VBox vBox2 = new VBox(5);
        vBox2.setId("settingsView");
        vBox2.setAlignment(Pos.CENTER);
        VBox.setVgrow(vBox2, Priority.ALWAYS);
        data = new TextArea("");
        data.setEditable(false);
        data.setPrefHeight(300);
        vBox2.getChildren().addAll(Styler.vContainer(data));
        vBox.getChildren().addAll(new TopBar().getReflectiveTopBar(stage), vBox2, new BottomBar().getBottomBar());
        return vBox;
    }
}
