package GUI.Views;

import GUI.Components.BottomBar;
import GUI.Components.TopBar;
import GUI.Styler;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class WebDeliveryView {

    private static final ObservableList<String> targets = FXCollections.observableArrayList(
            "Python",
            "Powershell"
    );

    public static JFXComboBox getTargetsComboBox() {
        return targetsComboBox;
    }

    public static JFXTextField getUrl() {
        return url;
    }

    public static JFXButton getExecuteButton(){
        return executeButton;
    }

    private static JFXComboBox targetsComboBox;
    private static JFXTextField url;
    private static JFXButton executeButton;


    public VBox getWebDeliveryView(Stage stage) {
        VBox vBox = new VBox();
        VBox.setVgrow(vBox, Priority.ALWAYS);
        vBox.getStylesheets().add(getClass().getResource(Styler.getCurrentStylesheet()).toExternalForm());
        VBox vBox2 = new VBox(5);
        vBox2.setPadding(new Insets(0, 25, 0, 25));
        vBox2.setId("settingsView");
        vBox2.setAlignment(Pos.CENTER);
        VBox.setVgrow(vBox2, Priority.ALWAYS);

        Label tgtLabel = (Label) Styler.styleAdd(new Label("Target: "), "label-bright");
        targetsComboBox = new JFXComboBox(targets);
        Label urlLabel = (Label) Styler.styleAdd(new Label("URL of payload: "), "label-bright");
        url = new JFXTextField();
        url.setId(".textarea-padded");
        url.setPromptText("Eg. 192.168.1.2:8080/jUipTv5");
        executeButton = new JFXButton("Execute Web Delivery");
        vBox2.getChildren().addAll(tgtLabel, targetsComboBox, urlLabel, url, executeButton);
        vBox.getChildren().addAll(new TopBar().getTopBarSansOptions(stage), vBox2, new BottomBar().getBottomBar());
        return vBox;
    }
}
