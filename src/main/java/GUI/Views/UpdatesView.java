package GUI.Views;

import GUI.Components.BottomBar;
import GUI.Components.NotificationView;
import GUI.Components.TopBar;
import GUI.Styler;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.Scanner;


class UpdatesView {
    private BorderPane updatesView = new BorderPane();

    BorderPane getUpdatesView() {
        updatesView.getStylesheets().add(getClass().getResource("/css/global.css").toExternalForm());
        HBox hBox1 = getUpdatesPanel();
        HBox hBox = getAboutPanel();
        hBox.setId("updatesView");
        hBox1.setId("updatesView");
        hBox.setPadding(new Insets(0, 0, 0, 10));
        updatesView.setTop(new TopBar().getTopBar(KUMO.Kumo.getPrimaryStage()));
        updatesView.setLeft(hBox);
        updatesView.setCenter(hBox1);
        updatesView.setBottom(new BottomBar().getBottomBar());
        return updatesView;
    }

    private HBox getAboutPanel() {
        Label title = (Label) Styler.styleAdd(new Label("KUMO 1.0"), "title");
        Text desc = (Text) Styler.styleAdd(new Text("KUMO is a lightweight cross-platform remote administrative tool " +
                "written in Java. \n\nKUMO is intended to present necessary features \nin an attractive and " +
                "easy to use UI. \n\nKumo is adapted from an open-sourced RAT called \nMaus (https://github.com/Ghosts/Maus) " +
                "Kumo builds on \nthe foundation it laid out and improves on all of them."), "");
        Button checkUpdates = new Button("Check for Updates");
        checkUpdates.setOnMouseClicked(event -> {
            Platform.runLater(() -> NotificationView.openNotification("Update Check Complete"));

            HBox hBox = getUpdatesPanel();
            hBox.setId("updatesView");
            updatesView.setCenter(hBox);
        });
        return Styler.hContainer(Styler.vContainer(30, title, desc)); //, checkUpdates));
    }

    private HBox getUpdatesPanel() {
        TextArea updates = new TextArea();
        updates.setPrefWidth(600);
        updates.setEditable(false);
        try{
            updates.setText(new Scanner(getClass().getResource("/LICENSE").openStream(), "UTF-8").useDelimiter("\\A").next());
        } catch (IOException ioe){
            ioe.printStackTrace();
        }

        return Styler.hContainer(updates);
    }

}
