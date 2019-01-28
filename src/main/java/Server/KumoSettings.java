package Server;

import Server.Data.CryptoUtils;

public class KumoSettings {

    /* START - settings saved / loaded from file */
    public static String CONNECTION_IP = "localhost";
    public static boolean SHOW_NOTIFICATIONS = true;
    public static boolean BACKGROUND_PERSISTENT = false;
    public static int MAX_CONNECTIONS = 999;
    public static int PORT = 22122;
    public static boolean SOUND = false;
    public static String AES_KEY = CryptoUtils.randTextAlpha(16);
    /* STOP - settings not saved / loaded from file */

    public static String CURRENT_VERSION = "1.0";
}