package GUI;


import GUI.Utilities.Utility;
import System.MainSystem;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class MainPage extends JFrame {
    private JPanel panel;
    private JLabel label;
    private MainSystem system;
    private HashMap<String, Object> arguments;


    public MainPage() {
        setTitle("Home Page");
        setSize(900, 500);

        setResizable(false);
        setDefaultCloseOperation(
            JFrame.EXIT_ON_CLOSE
        );

        panel = new JPanel();
        label = new JLabel("Welcome to the Home Page!");
        label.setForeground(Color.white);
        panel.add(label);
        add(panel);
        panel.setBackground(Utility.BG_COLOR);

        openOnSecondDisplay();
        setVisible(true);
    }

    public void navigator(String path, HashMap<String, Object> arguments) {
        this.arguments = arguments;
        navigator("_argumentsOn_-" + path);
    }

    public void navigator(String path) {
        if(path.startsWith("_argumentsOn_-")) {
            path = path.substring(14);
        } else {
            this.arguments = new HashMap<>();
        }
        switch (path) {
            case "/":
                label.setText("Welcome to the Home Page!");
                break;
            case "/loginPage":
                new GUI.Account.LoginPage(this);
                break;
            case "/registerPage":
                new GUI.Account.RegisterPage(this);
                break;
            case "/folderPage":
                new GUI.Containers.FolderPage(this);
                break;
            case "/itemPage":
                new GUI.Containers.ItemPage(this);
                break;
            case "/newFolderPage":
                new GUI.ModificationPages.NewFolderPage(this);
                break;
            case "/newItemPage":
                new GUI.ModificationPages.NewItemPage(this);
                break;
        }
    }

    public void build(JPanel panel) {
        remove(this.panel);
        this.panel = panel;
        add(panel);
        setVisible(true);
    }

    private void openOnSecondDisplay() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screens = ge.getScreenDevices();
        if (screens.length > 1) {
            GraphicsDevice secondScreen = screens[1];
            Rectangle bounds = secondScreen.getDefaultConfiguration().getBounds();

            setLocation(bounds.x+50, bounds.y+50);
        }
    }

    public MainSystem system() {
        return system;
    }
    public HashMap<String, Object> arguments() {
        return arguments;
    }

    public void setSystem(MainSystem system) {
        this.system = system;
    }
}
