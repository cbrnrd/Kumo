package gui.views;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import gui.Styler;
import gui.components.BottomBar;
import gui.components.TopBar;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ShowMessageboxView {

    private static JFXTextField msg;
    private static JFXTextField title;
    private static JFXButton goButton;

    public static JFXTextField getMsg(){ return msg; }
    public static JFXTextField getTitle() { return title; }
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
        Label titleLabel = new Label("Title:");
        titleLabel = (Label) Styler.styleAdd(titleLabel, "label-bright");
        title = new JFXTextField("");
        Label label = new Label("Message:");
        label = (Label) Styler.styleAdd(label, "label-bright");
        msg = new JFXTextField("");

        // Validator
        RequiredFieldValidator fieldValidator = new RequiredFieldValidator("This field is required");
        JFXTextField[] fields = {title, msg};
        for (JFXTextField f : fields) {
            f.getValidators().add(fieldValidator);
            f.focusedProperty().addListener((o, old, newVal) -> {
                if (!newVal) f.validate();
            });
        }
        goButton = new JFXButton("SHOW");
        vBox2.getChildren().addAll(titleLabel, title, label, msg, goButton);
        vBox.getChildren().addAll(new TopBar().getReflectiveTopBar(stage), vBox2, new BottomBar().getBottomBar());
        return vBox;
    }

}
