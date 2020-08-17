package gui.views;

import gui.Styler;
import gui.components.BottomBar;
import gui.components.TopBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import server.ClientObject;


public class RemoteDesktopView {
    private static ClientObject client;

    private static void setClient(ClientObject client) {
        RemoteDesktopView.client = client;
    }

    public BorderPane getRemoteDesktopView(ClientObject client, Stage stage) {
        setClient(client);
        BorderPane borderPane = new BorderPane();
        borderPane.getStylesheets().add(getClass().getResource(Styler.getCurrentStylesheet()).toExternalForm());
        ImageView imageView = new ImageView();
        borderPane.setTop(new TopBar().getReflectiveTopBar(stage));
        borderPane.setCenter(imageView);
        borderPane.setBottom(new BottomBar().getBottomBar());
        return borderPane;
    }
}
