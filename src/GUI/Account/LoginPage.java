package GUI.Account;

import GUI.MainPage;
import GUI.Utilities.Utility;
import System.Account;
import System.MainSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginPage {
    private MainPage mainPage;
    private JTextField name;
    private JTextField pasw;

    public LoginPage(MainPage mainPage) {
        this.mainPage = mainPage;

        mainPage.setTitle("Prog3 Leltár - Bejelentkezés");
        JPanel panel = builder();

        mainPage.build(panel);
    }

    private JPanel builder() {

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(Utility.BG_COLOR);

        JPanel center = new JPanel();
        center.setLayout(new FlowLayout());
        center.setBackground(Utility.BG_COLOR);
        center.setPreferredSize(new Dimension(200, 150));


        JLabel label = new JLabel("<html><b>Prog 3 Leltár<br>Bejelentkezés</b></html>");
        label.setForeground(Utility.FONT_COLOR);
        label.setFont(new Font("Arial", Font.ITALIC, 20));
        center.add(label, BorderLayout.NORTH);

        JTextField usernameField = new JTextField(15);
        name = usernameField;
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameField.setBackground(Utility.SECONDARY_BG_COLOR);
        usernameField.setForeground(Utility.INPUT_FONT_COLOR);
        Utility.addPlaceholder(usernameField, "Felhasználónév");
        center.add(usernameField);

        JPasswordField passwordField = new JPasswordField(15);
        pasw = passwordField;
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField.setBackground(Utility.SECONDARY_BG_COLOR);
        passwordField.setForeground(Utility.INPUT_FONT_COLOR);
        passwordField.setEchoChar('*');
        Utility.addPlaceholder(passwordField, "Jelszó");
        center.add(passwordField);

        JLabel registerLabel = new JLabel("<html><u>Regisztrálás</u></html>");
        registerLabel.setForeground(Color.cyan);
        registerLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        registerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainPage.navigator("/registerPage");
            }
        });
        center.add(registerLabel);

        JButton loginButton = new JButton("Bejelentkezés");
        loginButton.addActionListener(new loginButtonActionListener());
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(loginButton, BorderLayout.SOUTH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1;
        gbc.weighty = 1;

        panel.add(center, gbc);

        return panel;
    }

    private void login() {
        Account account = new Account();

        try {
            account.login(name.getText(), pasw.getText());
            mainPage.setSystem(new MainSystem(account));
            mainPage.navigator("/folderPage");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    e.getMessage(),
                    "Hiba",
                    JOptionPane.ERROR_MESSAGE
            );
        }

    }

    private class loginButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            login();
        }
    }
}
