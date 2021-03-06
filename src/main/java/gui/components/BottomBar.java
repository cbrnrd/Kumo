package gui.components;

import gui.Styler;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import server.data.Repository;


public class BottomBar implements Repository {
    private static Label connectionsLabel = null;

    public static Label getConnectionsLabel() {
        return connectionsLabel;
    }

    public HBox getBottomBar() {
        HBox hBox = new HBox();
        HBox.setHgrow(hBox, Priority.ALWAYS);
        VBox vBox = new VBox();
        vBox.getStylesheets().add(getClass().getResource(Styler.getCurrentStylesheet()).toExternalForm());
        VBox.setVgrow(vBox, Priority.ALWAYS);
        connectionsLabel = new Label("  Clients: " + CONNECTIONS.size());
        connectionsLabel = (Label) Styler.styleAdd(connectionsLabel, "label-light");
        connectionsLabel.setStyle("-fx-text-fill: " + Styler.getCurrentAccentColor());

        vBox.getChildren().add(connectionsLabel);
        hBox.getChildren().add(vBox);
        hBox.setId("stat-bar");
        return hBox;
    }
}
