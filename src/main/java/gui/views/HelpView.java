package gui.views;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class HelpView {

    public void showHelpView(){
        try {
            Desktop.getDesktop().browse(new URL("https://kumo.now.sh/faq").toURI());
        } catch (IOException | URISyntaxException s){
            new AlertView().showErrorAlert("Unable to open help web page!");
        }

    }
}
