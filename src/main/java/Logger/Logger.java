package Logger;

import java.util.Date;

public class Logger {
    /* Color console text for easy identification of log messages. */
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_BLUE = "\u001B[34m";

    public static void log(Level level, String message) {
        Date date = new Date();
        switch (level) {
            case INFO:
                System.out.println('[' + ANSI_BLUE + "INFO" + ':' + date + ANSI_RESET + "] - " + message);
                break;
            case WARNING:
                System.out.println('[' + ANSI_YELLOW + "WARNING" + ':' + date + ANSI_RESET + "] - " + message);
                break;
            case ERROR:
                System.out.println('[' + ANSI_RED + "ERROR" + ':' + date + ANSI_RESET + "] - " + message);

                break;
            default:
                break;
        }
    }

    public static void log(Level level, String message, Throwable t) {
        Date date = new Date();
        switch (level) {
            case INFO:
                System.out.println('[' + ANSI_BLUE + "INFO" + ':' + date + ANSI_RESET + "] - " + message);
                break;
            case WARNING:
                System.out.println('[' + ANSI_YELLOW + "WARNING" + ':' + date + ANSI_RESET + "] - " + message);
                break;
            case ERROR:
                System.out.println('[' + ANSI_RED + "ERROR" + ':' + date + ANSI_RESET + "] - " + message);
                t.printStackTrace();
                break;
            default:
                break;
        }
    }
}
