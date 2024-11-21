package GUI.Utilities;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.function.Function;

public class TexfieldWithHelper extends JTextField {
    private ArrayList<String> suggestionsList = new ArrayList<>();

    private Function<String, Void> onEnter = null;
    private Function<String, Void> onHelperClick = null;

    public TexfieldWithHelper(ArrayList<String> suggestions) {
        super();
        suggestionsList.addAll(suggestions);

        JPopupMenu popupMenu = new JPopupMenu();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String input = getText();

                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if(onEnter != null) {
                        onEnter.apply(input);
                    }
                    setText("");
                    return;
                }

                if(input.isEmpty()) {
                    popupMenu.setVisible(false);
                    return;
                }

                ArrayList<String> filteredSuggestions = new ArrayList<>();
                for(String suggestion : suggestionsList) {
                    if(suggestion.toLowerCase().contains(input)) {
                        filteredSuggestions.add(suggestion);
                    }
                }

                popupMenu.removeAll();
                for(String suggestion : filteredSuggestions) {
                    JMenuItem item = new JMenuItem(suggestion);
                    item.addActionListener(e1 -> {
                        setText(suggestion);
                        popupMenu.setVisible(false);
                    });
                    popupMenu.add(item);
                }

                if(!filteredSuggestions.isEmpty()) {
                    popupMenu.show(TexfieldWithHelper.this, 0, getHeight());
                } else {
                    popupMenu.setVisible(false);
                }
                requestFocus();
            }
        });
    }

    public void setOnEnter(Function<String, Void> onEnter) {
        this.onEnter = onEnter;
    }

    public void setOnHelperClick(Function<String, Void> onHelperClick) {
        this.onHelperClick = onHelperClick;
    }
}
