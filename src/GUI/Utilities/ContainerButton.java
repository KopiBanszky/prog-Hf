package GUI.Utilities;

import System.Tree.Item;
import System.Tree.Container;
import System.Tree.Folder;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ContainerButton extends JButton {
    Container container;

    public ContainerButton(Container container) {
        super();
        this.container = container;
        setPreferredSize(new Dimension(900, 75));
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        build();
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
        JPanel folderRight = new JPanel();
        folderRight.setOpaque(false);
        folderRight.setLayout(new BoxLayout(folderRight, BoxLayout.Y_AXIS));
        folderRight.add(name);
        folderRight.add(id);

        add(icon, BorderLayout.WEST);
        add(folderRight, BorderLayout.CENTER);
    }
}
