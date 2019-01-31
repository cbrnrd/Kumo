package GUI.Views;

import GUI.Components.BottomBar;
import GUI.Components.TopBar;
import GUI.Styler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;



public class ChromePassView {

    private static TextArea resultsArea;

    public static TextArea getResultsArea(){ return resultsArea; }

    public VBox getChromePassView(Stage stage){
        VBox vBox = new VBox();
        VBox.setVgrow(vBox, Priority.ALWAYS);
        vBox.getStylesheets().add(getClass().getResource("/css/global.css").toExternalForm());
        VBox vBox2 = new VBox(5);
        vBox2.setId("settingsView");
        vBox2.setPadding(new Insets(0, 25, 0, 25));
        vBox2.setAlignment(Pos.CENTER);
        VBox.setVgrow(vBox2, Priority.ALWAYS);
        Label label = new Label("Results:");
        label = (Label) Styler.styleAdd(label, "label-bright");
        resultsArea = new TextArea("Gathering passwords...");
        resultsArea.setId("console");
        resultsArea.setEditable(false);
        resultsArea.setPrefRowCount(30);
        resultsArea.setPrefColumnCount(100);
        vBox2.getChildren().addAll(label, Styler.vContainer(resultsArea));
        vBox.getChildren().addAll(new TopBar().getStrippedTopBar(stage), vBox2, new BottomBar().getBottomBar());
        return vBox;
    }
}
