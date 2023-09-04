package Apec.Utils;

import Apec.ApecMain;

import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class VersionChecker {

    private static final String versionUrl = "https://cdn.jsdelivr.net/gh/BananaFructa/Apec-DATA@__TAG__/current-version.txt";

    public static String getRegistryTag() {
        try {
            URL url = new URL("https://api.github.com/repos/BananaFructa/Apec-DATA/releases/latest");
            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            Scanner s = new Scanner(connection.getInputStream());
            connection.connect();

            String line = s.nextLine();
            String[] split = line.split("\"tag_name\":");
            String tag = split[1].split(",")[0];
            return tag.substring(1,tag.length()-1);
        } catch (Exception e) {
            return "NULL";
        }
    }

    /**
     * @return Returns the latest version tag of the mod
     */
    public static String getVersion() {
        try {
            URL url = new URL(ApecUtils.applyTagOnUrl(versionUrl,getRegistryTag()));
            Scanner s = new Scanner(url.openStream());
            return s.nextLine();
        } catch (Exception e) {
            return ApecMain.version;
        }
    }

}
