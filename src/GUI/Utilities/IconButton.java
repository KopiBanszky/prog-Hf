package GUI.Utilities;

import javax.swing.*;
import java.awt.*;

public class IconButton extends JButton {
    public IconButton(Icons icon) {
        super(Icons.loadIcon(icon, 20, 20));
        setPreferredSize(new Dimension(30, 30));
        build();
    }

    public IconButton(Icons icon, int width, int height) {
        super(Icons.loadIcon(icon, width, height));
        setPreferredSize(new Dimension(width+10, height+10));
        build();
    }

    public IconButton(Icons icon, int size) {
        super(Icons.loadIcon(icon, size, size));
        setPreferredSize(new Dimension(size+10, size+10));
        build();
    }

    private void build() {
        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.CENTER);
        setBackground(Color.BLACK);
        setForeground(Color.BLACK);
        setBorder(BorderFactory.createEmptyBorder());
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
