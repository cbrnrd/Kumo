package GUI.Views;

import GUI.Components.BottomBar;
import GUI.Components.ClientList;
import GUI.Components.TopBar;
import GUI.Styler;
import javafx.scene.layout.BorderPane;

public class ClientView {
    public BorderPane getClientView() {
        BorderPane borderPane = new BorderPane();
        borderPane.getStylesheets().add(getClass().getResource(Styler.getCurrentStylesheet()).toExternalForm());
        borderPane.getStyleClass().add("root");
        borderPane.setTop(new TopBar().getTopBar(KUMO.Kumo.getPrimaryStage()));
        borderPane.setCenter(Styler.vContainer(new ClientList().getClientList()));
        borderPane.setBottom(new BottomBar().getBottomBar());
        return borderPane;
    }
}
