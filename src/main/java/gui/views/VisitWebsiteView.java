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

public class VisitWebsiteView {

    private static JFXTextField url;
    private static JFXButton goButton;

    public static JFXTextField getUrl(){ return url; }
    public static JFXButton getGoButton(){ return goButton; }


    public VBox getVisitWebsiteView(Stage stage){
        VBox vBox = new VBox();
        VBox.setVgrow(vBox, Priority.ALWAYS);
        vBox.getStylesheets().add(getClass().getResource(Styler.getCurrentStylesheet()).toExternalForm());
        VBox vBox2 = new VBox(5);
        vBox2.setId("settingsView");
        vBox2.setAlignment(Pos.CENTER);
        vBox2.setPadding(new Insets(0, 25, 0, 25));
        VBox.setVgrow(vBox2, Priority.ALWAYS);
        Label label = new Label("URL:");
        label = (Label) Styler.styleAdd(label, "label-bright");
        url = new JFXTextField("http://example.com");
        goButton = new JFXButton("VISIT");
        vBox2.getChildren().addAll(label, url, goButton);
        vBox.getChildren().addAll(new TopBar().getStrippedTopBar(stage), vBox2, new BottomBar().getBottomBar());
        return vBox;
    }

}
