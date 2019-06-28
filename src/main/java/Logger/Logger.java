package Logger;

import Server.Data.FileUtils;

import java.io.File;
import java.io.IOException;
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

    protected static boolean initialized = false;

    private static File logFile;

    public static File getLogFile() {
        return logFile;
    }

    public static void initialize(File f){
        try {
            if (!f.exists()) {
                f.createNewFile();
                logFile = f;
                initialized = true;
                FileUtils.appendLineToFile("========== NEW START " + new Date() + " ==========\n", logFile);
            } else {
                logFile = f;
                initialized = true;
                FileUtils.appendLineToFile("========== NEW START " + new Date() + " ==========\n", logFile);
            }
            System.out.println("Log file initialized: " + f.getAbsolutePath());
        } catch (IOException ioe){
            ioe.printStackTrace();
        }
    }

    public static void log(Level level, String message) {
        if (!initialized){
            System.out.println('[' + ANSI_RED + "LOGGER\\ERROR" + ':' + new Date() + ANSI_RESET + "] - " + "Cannot log to an uninitialized log file. Logs will not be saved!");
            return;
        }
        Date date = new Date();
        String out = "";
        switch (level) {
            case INFO:
                out = '[' + ANSI_BLUE + "INFO" + ':' + date + ANSI_RESET + "] - " + message;
                break;
            case WARNING:
                out = '[' + ANSI_YELLOW + "WARNING" + ':' + date + ANSI_RESET + "] - " + message;
                break;
            case ERROR:
                out = '[' + ANSI_RED + "ERROR" + ':' + date + ANSI_RESET + "] - " + message;
                break;
            case GOOD:
                out = '[' + ANSI_GREEN + "GOOD" + ':' + date + ANSI_RESET + "] - " + message;
            default:
                break;
        }
        System.out.println(out);

        if (initialized){
            String cleaned = out.replaceAll("\u001B\\[[;\\d]*m", "").replace("\n", "\r\n");
            FileUtils.appendLineToFile(cleaned, logFile);
        }
    }

    public static void log(Level level, String message, Throwable t) {
        if (!initialized){
            System.out.println('[' + ANSI_RED + "LOGGER\\ERROR" + ':' + new Date() + ANSI_RESET + "] - " + "Cannot log to an uninitialized log file!");
            return;
        }

        Date date = new Date();
        String out = "";
        switch (level) {
            case INFO:
                out = '[' + ANSI_BLUE + "INFO" + ':' + date + ANSI_RESET + "] - " + message;
                break;
            case WARNING:
                out = '[' + ANSI_YELLOW + "WARNING" + ':' + date + ANSI_RESET + "] - " + message;
                break;
            case GOOD:
                out = '[' + ANSI_GREEN + "GOOD" + ':' + date + ANSI_RESET + "] - " + message;
                break;
            case ERROR:
                out = '[' + ANSI_RED + "ERROR" + ':' + date + ANSI_RESET + "] - " + message;
                t.printStackTrace();
                FileUtils.appendLineToFile(FileUtils.stacktraceToString(t), logFile);
            default:
                break;
        }
        System.out.println(out);

        if (initialized){
            String cleaned = out.replaceAll("\u001B\\[[;\\d]*m", "").replace("\n", "\r\n");
            FileUtils.appendLineToFile(cleaned, logFile);
        }
    }
}
