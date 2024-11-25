package GUI.ModificationPages;

import Config.Permission;
import GUI.Containers.FolderPage;
import GUI.MainPage;
import GUI.Utilities.*;
import System.Account;
import System.Tree.Comparators.NameComparatorAsc;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.util.HashMap;

public class NewFolderPage {
    private MainPage mainPage;
    private PermissionEditorElement permissionEditor;

    public NewFolderPage(MainPage mainPage) {
        this.mainPage = mainPage;

        mainPage.setTitle("Prog3 Leltár - Új mappa");
        JPanel panel = builder();

        mainPage.build(panel);
    }

    private JPanel builder() {

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Utility.BG_COLOR);

        JPanel header = new JPanel();
        JPanel body = bodyBuilder();

        //TODO: display accurate to permissions
//region header
        header.setLayout(new BorderLayout());
        header.setBackground(Color.BLACK);
        header.setPreferredSize(new Dimension(900, 40));

        JPanel headerLeft = new JPanel();
        headerLeft.setLayout(new FlowLayout(FlowLayout.LEFT));
        headerLeft.setBackground(Color.BLACK);

        JButton backToParent = new IconButton(Icons.BACK, 15);
        backToParent.addActionListener(e -> mainPage.navigator("/folderPage"));
        headerLeft.add(backToParent, BorderLayout.WEST);

        JLabel searchField = new JLabel();
        searchField.setForeground(Utility.FONT_COLOR);
        searchField.setPreferredSize(new Dimension(250, 20));
        //String title = mainPage.system().getCurrentParent().getName();
        String title = "root";
        String utility = mainPage.arguments().get("editOrNew").toString();
        searchField.setText((title.equals("root") ? "Gyökérkönyvtár" : title) + " - "
                + (utility.equals("edit") ? "Mappa szerkesztése" : "Új mappa"));
        headerLeft.add(searchField);

        header.add(headerLeft,BorderLayout.WEST);

//endregion


        panel.add(header, BorderLayout.NORTH);
        panel.add(body, BorderLayout.CENTER);

        return panel;
    }

    private JPanel bodyBuilder() {
        JPanel body = new JPanel();
        body.setLayout(new BorderLayout());
        body.setBackground(Utility.BG_COLOR);

        JPanel left = new JPanel();
        left.setLayout(new FlowLayout(FlowLayout.LEFT));
        left.setBackground(Utility.BG_COLOR);
        left.setPreferredSize(new Dimension(450, 400));

        JPanel right = new JPanel();
        right.setLayout(new FlowLayout(FlowLayout.RIGHT));
        right.setBackground(Utility.BG_COLOR);

        JTextField nameField = new JTextField();
        nameField.setBackground(Utility.SECONDARY_BG_COLOR);
        nameField.setForeground(Utility.INPUT_FONT_COLOR);
        nameField.setPreferredSize(new Dimension(250, 20));
        Utility.addPlaceholder(nameField, "Mappa neve");
        boolean edit = mainPage.arguments().get("editOrNew").equals("edit");
        if(edit) {
            nameField.setText(mainPage.system().getCurrentParent().getName());
            nameField.setForeground(Utility.INPUT_FONT_COLOR);
        }
        left.add(nameField);

        JButton create = new IconButton(edit ? Icons.EDIT : Icons.SAVE, 25);
        create.addActionListener(e -> {
            if(nameField.getText().equals("") || nameField.getText().equals("Mappa neve")) {
                JOptionPane.showMessageDialog(null,
                        "A mappa neve nem lehet üres!",
                        "Hiba",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            try {
                if(edit) {
                    mainPage.system().getCurrentParent().setName(nameField.getText());
                    mainPage.system().getCurrentParent().setPermissions(permissionEditor.getPermissions());
                    mainPage.navigator("/folderPage");
                    return;
                }
                mainPage.system().addFolderToCurrentParent(nameField.getText(), permissionEditor.getPermissions());
                mainPage.navigator("/folderPage");
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(null,
                        exception.getMessage(),
                        "Hiba",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
        right.add(create);

        HashMap<Account, Permission> permissions = (HashMap<Account, Permission>) mainPage.system().getCurrentParent().getPermissions().clone();

        permissionEditor = new PermissionEditorElement(permissions);

        left.add(permissionEditor, BorderLayout.SOUTH);
        for(Account account : permissions.keySet()) {
            if(mainPage.system().getCurrentParent().getName().equals("root")) break;
            if(!account.equals(mainPage.system().getAccount())) continue;
            if(permissions.get(account).equals(Permission.ADMIN)) break;
            left.remove(permissionEditor);
            break;
        }


        JButton button = new IconButton(Icons.FOLDER, 25);
        button.setText("File rendszerből");
        button.setPreferredSize(new Dimension(125, 25));
        button.setForeground(Utility.FONT_COLOR);
        button.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setMultiSelectionEnabled(false);
            fileChooser.setAcceptAllFileFilterUsed(false);

            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    mainPage.system().addTree(file.getAbsolutePath(), mainPage.system().getCurrentParent());
                    mainPage.navigator("/folderPage");
                } catch (Exception exception) {
                    JOptionPane.showMessageDialog(null,
                            exception.getMessage(),
                            "Hiba",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        right.add(button);


        body.add(left, BorderLayout.WEST);
        body.add(right, BorderLayout.EAST);



        return body;
    }


}
