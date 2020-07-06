package Apec;

import java.net.URL;
import java.util.Scanner;

public class VersionChecker {

    public static String getVersion() {
        try {
            URL url = new URL("https://dl.dropboxusercontent.com/s/onll3kv3ijjmjdn/apecVersion.txt"); // A dropbox link that shows the file apecVersion.txt
            Scanner s = new Scanner(url.openStream());
            return s.nextLine();
        } catch (Exception e) {
            System.out.println("Failed to import version!");
            return ApecMain.version;
        }
    }

}
