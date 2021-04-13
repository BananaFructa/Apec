package Apec.Components.Gui.Menu.TexturePackMenu;

public class TPData {
    public String name;
    public String author;
    public String downloadUrl;
    public String iconUrl;
    public String description;
    public String version;
    public String expectedFileName;
    public String requiresOptifine;

    public TPData(String name, String author, String description,String version, String downloadUrl,String requiresOptifine, String iconUrl,String expectedFileName) {
        this.name = name;
        this.author = author;
        this.description = description;
        this.downloadUrl = downloadUrl;
        this.iconUrl = iconUrl;
        this.version = version;
        this.expectedFileName = expectedFileName;
        this.requiresOptifine = requiresOptifine;
    }

}
