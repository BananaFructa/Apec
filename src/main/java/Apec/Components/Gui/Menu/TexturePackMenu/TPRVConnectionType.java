package Apec.Components.Gui.Menu.TexturePackMenu;

public enum TPRVConnectionType {
    FORUM_PAGE,
    DISCORD,
    WEB_PAGE,
    TWITTER,
    YOUTUBE;

    public static TPRVConnectionType parseConnection(String s) {
        for (TPRVConnectionType type : TPRVConnectionType.values()) {
            if (s.equals(type.name())) return type;
        }
        return null;
    }

}
