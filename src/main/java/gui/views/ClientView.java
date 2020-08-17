package gui.views;

import gui.Styler;
import gui.components.ClientList;
import gui.components.TopBar;
import javafx.scene.layout.BorderPane;

public class ClientView {
    public BorderPane getClientView() {
        BorderPane borderPane = new BorderPane();
        borderPane.getStylesheets().add(getClass().getResource(Styler.getCurrentStylesheet()).toExternalForm());
        borderPane.getStyleClass().add("root");
        borderPane.setTop(new TopBar().getTopBar(kumo.Kumo.getPrimaryStage()));
        borderPane.setCenter(Styler.vContainer(new ClientList().getClientList()));
        //borderPane.setBottom(new BottomBar().getBottomBar());
        return borderPane;
    }
}
