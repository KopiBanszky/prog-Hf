package GUI.Utilities;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Docs extends JPanel {
    public Docs() {
        setLayout(new FlowLayout());
        setBackground(Utility.BG_COLOR);
        setPreferredSize(new Dimension(200, 50));

        IconButton documentations = new IconButton(Icons.ITEM);
        documentations.addActionListener(e -> {
            //InputStream indexHtml = LoginPage.class.getResourceAsStream("/javadoc/index.html");
            File file = new File("doc/index.html");
            if(Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                try {
                    desktop.browse(file.toURI());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        IconButton userManual = new IconButton(Icons.MANUAL);
        userManual.addActionListener(e -> {
            //InputStream indexHtml = LoginPage.class.getResourceAsStream("/javadoc/index.html");
            File file = new File("README.pdf");
            if(Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                try {
                    desktop.browse(file.toURI());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });


        if(Desktop.isDesktopSupported()){
            add(documentations, BorderLayout.SOUTH);
            add(userManual, BorderLayout.SOUTH);
        }
    }
}
