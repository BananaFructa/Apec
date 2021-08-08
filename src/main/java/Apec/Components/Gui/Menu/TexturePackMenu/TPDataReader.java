package Apec.Components.Gui.Menu.TexturePackMenu;

import net.minecraft.util.Tuple;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
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
            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            connection.connect();
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
        String name = null,author = null,download = null,icon = null,description = null,version = null,efn = null,optifine = null,neu = null,tag = null,connections = null;
        final int tagCount = 11;
        for (int i = 0;i < tagCount;i++) {
            Tuple<String, String> pair = readNextPair();
            if (pair == null) break;
            String tagPair = pair.getFirst();
            if (tagPair.equals("name")) name = pair.getSecond();
            else if (tagPair.equals("author")) author = pair.getSecond();
            else if (tagPair.equals("download")) download = pair.getSecond();
            else if (tagPair.equals("icon")) icon = pair.getSecond();
            else if (tagPair.equals("description")) description = pair.getSecond();
            else if (tagPair.equals("version")) version = pair.getSecond();
            else if (tagPair.equals("efn")) efn = pair.getSecond();
            else if (tagPair.equals("req-optifine")) optifine = pair.getSecond();
            else if (tagPair.equals("req-NEU")) neu = pair.getSecond();
            else if (tagPair.equals("tag")) tag = pair.getSecond();
            else if (tagPair.equals("connections")) connections = pair.getSecond();
        }
        if (name == null || author == null || download == null || icon == null || description == null || efn == null || optifine == null || version == null || neu == null || tag == null) return null;
        return new TPData(name,author,description,version,tag,download,optifine,neu,icon,efn,connections);
    }

}