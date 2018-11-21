package GUI.Views;


import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;

import static javafx.scene.control.Alert.AlertType;

public class AlertView {

    public void showErrorAlert(String message){
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("ERROR");
        alert.setHeaderText("No internet detected");
        alert.setContentText("No internet detected. Aborting startup.");
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/css/global.css").toExternalForm());
        dialogPane.getStyleClass().add("alert");
        alert.showAndWait();
        Platform.exit();
    }

}
