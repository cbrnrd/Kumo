package gui.components;

import gui.Controller;
import gui.Styler;
import gui.views.ReleaseNotesView;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import logger.Level;
import logger.Logger;
import server.KumoSettings;

class TitleBar {

    HBox getMenuBar(Stage stage) {
        MenuBar menuBar = new MenuBar();
        menuBar.getStylesheets().add(getClass().getResource(Styler.getCurrentStylesheet()).toExternalForm());
        menuBar.getStyleClass().add("background");

        Label kumoLabel = (Label) Styler.styleAdd(new Label("Kumo " + KumoSettings.CURRENT_VERSION + " - " + KumoSettings.VERSION_CODENAME), "option-button");
        kumoLabel.setPadding(new Insets(5, 10, 5, 10));
        kumoLabel.setOnMouseClicked(event -> {
            Stage s = new Stage();
            s.initStyle(StageStyle.UNDECORATED);
            s.setMinHeight(300);
            s.setMinWidth(300);
            s.setScene(new Scene(new ReleaseNotesView().getReleaseNotesView(s), 600, 600));
            ReleaseNotesView.getData().setText(Controller.getReleaseNotes());
            s.show();
        });

        Label minimize = (Label) Styler.styleAdd(new Label("_"), "option-button");
        minimize.setPadding(new Insets(5, 10, 5, 10));
        minimize.setOnMouseClicked(event -> stage.setIconified(true));

        Label exit = (Label) Styler.styleAdd(new Label("X"), "exit-button");
        exit.setPadding(new Insets(5, 10, 5, 10));
        exit.setOnMouseClicked(event -> {
            if (stage.equals(kumo.Kumo.getPrimaryStage())) {
                Logger.log(Level.INFO, "Exit event detected. ");
                /* Only hide stage if kumo is set to be background persistent */
                if (KumoSettings.BACKGROUND_PERSISTENT) {
                    kumo.Kumo.getPrimaryStage().hide();
                } else {
                    Platform.exit();
                    kumo.Kumo.systemTray.remove(kumo.Kumo.systemTray.getTrayIcons()[0]);
                    System.exit(0);
                }
            } else {
                stage.close();
            }
        });

        HBox sep = Styler.hContainer();
        sep.setId("drag-bar");
        final Delta dragDelta = new Delta();
        sep.setOnMousePressed(mouseEvent -> {
            dragDelta.x = stage.getX() - mouseEvent.getScreenX();
            dragDelta.y = stage.getY() - mouseEvent.getScreenY();
        });
        sep.setOnMouseDragged(mouseEvent -> {
            stage.setX(mouseEvent.getScreenX() + dragDelta.x);
            stage.setY(mouseEvent.getScreenY() + dragDelta.y);
        });

        HBox hBox = Styler.hContainer(5, kumoLabel, sep, minimize, exit);
        hBox.setId("drag-bar");
        return hBox;
    }

    class Delta {
        double x, y;
    }

}
