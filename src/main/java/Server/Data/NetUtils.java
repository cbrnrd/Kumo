package Server.Data;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import Logger.*;

public class NetUtils {

    public static String sendHttpRequest(String s){
        try{
            return new Scanner(new URL(s).openStream(), "UTF-8").useDelimiter("\\A").next();
        } catch (IOException ioe){
            Logger.log(Level.ERROR, "Error in HTTP request to " + s + ". Exception below:", ioe);
            return "";
        }
    }

}
