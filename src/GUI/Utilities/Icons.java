package GUI.Utilities;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;

public enum Icons {
    FOLDER("folder"),
    ITEM("item"),
    DELETE("delete"),
    EDIT("edit"),
    ADD_FOLDER("add_folder"),
    ADD_ITEM("add_item"),
    SORT_ASC("sort_asc"),
    SORT_DESC("sort_desc"),
    LOGOUT("logout"),
    DELETE_ACCOUNT("delete_account"),
    BACK("back"),
    NULL("null");

    private final String name;

    Icons(String name) {
        this.name = name;
    }

    /**
     * Loads an icon from the resources
     * @param name of the icon
     * @return ImageIcon of the icon
     */
    public static ImageIcon loadIcon(Icons name) {
        return loadIcon(name, 16, 16);
    }

    /**
     * Loads an icon from the resources and scales it to the given width and height
     * @param name of the icon
     * @param width of the icon
     * @param height of the icon
     * @return ImageIcon of the icon
     */
    public static ImageIcon loadIcon(Icons name, int width, int height) {
        try {
            InputStream iconStream = Icons.class.getResourceAsStream("/icons/" + name + ".png");
            if(iconStream == null) {
                return loadIcon(Icons.NULL, width, height);
            }
            ImageIcon icon = new ImageIcon(iconStream.readAllBytes());
            Image img = icon.getImage();
            Image newImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(newImg);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Loads an icon from the resources and scales it to the given size
     * @param name of the icon
     * @param size of the icon
     * @return ImageIcon of the icon
     */
    public static ImageIcon loadIcon(Icons name, int size) {
        return loadIcon(name, size, size);
    }
}
