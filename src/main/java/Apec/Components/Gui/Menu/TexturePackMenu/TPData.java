package Apec.Components.Gui.Menu.TexturePackMenu;

public class TPData {
    public String name;
    public String author;
    public String downloadUrl;
    public String iconUrl;
    public String description;

    public TPData(String name, String author, String description, String downloadUrl, String iconUrl) {
        this.name = name;
        this.author = author;
        this.description = description;
        this.downloadUrl = downloadUrl;
        this.iconUrl = iconUrl;
    }

}
