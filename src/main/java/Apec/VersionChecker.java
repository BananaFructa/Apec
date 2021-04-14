package Apec;

import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class VersionChecker {

    /**
     * @return Returns the latest version tag of the mod
     */
    public static String getVersion() {
        try {
            URL url = new URL("https://dl.dropboxusercontent.com/s/onll3kv3ijjmjdn/apecVersion.txt"); // A dropbox link that shows the file apecVersion.txt
            Scanner s = new Scanner(url.openStream());
            return s.nextLine();
        } catch (Exception e) {
            return ApecMain.version;
        }
    }

    public static String getRegistryTag() {
        try {
            URL url = new URL("https://dl.dropboxusercontent.com/s/keifgf5xf8sfmau/current-database-tag.txt");
            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            Scanner s = new Scanner(connection.getInputStream());
            connection.connect();
            return s.nextLine();
        } catch (Exception e) {
            return "NULL";
        }
    }

}
