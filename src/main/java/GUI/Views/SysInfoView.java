package GUI.Views;

import GUI.Components.BottomBar;
import GUI.Components.TopBar;
import GUI.Styler;
import javafx.geometry.Pos;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SysInfoView {

    private static TextArea area;

    public static TextArea getArea(){ return area; }

    public VBox getSysInfoView(Stage stage){
        VBox vBox = new VBox();
        VBox.setVgrow(vBox, Priority.ALWAYS);
        vBox.getStylesheets().add(getClass().getResource(Styler.getCurrentStylesheet()).toExternalForm());
        VBox vBox2 = new VBox(5);
        vBox2.setId("settingsView");
        vBox2.setAlignment(Pos.CENTER);
        VBox.setVgrow(vBox2, Priority.ALWAYS);
        area = new TextArea("");
        area.setPrefRowCount(30);
        area.setId("console");
        area.setEditable(false);
        vBox2.getChildren().addAll( Styler.vContainer(area));
        vBox.getChildren().addAll(new TopBar().getReflectiveTopBar(stage), vBox2, new BottomBar().getBottomBar());
        return vBox;
    }

}
