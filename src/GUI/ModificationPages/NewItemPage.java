package GUI.ModificationPages;

import Config.Permission;
import GUI.MainPage;
import GUI.Utilities.*;
import System.Account;
import System.Tree.Item;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class NewItemPage {
    private MainPage mainPage;
    private PermissionEditorElement permissionEditor;
    private Item item;

    public NewItemPage(MainPage mainPage) {
        this.mainPage = mainPage;

        if(mainPage.arguments().get("editOrNew").equals("edit")) {
            item = (Item) mainPage.arguments().get("item");
        }
        mainPage.setTitle("Prog3 Leltár - Új elem");
        JPanel panel = builder();

        mainPage.build(panel);

    }


    private JPanel builder() {

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Utility.BG_COLOR);

        JPanel header = new JPanel();
        JPanel body;

        try {
            body = bodyBuilder();
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(null,
                    exception.getMessage(),
                    "Hiba",
                    JOptionPane.ERROR_MESSAGE
            );
            return null;
        }

        //TODO: display accurate to permissions
//region header
        header.setLayout(new BorderLayout());
        header.setBackground(Color.BLACK);
        header.setPreferredSize(new Dimension(900, 40));

        JPanel headerLeft = new JPanel();
        headerLeft.setLayout(new FlowLayout(FlowLayout.LEFT));
        headerLeft.setBackground(Color.BLACK);

        JButton backToParent = new IconButton(Icons.BACK, 15);
        if(mainPage.arguments().get("editOrNew").equals("edit")) {
            HashMap<String, Object> arguments = new HashMap<>();
            arguments.put("item", item);
            backToParent.addActionListener(e -> mainPage.navigator("/itemPage"));
        }
        else {
            backToParent.addActionListener(e -> mainPage.navigator("/folderPage"));
        }
        headerLeft.add(backToParent, BorderLayout.WEST);

        JLabel searchField = new JLabel();
        searchField.setForeground(Utility.FONT_COLOR);
        searchField.setPreferredSize(new Dimension(250, 20));
        //String title = mainPage.system().getCurrentParent().getName();
        String title = "root";
        String utility = mainPage.arguments().get("editOrNew").toString();
        searchField.setText((title.equals("root") ? "Gyökérkönyvtár" : title) + " - "
                + (utility.equals("edit") ? "Elem szerkesztése" : "Új elem"));
        headerLeft.add(searchField);

        header.add(headerLeft,BorderLayout.WEST);

//endregion


        panel.add(header, BorderLayout.NORTH);
        panel.add(body, BorderLayout.CENTER);

        return panel;
    }

    private JPanel bodyBuilder() throws Exception {
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
        right.setPreferredSize(new Dimension(450, 400));


        //region nameField
        JTextField nameField = new JTextField();
        nameField.setBackground(Utility.SECONDARY_BG_COLOR);
        nameField.setForeground(Utility.INPUT_FONT_COLOR);
        nameField.setPreferredSize(new Dimension(250, 20));
        Utility.addPlaceholder(nameField, "Elem neve");
        boolean edit = mainPage.arguments().get("editOrNew").equals("edit");
        if(edit) {
            nameField.setText(item.getName());
            nameField.setForeground(Utility.INPUT_FONT_COLOR);
        }
        left.add(nameField);

        //endregion

        //region descriptionField

        JTextArea descriptionField = new JTextArea();
        descriptionField.setBackground(Utility.SECONDARY_BG_COLOR);
        descriptionField.setForeground(Utility.INPUT_FONT_COLOR);
        descriptionField.setPreferredSize(new Dimension(400, 100));
        Utility.addPlaceholder(descriptionField, "Elem leírása");

        if(edit) {
            descriptionField.setText(item.getDescription(mainPage.system().getAccount()));
            descriptionField.setForeground(Utility.INPUT_FONT_COLOR);
        }
        left.add(descriptionField);
        //endregion


        //region permissionEditor

        HashMap<Account, Permission> permissions = (HashMap<Account, Permission>) mainPage.system().getCurrentParent().getPermissions().clone();
        if(edit) {
            permissions = (HashMap<Account, Permission>) item.getPermissions().clone();
        }
        permissionEditor = new PermissionEditorElement(permissions);

        left.add(permissionEditor, BorderLayout.SOUTH);
        for(Account account : permissions.keySet()) {
            if(mainPage.system().getCurrentParent().getName().equals("root") && !edit) break;
            if(!account.equals(mainPage.system().getAccount())) continue;
            if(permissions.get(account).equals(Permission.ADMIN)) break;
            left.remove(permissionEditor);
            break;
        }

        //endregion

        //region image adder
        ImageManager imageManager = new ImageManager();
        if(edit) {
            imageManager = new ImageManager(item, mainPage.system().getAccount());
        }
        left.add(imageManager);

        //endregion

        //region text adder

        JTextArea textField = new JTextArea();
        textField.setBackground(Utility.SECONDARY_BG_COLOR);
        textField.setForeground(Utility.INPUT_FONT_COLOR);
        textField.setPreferredSize(new Dimension(420, 360));
        Utility.addPlaceholder(textField, "Elem szövege");

        if(edit) {
            textField.setText(item.getText(mainPage.system().getAccount()));
            textField.setForeground(Utility.INPUT_FONT_COLOR);
        }

        right.add(textField, BorderLayout.NORTH);


        //endregion


        JButton create = new IconButton(edit ? Icons.EDIT : Icons.ADD_ITEM, 25);
        ImageManager finalImageManager = imageManager;
        create.addActionListener(e -> {
            if(nameField.getText().equals("") || nameField.getText().equals("Elem neve")) {
                JOptionPane.showMessageDialog(null,
                        "Az elem neve nem lehet üres!",
                        "Hiba",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            try {
                String description = descriptionField.getText().equals("Elem leírása") ? "" : descriptionField.getText();
                String longText = textField.getText().equals("Elem szövege") ? "" : textField.getText();
                if(edit) {
                    item.setName(nameField.getText());
                    item.setDescription(description, mainPage.system().getAccount());
                    item.setText(longText, mainPage.system().getAccount());
                    item.addImgs(finalImageManager.getNewImgs(), mainPage.system().getAccount());
                    try {
                        finalImageManager.getDeletedImgs().forEach(img -> {
                            try {
                                item.removeImg(img, mainPage.system().getAccount());
                            } catch (Exception ex) {
                                throw new RuntimeException(ex);
                            }
                        });
                    } catch (Exception exception) {
                        JOptionPane.showMessageDialog(null,
                                exception.getMessage(),
                                "Hiba",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                    item.setPermissions(permissionEditor.getPermissions());

                    HashMap<String, Object> arguments = new HashMap<>();
                    arguments.put("item", item);
                    mainPage.navigator("/itemPage", arguments);

                    return;
                }

                Item newItem = mainPage.system().addItemToCurrentParent(
                        nameField.getText(),
                        description,
                        longText,
                        finalImageManager.getImgs(),
                        permissionEditor.getPermissions()
                );
                HashMap<String, Object> arguments = new HashMap<>();
                arguments.put("item", newItem);
                mainPage.navigator("/itemPage", arguments);
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(null,
                        exception.getMessage(),
                        "Hiba",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
        right.add(create, BorderLayout.SOUTH);




        body.add(left, BorderLayout.WEST);
        body.add(right, BorderLayout.EAST);



        return body;
    }

}
