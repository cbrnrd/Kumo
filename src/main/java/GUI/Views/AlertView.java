package GUI.Views;


import GUI.Styler;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;

import static javafx.scene.control.Alert.AlertType;

public class AlertView {

    public void showErrorAlert(String message){
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("ERROR");
        alert.setHeaderText("An error occured");
        alert.setContentText(message);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource(Styler.getCurrentStylesheet()).toExternalForm());
        dialogPane.getStyleClass().add("alert");
        alert.show();
    }

    public void showErrorAlertWait(String message){
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("ERROR");
        alert.setHeaderText("An error occured");
        alert.setContentText(message);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource(Styler.getCurrentStylesheet()).toExternalForm());
        dialogPane.getStyleClass().add("alert");
        alert.showAndWait();
    }

}
