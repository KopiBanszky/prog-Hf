package GUI.Utilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class Utility {
    public final static Color BG_COLOR = Color.darkGray;
    public final static Color SECONDARY_BG_COLOR = Color.white;
    public final static Color FONT_COLOR = Color.white;
    public final static Color INPUT_FONT_COLOR = Color.black;
    public final static Color SECONDARY_FONT_COLOR = Color.gray;


    public static void addPlaceholder(JTextField textfield, String placeholder) {
        textfield.setText(placeholder);
        textfield.setForeground(Utility.SECONDARY_FONT_COLOR);
        if(textfield instanceof JPasswordField) {
            ((JPasswordField) textfield).setEchoChar((char) 0);
        }
        textfield.addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent evt) {
                if (textfield.getText().equals(placeholder)) {
                    textfield.setText("");
                    textfield.setForeground(Utility.INPUT_FONT_COLOR);
                    if(textfield instanceof JPasswordField) {
                        ((JPasswordField) textfield).setEchoChar('*');
                    }
                }
            }

            @Override
            public void focusLost(FocusEvent evt) {
                if (textfield.getText().isEmpty()) {
                    textfield.setText(placeholder);
                    textfield.setForeground(Utility.SECONDARY_FONT_COLOR);
                    if(textfield instanceof JPasswordField) {
                        ((JPasswordField) textfield).setEchoChar((char) 0);
                    }
                }
            }
        });
    }

    public static void addPlaceholder(JTextArea textfield, String placeholder) {
        textfield.setText(placeholder);
        textfield.setForeground(Utility.SECONDARY_FONT_COLOR);
        textfield.addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent evt) {
                if (textfield.getText().equals(placeholder)) {
                    textfield.setText("");
                    textfield.setForeground(Utility.INPUT_FONT_COLOR);
                }
            }

            @Override
            public void focusLost(FocusEvent evt) {
                if (textfield.getText().isEmpty()) {
                    textfield.setText(placeholder);
                    textfield.setForeground(Utility.SECONDARY_FONT_COLOR);
                }
            }
        });
    }

}
