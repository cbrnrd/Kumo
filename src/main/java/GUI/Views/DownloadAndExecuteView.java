package GUI.Views;

import GUI.Components.BottomBar;
import GUI.Components.TopBar;
import GUI.Styler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;


public class DownloadAndExecuteView {

    private static TextField urlField;
    private static Button downloadButton;
    private static Label statusLabel;

    public static TextField getUrlField() {
        return urlField;
    }

    public static String getUrlFieldValue(){
        return urlField.getText();
    }

    public static Button getDownloadButton() {
        return downloadButton;
    }

    public static void setStatusLabel(String data){
        statusLabel.setText(data);
    }

    public static void setStatusLabelColor(String value){
        statusLabel.setTextFill(Paint.valueOf(value));
    }

    public VBox getDownloadAndExecuteView(Stage stage){
        VBox vBox = new VBox();
        VBox.setVgrow(vBox, Priority.ALWAYS);
        vBox.getStylesheets().add(getClass().getResource("/css/global.css").toExternalForm());
        VBox vBox2 = new VBox(5);
        vBox2.setId("downloadAndExecuteView");
        vBox2.setAlignment(Pos.CENTER);
        VBox.setVgrow(vBox2, Priority.ALWAYS);
        vBox2.setPadding(new Insets(0, 25, 0, 25));
        Label urlLabel = new Label("URL of binary: ");
        urlLabel = (Label) Styler.styleAdd(urlLabel, "label-bright");
        urlField = new TextField();
        urlField.setPromptText("Example: http://example.com/evil.exe");
        downloadButton = new Button("GO");
        statusLabel = new Label("");
        vBox2.getChildren().addAll(urlLabel, urlField, downloadButton, statusLabel);
        vBox.getChildren().addAll(new TopBar().getTopBarSansOptions(stage), vBox2, new BottomBar().getBottomBar());
        return vBox;
    }

}
