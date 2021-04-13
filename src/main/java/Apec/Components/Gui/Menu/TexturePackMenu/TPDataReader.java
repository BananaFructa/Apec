package Apec.Components.Gui.Menu.TexturePackMenu;

import net.minecraft.util.Tuple;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.Sys;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TPDataReader {

    private String[] lines;
    private int currentIndex = 0;

    public TPDataReader(String data) {
        lines = data.split("\n");
    }

    public TPDataReader(URL url) {
        List<String> lines = new ArrayList<String>();
        String line;
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }
            bufferedReader.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        Object[] oarr = lines.toArray();
        this.lines = Arrays.copyOf(oarr, oarr.length, String[].class);
    }

    public String nextLine() {
        if (currentIndex < lines.length) {
            return lines[currentIndex++];
        } else {
            return null;
        }
    }

    public Tuple<String,String> readNextPair() {
        String line = null;

        do {
            line = nextLine();
            if (line == null) return null;
        } while(line.length() == 0);
        String[] lines = line.split(":",2);
        return new Tuple<String, String>(lines[0],lines[1]);
    }

    public List<String> readAllDatabaseUrls() {
        List<String> urls = new ArrayList<String>();
        Tuple<String,String> pair = readNextPair();
        while (pair != null) {
            if (pair.getFirst().equals("data-base")) {
                urls.add(pair.getSecond());
            }
            pair = readNextPair();
        }
        return urls;
    }

    public TPData readNextTPData() {
        String name = null,author = null,download = null,icon = null,description = null,version = null,efn = null,optifine = null;
        for (int i = 0;i < 5;i++) {
            Tuple<String, String> pair = readNextPair();
            String tag = pair.getFirst();
            if (tag.equals("name")) name = pair.getSecond();
            else if (tag.equals("author")) author = pair.getSecond();
            else if (tag.equals("download")) download = pair.getSecond();
            else if (tag.equals("icon")) icon = pair.getSecond();
            else if (tag.equals("description")) description = pair.getSecond();
            else if (tag.equals("version")) version = pair.getSecond();
            else if (tag.equals("efn")) efn = pair.getSecond();
            else if (tag.equals("req-optifine")) optifine = pair.getSecond();
        }
        if (name == null || author == null || download == null || icon == null || description == null || efn == null || optifine == null) return null;
        return new TPData(name,author,description,version,download,optifine,icon,efn);
    }

}