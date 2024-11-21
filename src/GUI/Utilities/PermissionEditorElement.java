package GUI.Utilities;

import Config.Permission;
import GUI.MainPage;
import System.Tree.Container;
import System.Account;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class PermissionEditorElement extends JPanel {
    private HashMap<Account, Permission> permissions;
    private JComboBox<String> userSelection;

    public PermissionEditorElement(HashMap<Account, Permission> permissions) {
        super();
        this.permissions = permissions;

        setLayout(new FlowLayout(FlowLayout.LEFT));
        setPreferredSize(new Dimension(400, 100));
        setBackground(Utility.SECONDARY_BG_COLOR);

        createPermissionEditors().forEach(this::add);
        try {
            userSelection = newPermissionButton(Account.getAllAccounts());
            add(userSelection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HashMap<Account, Permission> getPermissions() {
        return permissions;
    }

    private JComboBox<String> newPermissionButton(ArrayList<Account> accounts) {
        JComboBox<String> userSelection = new JComboBox<>();
        accounts.forEach(account -> {
            if(account.isIn(permissions.keySet())) return;
            userSelection.addItem(account.getName());
        });

        userSelection.addActionListener(e -> {
            Account selectedAccount;
            try {
                selectedAccount = accounts.stream().filter(account ->
                        account.getName().equals(userSelection.getSelectedItem())
                ).findFirst().orElseThrow();
            } catch (Exception exception) {
                return;
            }
            permissions.put(selectedAccount, Permission.READ);

            remove(userSelection);
            add(createPermissionEditor(selectedAccount, Permission.READ));
            add(this.userSelection = newPermissionButton(accounts));

            validate();
            repaint();
        });

        return userSelection;
    }

    private ArrayList<JComponent> createPermissionEditors(){
        ArrayList<JComponent> permissionEditors = new ArrayList<>();
        permissions.forEach((account, permission) -> {
            permissionEditors.add(createPermissionEditor(account, permission));
        });

        return permissionEditors;
    }

    private JComponent createPermissionEditor(Account account, Permission permission){
        JComponent permissionEditor = new JPanel();
        permissionEditor.setLayout(new FlowLayout());
        permissionEditor.setBackground(Utility.BG_COLOR);

        JLabel userLabel = new JLabel(account.getName());
        userLabel.setForeground(Utility.FONT_COLOR);
        permissionEditor.add(userLabel);

        JComboBox<String> permissionSelection = new JComboBox<>();
        permissionSelection.addItem("READ");
        permissionSelection.addItem("EDIT");
        permissionSelection.addItem("ADMIN");
        permissionSelection.setSelectedItem(permission.toString());
        permissionSelection.addActionListener(e -> {
            permissions.put(account, Permission.valueOf((String) permissionSelection.getSelectedItem()));
        });
        permissionEditor.add(permissionSelection);

        JButton removeButton = new IconButton(Icons.DELETE, 12);
        removeButton.addActionListener(e -> {
            permissions.remove(account);
            System.out.println(permissions);
            remove(permissionEditor);
            remove(userSelection);
            try {
                add(userSelection = newPermissionButton(Account.getAllAccounts()));
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            validate();
            repaint();
        });
        permissionEditor.add(removeButton);

        return permissionEditor;
    }

}
