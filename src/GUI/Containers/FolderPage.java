package GUI.Containers;

import Config.Permission;
import GUI.MainPage;
import GUI.Utilities.*;
import System.Tree.Comparators.NameComparatorAsc;
import System.Tree.Comparators.NameComparatorDesc;
import System.Tree.Container;
import System.Tree.Folder;
import System.Tree.Item;
import System.Account;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

public class FolderPage {
    private MainPage mainPage;
    private String query = null;

    public FolderPage(MainPage mainPage) {
        this.mainPage = mainPage;


        if(mainPage.arguments() != null) {
            if (mainPage.arguments().containsKey("query")) {
                query = mainPage.arguments().get("query").toString();
            }
        }

        mainPage.setTitle("Prog3 Leltár - Mappák");
        JPanel panel = builder();

        mainPage.build(panel);
    }

    private JPanel builder() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Utility.BG_COLOR);

        JPanel header = new JPanel();
        JPanel footer = new JPanel();
        JPanel body = buildBody();

//region header
        header.setLayout(new BorderLayout());
        header.setBackground(Color.BLACK);
         header.setPreferredSize(new Dimension(900, 40));

        JPanel headerLeft = new JPanel();
        headerLeft.setLayout(new FlowLayout(FlowLayout.LEFT));
        headerLeft.setBackground(Color.BLACK);

        JButton backToParent = new IconButton(Icons.BACK, 15);
        backToParent.addActionListener(new BackToParent());
        headerLeft.add(backToParent, BorderLayout.WEST);

        TexfieldWithHelper searchField = new TexfieldWithHelper(mainPage.system().getSuggestions());
        searchField.setOnEnter(input -> {
            try {

                mainPage.navigator("/folderPage", new HashMap<String, Object>() {
                    {
                        put("query", input);
                    }
                });
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(null,
                        exception.getMessage(),
                        "Hiba",
                        JOptionPane.ERROR_MESSAGE
                );
            }
            return null;
        });
        searchField.setBackground(Utility.SECONDARY_BG_COLOR);
        searchField.setForeground(Utility.INPUT_FONT_COLOR);
        searchField.setPreferredSize(new Dimension(250, 20));
        String title = mainPage.system().getCurrentParent().getName();
        if(title.equals("root")) title = "Gyökér könyvtár";
        Utility.addPlaceholder(searchField, title + " - Keresés");
        headerLeft.add(searchField);

        header.add(headerLeft,BorderLayout.WEST);

        JPanel headerRight = new JPanel();
        headerRight.setLayout(new FlowLayout(FlowLayout.TRAILING));
        headerRight.setBackground(Color.BLACK);
        //topRight.setPreferredSize(new Dimension(420, 40));

        JButton newFolder = new IconButton(Icons.ADD_FOLDER, 20);
        newFolder.addActionListener(e -> {
            HashMap<String, Object> arguments = new HashMap<>();
            arguments.put("editOrNew", "new");
            mainPage.navigator("/newFolderPage", arguments);
        });
        headerRight.add(newFolder, BorderLayout.EAST);
        JButton newItem = new IconButton(Icons.ADD_ITEM, 20);
        newItem.addActionListener(e -> {
            HashMap<String, Object> arguments = new HashMap<>();
            arguments.put("editOrNew", "new");
            mainPage.navigator("/newItemPage", arguments);
        });
        headerRight.add(newItem, BorderLayout.EAST);
        JButton edit = new IconButton(Icons.EDIT, 20);
        edit.addActionListener(e -> {
            HashMap<String, Object> arguments = new HashMap<>();
            arguments.put("editOrNew", "edit");
            mainPage.navigator("/newFolderPage", arguments);
        });
        headerRight.add(edit, BorderLayout.EAST);
        JButton delete = new IconButton(Icons.DELETE, 20);
        delete.addActionListener(e -> delete());
        headerRight.add(delete, BorderLayout.EAST);
        header.add(headerRight);

        //add utility btns correctly
        Permission perm = Permission.READ;
        for(Account acc : mainPage.system().getCurrentParent().getPermissions().keySet()) {
            if(acc.getName().equals(mainPage.system().getAccount().getName())) {
                perm = mainPage.system().getCurrentParent().getPermissions().get(acc);
            }
        }
        if(perm.lessThan(Permission.EDIT)) {
            newFolder.setEnabled(false);
            newItem.setEnabled(false);
            edit.setEnabled(false);
            delete.setEnabled(false);
        }

        if(mainPage.system().getCurrentParent().getName().equals("root")) {
            newFolder.setEnabled(true);
            newItem.setEnabled(true);
            edit.setEnabled(false);
            delete.setEnabled(false);
        }
//endregion


