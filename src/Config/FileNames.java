package Config;

import java.util.ArrayList;

public class FileNames {
    public static final String FOLDER = "database";
    public static String FILE = "containers.ser";
    public static String ACCOUNT = "accounts.ser";
    public static String IMGS_FOLDER = "imgs";
    public static String TEXT_FOLDER = "texts";
    public static final ArrayList<String> IMG_EXTENSIONS = new ArrayList<String>() {{
        add("jpg");
        add("jpeg");
        add("png");
        add("gif");
    }};

    public FileNames() {}
}
