package GUI.Components;

import GUI.Styler;
import Server.ClientObject;
import Server.Data.Repository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Paint;

public class ClientList implements Repository {
    private static TableView tableView;

    public static TableView getTableView() {
        return tableView;
    }

    public TableView getClientList() {

        Label placeholder = new Label("No clients yet :(");
        placeholder.setTextFill(Paint.valueOf(Styler.getCurrentAccentColor()));

        tableView = new TableView();
        tableView.setPlaceholder(placeholder);
        tableView.setEditable(true);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.getStylesheets().add(getClass().getResource(Styler.getCurrentStylesheet()).toExternalForm());

        TableColumn<String, String> onlineStatus = new TableColumn<>("Status");
        onlineStatus.setMaxWidth(70);
        onlineStatus.setResizable(false);
        onlineStatus.setCellValueFactory(
                new PropertyValueFactory<>("onlineStatus"));


        TableColumn<ClientObject, String> nickName = new TableColumn<>("ID");
        nickName.setMinWidth(150);
        nickName.setMaxWidth(200);
        nickName.setResizable(true);
        nickName.setEditable(true);
        nickName.setOnEditCommit(
                t -> t.getTableView().getItems().get(
                        t.getTablePosition().getRow()).setNickName(t.getNewValue())
        );
        nickName.setCellValueFactory(new PropertyValueFactory<>("nickName"));
        nickName.setCellFactory(TextFieldTableCell.forTableColumn());
        /*nickName.setCellFactory(col -> {
            final TableCell<ClientObject, String> cell = new TableCell<>();
            cell.textProperty().bind(cell.itemProperty());
            cell.setOnMouseClicked(event -> {
                if (event.getButton().equals(MouseButton.SECONDARY) && cell.getTableView().getSelectionModel().getSelectedItem() != null && cell.getTableView().getSelectionModel().getSelectedItem().getClient().isConnected()) {
                    IPContextMenu.getIPContextMenu(cell, event);
                }
            });

            return cell;
        });*/


        /*nickName.setCellFactory(col -> {
            final TableCell<ClientObject, String> cell = new TableCell<>();
            cell.textProperty().bind(cell.itemProperty());
            cell.setOnMouseClicked(event -> {
                if (event.getButton().equals(MouseButton.SECONDARY) && cell.getTableView().getSelectionModel().getSelectedItem() != null && cell.getTableView().getSelectionModel().getSelectedItem().getClient().isConnected()) {
                    IPContextMenu.getIPContextMenu(cell, event);
                }
            });
            return cell;
        });*/

        TableColumn<ClientObject, String> IP = new TableColumn<>("IP");
        IP.setMinWidth(200);
        IP.setResizable(false);
        IP.setCellValueFactory(new PropertyValueFactory<>("IP"));
        IP.setCellFactory(col -> {
            final TableCell<ClientObject, String> cell = new TableCell<>();
            cell.textProperty().bind(cell.itemProperty());
            cell.setOnMouseClicked(event -> {
                if (event.getButton().equals(MouseButton.SECONDARY) && cell.getTableView().getSelectionModel().getSelectedItem() != null && cell.getTableView().getSelectionModel().getSelectedItem().getClient().isConnected()) {

                    IPContextMenu.getIPContextMenu(cell, event);
                }
            });
            return cell;
        });

        TableColumn<ClientObject, String> OS = new TableColumn<>("OS");
        OS.setMinWidth(100);
        OS.setResizable(true);
        OS.setCellValueFactory(new PropertyValueFactory<>("SYSTEM_OS"));
        OS.setCellFactory(TextFieldTableCell.forTableColumn());
        OS.setEditable(false);
        OS.setCellFactory(col -> {
            final TableCell<ClientObject, String> cell = new TableCell<>();
            cell.textProperty().bind(cell.itemProperty());
            cell.setOnMouseClicked(event -> {
                if (event.getButton().equals(MouseButton.SECONDARY) && cell.getTableView().getSelectionModel().getSelectedItem() != null && cell.getTableView().getSelectionModel().getSelectedItem().getClient().isConnected()) {
                    IPContextMenu.getIPContextMenu(cell, event);
                }
            });
            return cell;
        });

        TableColumn<ClientObject, String> user = new TableColumn<>("User");
        user.setMinWidth(200);
        user.setResizable(true);
        user.setCellValueFactory(new PropertyValueFactory<>("uname"));
        user.setEditable(false);
        user.setCellFactory(col -> {
            final TableCell<ClientObject, String> cell = new TableCell<>();
            cell.textProperty().bind(cell.itemProperty());
            cell.setOnMouseClicked(event -> {
                if (event.getButton().equals(MouseButton.SECONDARY) && cell.getTableView().getSelectionModel().getSelectedItem() != null && cell.getTableView().getSelectionModel().getSelectedItem().getClient().isConnected()) {
                    IPContextMenu.getIPContextMenu(cell, event);
                }
            });
            return cell;
        });

        ObservableList<ClientObject> list = FXCollections.observableArrayList();
        list.addAll(CONNECTIONS.values()); // Only ClientObjects
        tableView.setItems(list);
        tableView.getColumns().addAll(onlineStatus, nickName, IP, OS, user);

        return tableView;
    }
}
