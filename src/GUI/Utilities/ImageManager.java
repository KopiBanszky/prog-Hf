package GUI.Utilities;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import System.Tree.Item;
import System.Account;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class ImageManager extends JPanel {
    private ArrayList<String> imgs;
    private ArrayList<String> newImgs;
    private ArrayList<String> deletedImgs;
    private JPanel panel;
    private JScrollPane scrollPane;
    private int width = 440;

    public ImageManager() {
        super();
        imgs = new ArrayList<>();
        newImgs = new ArrayList<>();
        deletedImgs = new ArrayList<>();

        panel = new JPanel();

        setLayout(new BorderLayout());
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));

        scrollPane = new JScrollPane(panel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);


        addImgs();

        JButton addImg = ImgAdder();
        panel.add(addImg);

        add(scrollPane, BorderLayout.CENTER);

    }

    public ImageManager(Item item, Account account){
        super();
        try {
            imgs = item.getImgs(account);
        } catch (Exception e) {
            e.printStackTrace();
        }
        newImgs = new ArrayList<>();
        deletedImgs = new ArrayList<>();


        panel = new JPanel();

        setLayout(new BorderLayout());
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));

        scrollPane = new JScrollPane(panel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);


        addImgs();

        JButton addImg = ImgAdder();
        panel.add(addImg);

        add(scrollPane, BorderLayout.CENTER);
    }

    public void addImgs() {
        width = 110;
        for(String path : imgs) {
            panel.add(imgBuilder(path), FlowLayout.LEFT);
        }
        updatePanelSize();
    }

    private JPanel imgBuilder(String path) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Utility.BG_COLOR);

        //JPanel img = new AssetImage(path, 100, 100);

        AssetImage img = new AssetImage(path, -1, 100);
        panel.add(img);
        width += img.getImgWidth() + 10;


        JButton delete = new IconButton(Icons.DELETE, 25);
        delete.addActionListener(e -> {
            imgs.remove(path);
            if(newImgs.contains(path)) {
                newImgs.remove(path);
            } else {
                deletedImgs.add(path);
            }
            this.scrollPane.remove(panel);
            this.panel.remove(panel);
            updatePanelSize();
            this.panel.validate();
            this.panel.repaint();
        });
        panel.add(delete, FlowLayout.CENTER);

        return panel;
    }

    private JButton ImgAdder() {
        JButton button = new IconButton(Icons.ADD_ITEM, 90);
        button.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setMultiSelectionEnabled(true);
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(java.io.File f) {
                    if (f.isDirectory()) {
                        return true;
                    }

                    String extension = f.getName().substring(f.getName().lastIndexOf(".") + 1);
                    return extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg") || extension.equalsIgnoreCase("png");
                }

                @Override
                public String getDescription() {
                    return "Image files";
                }
            });

            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                for (java.io.File file : fileChooser.getSelectedFiles()) {
                    imgs.add(file.getAbsolutePath());
                    newImgs.add(file.getAbsolutePath());
                    panel.remove(button);
                    panel.add(imgBuilder(file.getAbsolutePath()), FlowLayout.LEFT);
                    panel.add(ImgAdder());
                    updatePanelSize();
                    panel.validate();
                    panel.repaint();
                }
            }
        });

        return button;
    }

    private void updatePanelSize() {
        panel.setPreferredSize(new Dimension(width, 160));
        scrollPane.setPreferredSize(new Dimension(450, 160));
        scrollPane.revalidate();
        scrollPane.repaint();
    }

    public ArrayList<String> getNewImgs() {
        return newImgs;
    }

    public ArrayList<String> getDeletedImgs() {
        return deletedImgs;
    }

    public ArrayList<String> getImgs() {
        return imgs;
    }
}
