package Apec.Components.Gui.Menu.TexturePackMenu;

import net.minecraft.util.Tuple;

import java.util.ArrayList;
import java.util.List;

public class TPData {
    public String name;
    public String author;
    public String downloadUrl;
    public String iconUrl;
    public String description;
    public String tag;
    public String version;
    public String expectedFileName;
    public String requiresOptifine;
    public String requiresNeu;
    public String connections;

    public TPData(String name,
                  String author,
                  String description,
                  String version,
                  String tag,
                  String downloadUrl,
                  String requiresOptifine,
                  String requiresNeu,
                  String iconUrl,
                  String expectedFileName,
                  String connections) {
        this.name = name;
        this.author = author;
        this.description = description.replace("\\n","\n");
        this.downloadUrl = downloadUrl;
        this.iconUrl = iconUrl;
        this.version = version;
        this.expectedFileName = expectedFileName;
        this.requiresOptifine = requiresOptifine;
        this.requiresNeu = requiresNeu;
        this.tag = tag;
        this.connections = connections;
    }
}
