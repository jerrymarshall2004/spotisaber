package spotisaber.api.util;

import lombok.experimental.UtilityClass;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

@UtilityClass
public class Utils {

    public static void openLinkInBrowser(String request){
        try{
            Desktop.getDesktop().browse(URI.create(request));
        } catch (IOException error) {
            System.out.println(error.getMessage());
        }
    }

    public static void openLinkInBrowser(URI request){
        try{
            Desktop.getDesktop().browse(request);
        } catch (IOException error) {
            System.out.println(error.getMessage());
        }
    }
}