//region footer
        footer.setLayout(new BorderLayout());
        footer.setBackground(Color.BLACK);
        //footer.setPreferredSize(new Dimension(900, 40));

        JPanel footerLeft = new JPanel();
        footerLeft.setLayout(new FlowLayout(FlowLayout.LEFT));
        footerLeft.setBackground(Color.BLACK);
        //bottomLeft.setPreferredSize(new Dimension(450, 40));

        boolean isAscending = mainPage.system().getComparator().equals(new NameComparatorAsc());
        JButton sort = new IconButton(isAscending ? Icons.SORT_ASC : Icons.SORT_DESC, 15);
        sort.addActionListener(new sort());
        footerLeft.add(sort, BorderLayout.EAST);

        footerLeft.add(pathBuilder());

        footer.add(footerLeft, BorderLayout.WEST);

        JPanel footerRight = new JPanel();
        footerRight.setLayout(new FlowLayout(FlowLayout.TRAILING));
        footerRight.setBackground(Color.BLACK);
        //bottomRight.setPreferredSize(new Dimension(420, 40));

        JButton logout = new IconButton(Icons.LOGOUT, 20);
        logout.addActionListener(e -> {
            mainPage.system().logout();
            mainPage.navigator("/loginPage");
        });
        footerRight.add(logout, BorderLayout.EAST);

        JButton deleteAccount = new IconButton(Icons.DELETE_ACCOUNT, 20);
        deleteAccount.addActionListener(e -> {
            mainPage.system().deleteAccount();
            mainPage.navigator("/loginPage");
        });
        footerRight.add(deleteAccount, BorderLayout.EAST);

        footer.add(footerRight, BorderLayout.EAST);

//endregion

        panel.add(header, BorderLayout.NORTH);
        panel.add(body, BorderLayout.CENTER);
        panel.add(footer, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buildBody() {
        JPanel center = new JPanel();
        center.setLayout(new BorderLayout());
        center.setBackground(Utility.BG_COLOR);

        JPanel centerLeft = new JPanel();
        GridLayout gridLayout = new GridLayout(0, 1);
        gridLayout.setVgap(5);
        centerLeft.setLayout(gridLayout);
        centerLeft.setBackground(Utility.BG_COLOR);


        JScrollPane scrollPane = new JScrollPane(centerLeft);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        center.add(scrollPane, BorderLayout.CENTER);
        try {
            for(Container container : mainPage.system().openContainer(null, query)) {
                JButton button = new ContainerButton(container);
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            if(query != null) {
                                for(Folder ancestor : container.getPath()) {
                                    if(mainPage.system().getCurrentParent().getPath().contains(ancestor)) continue;
                                    if(mainPage.system().getCurrentParent().getName().equals(ancestor.getName())) continue;
                                    mainPage.system().openContainer(ancestor.getName());
                                }
                            }
                            if(container instanceof Folder) {
                                mainPage.system().openContainer(container.getName());
                                mainPage.navigator("/folderPage");
                            } else if(container instanceof Item) {
                                Item item = mainPage.system().openItem(container.getName());
                                HashMap<String, Object> arguments = new HashMap<>();
                                arguments.put("item", item);
                                mainPage.navigator("/itemPage", arguments);
                            }
                        } catch (Exception exception) {
                            JOptionPane.showMessageDialog(null,
                                    exception.getMessage(),
                                    "Hiba",
                                    JOptionPane.ERROR_MESSAGE
                            );
                        }
                    }
                });
                centerLeft.add(button);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel centerRight = new JPanel();
        centerRight.setLayout(new FlowLayout(FlowLayout.RIGHT));
        centerRight.setBackground(Utility.BG_COLOR);
        centerRight.setPreferredSize(new Dimension(0, 40));
        centerRight.setMaximumSize(new Dimension(0, 40));
        centerRight.setBackground(Utility.BG_COLOR);


//        center.add(centerLeft, BorderLayout.WEST);
  //      center.add(centerRight, BorderLayout.EAST);

        return center;
    }

    private JPanel pathBuilder() {
        JPanel path = new JPanel();
        path.setLayout(new FlowLayout());
        path.setBackground(Color.BLACK);
        path.setPreferredSize(new Dimension(400, 40));

        return path;
    }

    private class BackToParent implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Back to parent");
            try {
                mainPage.system().openContainer("..");
                mainPage.navigator("/folderPage");
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(null,
                        exception.getMessage(),
                        "Hiba",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }




    private void addNewItem() {
    }

    private void delete() {
        try{
            mainPage.system().deleteCurrent();
            mainPage.navigator("/folderPage");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    e.getMessage(),
                    "Hiba",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private class sort implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            if(mainPage.system().getComparator().equals(new NameComparatorAsc())) {
                mainPage.system().sortBy(new NameComparatorDesc());
            } else {
                mainPage.system().sortBy(new NameComparatorAsc());
            }
            System.out.println("Sort");
            mainPage.navigator("/folderPage");
        }
    }
}
