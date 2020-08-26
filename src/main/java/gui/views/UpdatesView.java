package gui.views;

import com.jfoenix.controls.JFXButton;
import gui.Styler;
import gui.components.TopBar;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import kumo.Kumo;
import logger.Logger;
import server.KumoSettings;

import java.awt.*;
import java.io.IOException;
import java.util.Scanner;


public class UpdatesView {
    private BorderPane updatesView = new BorderPane();

    public BorderPane getUpdatesView() {
        updatesView.getStylesheets().add(getClass().getResource(Styler.getCurrentStylesheet()).toExternalForm());
        HBox hBox1 = getUpdatesPanel();
        HBox hBox = getAboutPanel();
        hBox.setId("updatesView");
        hBox1.setId("updatesView");
        hBox.setPadding(new Insets(0, 0, 0, 10));
        Kumo.getPrimaryStage().setHeight(600);
        updatesView.setTop(new TopBar().getTopBar(kumo.Kumo.getPrimaryStage()));
        updatesView.setLeft(hBox);
        updatesView.setCenter(hBox1);
        //updatesView.setBottom(new BottomBar().getBottomBar());
        return updatesView;
    }

    private HBox getAboutPanel() {
        Label title = (Label) Styler.styleAdd(new Label("Kumo " + KumoSettings.CURRENT_VERSION + " - " + KumoSettings.VERSION_CODENAME), "title");
        Text desc = (Text) Styler.styleAdd(new Text("kumo is a lightweight cross-platform remote administrative tool " +
                "written in Java. \n\nkumo is intended to present necessary features \nin an attractive and " +
                "easy to use UI.\n\nKumo was built to experiment with GUI design but evolved into a great project \nthat is fun to work on. There are 2 libraries used in Kumo: JFoenix and javafxsvg.\nPlease remember to use Kumo responsibly and use the\nsoftware according to the rules outlined in the \"LEGAL\" section to the right --->." +
                "\n\nIf you have any questions, concerns, issues, or anything else to ask the \ndeveloper you can PM @ClovaHF on Telegram or KumoRAT@protonmail.com."), "");
        if (KumoSettings.DARK_MODE){
            desc.setFill(Paint.valueOf("white"));
        } else {
            desc.setFill(Paint.valueOf("black"));
        }
        JFXButton checkUpdates = new JFXButton("View Debug Log");
        checkUpdates.setPadding(new Insets(10, 10, 10, 15));
        checkUpdates.setOnMouseClicked(event -> {
            Platform.runLater(() -> {
                try {
                    Desktop.getDesktop().open(Logger.getLogFile());
                } catch (IOException ioe){
                    ioe.printStackTrace();
                    new AlertView().showErrorAlert("Unable to open debug log: " + ioe.getMessage());
                }
            });

            HBox hBox = getUpdatesPanel();
            hBox.setId("updatesView");
            updatesView.setCenter(hBox);
        });

        JFXButton helpButton = new JFXButton("Help");
        helpButton.setPadding(new Insets(15, 15, 15, 15));
        helpButton.setOnMouseClicked(event -> {
            Platform.runLater(() -> {
                new HelpView().showHelpView();
            });

            HBox hBox = getUpdatesPanel();
            hBox.setId("updatesView");
            updatesView.setCenter(hBox);
        });

        return Styler.hContainer(Styler.vContainer(20, title, desc, checkUpdates, helpButton));
    }

    private HBox getUpdatesPanel() {
        TextArea updates = new TextArea();
        updates.setPrefWidth(600);
        updates.setPrefColumnCount(120);
        updates.setEditable(false);
        try{
            updates.setText(new Scanner(getClass().getResource("/LICENSE").openStream(), "UTF-8").useDelimiter("\\A").next());
        } catch (IOException ioe){
            ioe.printStackTrace();
        }

        return Styler.hContainer(updates);
    }

}
