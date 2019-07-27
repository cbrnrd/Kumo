package GUI;

import Server.ClientObject;
import Server.Data.PseudoBase;
import Server.Data.Repository;
import Server.KumoSettings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;

import static GUI.Components.ClientList.getTableView;

public class Controller implements Repository {

    /* Reloads the table to ensure Offline/Online status is accurate. */
    public static void updateTable() {
        ObservableList<ClientObject> list = FXCollections.observableArrayList();
        list.addAll(PseudoBase.getKumoData().values());
        getTableView().setItems(list);
        TableView tableView = getTableView();
        tableView.refresh();
    }

    /* Refreshes the number of connections based on KumoData size. */
    @Deprecated
    public static void updateStats() {
        return;
        //Platform.runLater(() -> BottomBar.getConnectionsLabel().setText("  Clients: " + CONNECTIONS.size() + " \t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t Version " + KumoSettings.CURRENT_VERSION + " | Developer: Clova" ));
    }

    /* Changes the primary view to the provided scene. */
    public static void changePrimaryStage(Pane newScene) {
        KUMO.Kumo.getPrimaryStage().setScene(new Scene(newScene, 900, 500));
    }

    public static void changePrimaryStage(Pane newScene, int w, int h) {
        KUMO.Kumo.getPrimaryStage().setScene(new Scene(newScene, w, h));
    }

    public static String getReleaseNotes(){
        String s = "Release notes: Kumo v" + KumoSettings.CURRENT_VERSION + " - " + KumoSettings.VERSION_CODENAME + "\n" +
                "* Fix linux persistence\n" +
                "* Remove bottom bar\n" +
                "* Add client count to top bar\n" +
                "* Add server debug log viewer\n" +
                "* Increase persistence write timeout\n" +
                "* Fix MSF Python web delivery payload\n" +
                "* Fix MSF web delivery payload target selection (client side)\n" +
                "* Add various field validations\n" +
                "* Add client country to client list\n" +
                "* Add Help and Show Debug Log buttons to INFO tab\n" +
                "* Update LEGAL\n" +
                "* Dark mode preferences now saves across server restarts\n" +
                "* Top bar in dialogs now reflects the active client\n" +
                "* Increase persistence creation timeout (2s -> 4s)\n" +
                "* Improve communication to server if the keylog file is requested and keylogging isn't enabled\n" +
                "* Reduce size of IP column in the client list by 25px\n" +
                "* Add some more verbose server logging to help with debugging\n" +
                "* Server logs now output to a file as well as to the console\n" +
                "* Fix bug where adding the app to tray on OSX/MacOS would cause an infinite loop and the program would not start completely\n" +
                "* Added some small utility functions to the server to shrink the codebase a little bit\n" +
                "* Add server system information to startup output log\n" +
                "* Switch browser credential gather method (PSH based to EXE based)";
        return s;
    }

}
