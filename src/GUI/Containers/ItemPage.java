package GUI.Containers;


import Config.Permission;
import GUI.MainPage;
import GUI.Utilities.AssetImage;
import GUI.Utilities.IconButton;
import GUI.Utilities.Icons;
import GUI.Utilities.Utility;
import System.Tree.Comment;
import System.Tree.Item;
import System.Account;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ItemPage {
    private MainPage mainPage;
    private Item item;

    public ItemPage(MainPage mainPage) {
        this.mainPage = mainPage;

        item = (Item) mainPage.arguments().get("item");
        mainPage.setTitle("Prog3 Leltár - Tárgy");
        JPanel panel = builder();
        mainPage.build(panel);
    }
    public ItemPage(MainPage mainPage, boolean newPage) {
        this.mainPage = mainPage;

        item = (Item) mainPage.arguments().get("item");
        mainPage.setTitle("Prog3 Leltár - Tárgy");
        JPanel panel = builder();
        if(newPage) mainPage.build(panel);
    }

    public JPanel builder() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Utility.BG_COLOR);

        JPanel header = new JPanel();
        JPanel footer = new JPanel();
        JPanel body = null;
        try {
            body = buildBody();
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(null,
                    exception.getMessage(),
                    "Hiba",
                    JOptionPane.ERROR_MESSAGE
            );
        }

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

        JLabel IDfield = new JLabel();
        IDfield.setForeground(Utility.FONT_COLOR);
        IDfield.setPreferredSize(new Dimension(250, 20));
        IDfield.setText(item.getReadableId());
        IDfield.setFont(new Font("Arial", Font.BOLD, 15));
        headerLeft.add(IDfield);

        header.add(headerLeft,BorderLayout.WEST);

        JPanel headerRight = new JPanel();
        headerRight.setLayout(new FlowLayout(FlowLayout.TRAILING));
        headerRight.setBackground(Color.BLACK);
        //topRight.setPreferredSize(new Dimension(420, 40));

        JButton edit = new IconButton(Icons.EDIT, 20);
        edit.addActionListener(e -> {
            HashMap<String, Object> arguments = new HashMap<>();
            arguments.put("editOrNew", "edit");
            arguments.put("item", item);
            mainPage.navigator("/newItemPage", arguments);
        });
        headerRight.add(edit, BorderLayout.EAST);
        JButton delete = new IconButton(Icons.DELETE, 20);
        delete.addActionListener(e -> {
            try {
                mainPage.system().getCurrentParent().removeChild(item, mainPage.system().getAccount());
                mainPage.navigator("/folderPage");
            } catch (Exception exception) {
                exception.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        exception.getMessage(),
                        "Hiba",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
        headerRight.add(delete, BorderLayout.EAST);
        header.add(headerRight);

        Permission perm = Permission.READ;
        for(Account acc : item.getPermissions().keySet()) {
            if(acc.getName().equals(mainPage.system().getAccount().getName())) {
                perm = item.getPermissions().get(acc);
            }
        }
        if(perm.lessThan(Permission.EDIT)) {
            edit.setEnabled(false);
            delete.setEnabled(false);
        }
//endregion


        panel.add(header, BorderLayout.NORTH);
        panel.add(body, BorderLayout.CENTER);

        return panel;
    }

    private JPanel buildBody() throws Exception{
        JPanel body = new JPanel();
        body.setLayout(new BorderLayout());
        body.setBackground(Utility.BG_COLOR);

        //region top setup
        JPanel top = new JPanel();
        top.setLayout(new BorderLayout());
        top.setOpaque(false);

        JPanel topLeft = new JPanel();
        topLeft.setLayout(new FlowLayout(FlowLayout.LEFT));
        topLeft.setOpaque(false);

        JPanel topRight = new JPanel();
        topRight.setLayout(new FlowLayout(FlowLayout.RIGHT));
        topRight.setOpaque(false);
        //endregion

        //region scroll body setup

        JPanel scrollBody = new JPanel();
        scrollBody.setLayout(new BoxLayout(scrollBody, BoxLayout.Y_AXIS));
        scrollBody.setOpaque(false);

        JPanel commentSection = new JPanel();
        commentSection.setLayout(new BoxLayout(commentSection, BoxLayout.Y_AXIS));
        commentSection.setOpaque(false);

        JPanel newCommentSection = new JPanel();
        newCommentSection.setLayout(new FlowLayout(FlowLayout.LEFT));
        newCommentSection.setOpaque(false);
        commentSection.add(newCommentSection, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(scrollBody);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        //endregion

        JLabel name = new JLabel("Név: " + item.getName());
        name.setForeground(Utility.FONT_COLOR);
        topLeft.add(name);

        JLabel time = new JLabel("Módosítás: " + item.getReadableLastModified());
        time.setForeground(Utility.FONT_COLOR);
        topRight.add(time);

        JTextArea description = new JTextArea();
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setEditable(false);
        description.setOpaque(false);
        description.setForeground(Utility.FONT_COLOR);
        description.setText("Leírás:\n" + item.getDescription(mainPage.system().getAccount()));
        scrollBody.add(description);

        scrollBody.add(imgBuilder());

        scrollBody.add(longTextBuilder(item.getText(mainPage.system().getAccount())));

        JTextField newComment = new JTextField();
        newComment.setPreferredSize(new Dimension(500, 20));
        newCommentSection.add(newComment);

        JButton addComment = new JButton("Hozzáadás");
        addComment.addActionListener(e -> addComment(commentSection, newComment.getText(), mainPage.system().getAccount()));
        newCommentSection.add(addComment);

        item.getComments(
                mainPage.system().getAccount())
                .forEach(c ->
                        commentSection.add(commentBuilder(commentSection, c.getComment(), c.getWriter(), c)
                        )
                );

        scrollBody.add(commentSection);

        top.add(topLeft, BorderLayout.WEST);
        top.add(topRight, BorderLayout.EAST);
        body.add(top, BorderLayout.NORTH);
        body.add(scrollPane, BorderLayout.CENTER);


        return body;
    }

    //TODO: implement
    private JPanel imgBuilder() {
        JPanel img = new JPanel();
        JPanel panel = new JPanel();

        img.setLayout(new BorderLayout());
        img.setOpaque(false);
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setOpaque(false);
        scrollPane.setBackground(Utility.BG_COLOR);
        scrollPane.getViewport().setOpaque(false);
        img.setBackground(Utility.BG_COLOR);
        panel.setBackground(Utility.BG_COLOR);

        try {
            ArrayList<String> imgs = item.getImgs(mainPage.system().getAccount());
            int size = 10;
            for (String path : imgs) {
                AssetImage assetImage = new AssetImage(path, -1, 150);
                panel.add(assetImage);
                size += assetImage.getImgWidth() + 10;
                panel.setPreferredSize(new Dimension(size, 170));
                scrollPane.setPreferredSize(new Dimension(850, 170));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        img.add(scrollPane, BorderLayout.CENTER);

        return img;
    }

    private JTextArea longTextBuilder(String text) {
        JTextArea area = new JTextArea();
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);
        area.setOpaque(false);
        area.setForeground(Utility.FONT_COLOR);
        area.setText(text);
        return area;
    }

    private void addComment(JPanel container, String comment, Account account) {
        Comment nComment = item.addComment(comment, account);
        JPanel commentPanel = commentBuilder(container, comment, account, nComment);

        container.add(commentPanel);
        container.revalidate();
        container.repaint();

    }

    private JPanel commentBuilder(JPanel container, String comment, Account account, Comment comment1) {
        JPanel commentPanel = new JPanel();
        commentPanel.setLayout(new BorderLayout());
        commentPanel.setOpaque(false);
        commentPanel.setPreferredSize(new Dimension(200, 30));

        JLabel commentLabel = new JLabel(account.getName() + ": " + comment);
        commentLabel.setForeground(Utility.FONT_COLOR);
        commentLabel.setForeground(comment1.isResolved() ? Color.green : Color.WHITE);
        commentPanel.add(commentLabel, BorderLayout.CENTER);

        JButton delete = new IconButton(Icons.DELETE, 15);
        delete.addActionListener(e -> {
            item.resolveComment(comment1, mainPage.system().getAccount());
            container.remove(commentPanel);
            container.add(commentBuilder(container, comment, account, comment1));
            container.revalidate();
            container.repaint();
        });
        if(!comment1.isResolved()) commentPanel.add(delete, BorderLayout.EAST);
        return commentPanel;
    }
}
