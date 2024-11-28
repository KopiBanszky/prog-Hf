package GUI.Account;

import GUI.MainPage;
import GUI.Utilities.Docs;
import GUI.Utilities.Utility;
import System.Account;
import System.MainSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RegisterPage {
    private MainPage mainPage;
    private JTextField name;
    private JTextField pasw;
    private JTextField pasw2;

    public RegisterPage(MainPage mainPage) {
        this.mainPage = mainPage;

        mainPage.setTitle("Prog3 Leltár - Regisztrálás");
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
        center.setPreferredSize(new Dimension(220, 200));


        JLabel label = new JLabel("<html><b>Prog 3 Leltár<br>Regisztrálás</b></html>");
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

        JPasswordField passwordAgainField = new JPasswordField(15);
        pasw2 = passwordAgainField;
        passwordAgainField.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordAgainField.setBackground(Utility.SECONDARY_BG_COLOR);
        passwordAgainField.setForeground(Utility.INPUT_FONT_COLOR);
        passwordAgainField.setEchoChar('*');
        Utility.addPlaceholder(passwordAgainField, "Jelszó újra");
        center.add(passwordAgainField);

        JLabel loginLabel = new JLabel("<html><u>Bejelentkezés</u></html>");
        loginLabel.setForeground(Color.cyan);
        loginLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainPage.navigator("/loginPage");
            }
        });

        center.add(loginLabel);

        JButton registerButton = new JButton("Regisztáció");
        registerButton.addActionListener(new registerButtonActionListener());
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(registerButton, BorderLayout.SOUTH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1;
        gbc.weighty = 1;

        panel.add(center, gbc);
        panel.add(new Docs());

        return panel;
    }

    private void register() {
        if (pasw.getText().equals(pasw2.getText())) {
            Account account = new Account();
            try {
                account.createAccount(name.getText(), pasw.getText());
                mainPage.setSystem(new MainSystem(account));
                mainPage.navigator("/folderPage");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Hiba", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "A két jelszó nem egyezik!", "Hiba", JOptionPane.ERROR_MESSAGE);
        }
    }

    private class registerButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            register();
        }
    }
}
