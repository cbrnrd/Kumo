package GUI.Views;

import GUI.Components.BottomBar;
import GUI.Components.TopBar;
import GUI.Styler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class WebDeliveryView {

    private static final ObservableList<String> targets = FXCollections.observableArrayList(
            "Python",
            "Powershell"
    );

    public static ComboBox getTargetsComboBox() {
        return targetsComboBox;
    }

    public static TextField getUrl() {
        return url;
    }

    public static Button getExecuteButton(){
        return executeButton;
    }

    private static ComboBox targetsComboBox;
    private static TextField url;
    private static Button executeButton;


    public VBox getWebDeliveryView(Stage stage) {
        VBox vBox = new VBox();
        VBox.setVgrow(vBox, Priority.ALWAYS);
        vBox.getStylesheets().add(getClass().getResource("/css/global.css").toExternalForm());
        VBox vBox2 = new VBox(5);
        vBox2.setId("settingsView");
        vBox2.setAlignment(Pos.CENTER);
        VBox.setVgrow(vBox2, Priority.ALWAYS);

        Label tgtLabel = (Label) Styler.styleAdd(new Label("Target: "), "label-bright");
        targetsComboBox = new ComboBox(targets);
        Label urlLabel = (Label) Styler.styleAdd(new Label("URL of payload: "), "label-bright");
        url = new TextField();
        url.setId(".textarea-padded");
        url.setPromptText("Eg. 192.168.1.2:8080/jUipTv5");
        executeButton = new Button("Execute Web Delivery");
        vBox2.getChildren().addAll(tgtLabel, targetsComboBox, urlLabel, url, executeButton);
        vBox.getChildren().addAll(new TopBar().getTopBarSansOptions(stage), vBox2, new BottomBar().getBottomBar());
        return vBox;
    }
}
