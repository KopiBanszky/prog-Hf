package GUI.Utilities;

import System.Tree.Item;
import System.Tree.Container;
import System.Tree.Folder;


import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class ContainerButton extends JButton {
    Container container;
    private int radius = 30;

    public ContainerButton(Container container) {
        super();
        this.container = container;
        setPreferredSize(new Dimension(800, 75));
        setBackground(new Color(46, 46, 46));
        setForeground(Utility.FONT_COLOR);
        //setBorder(new LineBorder(Color.black, 1));
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorder(null);
        setOpaque(false);

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        build();
    }


    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background
        g2d.setColor(getBackground());
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

        // Border
        g2d.setColor(Color.black);
        g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);

        g2d.dispose();
        super.paintComponent(g);
    }

    private void build() {
        boolean isFolder = container instanceof Folder;

        Icons iconName = isFolder ? Icons.FOLDER : Icons.ITEM;

        JLabel icon = new JLabel(Icons.loadIcon(iconName, 50, 50));
        JLabel name = new JLabel(container.getName());
        JLabel id = new JLabel(isFolder ?
                container.getReadableId() :
                ((Item)container).getReadableId()
        );
        id.setForeground(Utility.FONT_COLOR);
        name.setForeground(Utility.FONT_COLOR);
        JPanel folderRight = new JPanel();
        folderRight.setOpaque(false);
        folderRight.setLayout(new BoxLayout(folderRight, BoxLayout.Y_AXIS));
        folderRight.add(name);
        folderRight.add(id);

        add(icon, BorderLayout.WEST);
        add(folderRight, BorderLayout.CENTER);
    }
}
