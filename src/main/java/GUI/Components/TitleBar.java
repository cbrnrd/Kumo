package GUI.Components;

import GUI.Controller;
import GUI.Styler;
import GUI.Views.ReleaseNotesView;
import Logger.Level;
import Logger.Logger;
import Server.KumoSettings;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

class TitleBar {

    HBox getMenuBar(Stage stage) {
        MenuBar menuBar = new MenuBar();
        menuBar.getStylesheets().add(getClass().getResource(Styler.getCurrentStylesheet()).toExternalForm());
        menuBar.getStyleClass().add("background");

        Label kumo = (Label) Styler.styleAdd(new Label("KUMO " + KumoSettings.CURRENT_VERSION + " - " + KumoSettings.VERSION_CODENAME), "option-button");
        kumo.setPadding(new Insets(5, 10, 5, 10));
        kumo.setOnMouseClicked(event -> {
            Stage s = new Stage();
            s.initStyle(StageStyle.UNDECORATED);
            s.setMinHeight(300);
            s.setMinWidth(300);
            s.setScene(new Scene(new ReleaseNotesView().getReleaseNotesView(s), 600, 600));
            ReleaseNotesView.getData().setText(Controller.getReleaseNotes());
            s.show();
        });
        /*kumo.setOnMouseClicked(event -> {
            String[] KumoEasterEgg = {
                    KumoSettings.CURRENT_VERSION,
            };
            Random rn = new Random();
            int rnn = rn.nextInt(KumoEasterEgg.length);
            kumo.setText(KumoEasterEgg[rnn]);
        });*/

        Label minimize = (Label) Styler.styleAdd(new Label("_"), "option-button");
        minimize.setPadding(new Insets(5, 10, 5, 10));
        minimize.setOnMouseClicked(event -> stage.setIconified(true));

        Label exit = (Label) Styler.styleAdd(new Label("X"), "exit-button");
        exit.setPadding(new Insets(5, 10, 5, 10));
        exit.setOnMouseClicked(event -> {
            if (stage.equals(KUMO.Kumo.getPrimaryStage())) {
                Logger.log(Level.INFO, "Exit event detected. ");
                /* Only hide stage if KUMO is set to be background persistent */
                if (KumoSettings.BACKGROUND_PERSISTENT) {
                    KUMO.Kumo.getPrimaryStage().hide();
                } else {
                    Platform.exit();
                    KUMO.Kumo.systemTray.remove(KUMO.Kumo.systemTray.getTrayIcons()[0]);
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

        HBox hBox = Styler.hContainer(5, kumo, sep, minimize, exit);
        hBox.setId("drag-bar");
        return hBox;
    }

    class Delta {
        double x, y;
    }

}
