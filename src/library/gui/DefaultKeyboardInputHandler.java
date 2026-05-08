package library.gui;

import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.Timer;

import jp.recoarder.ikd.assets.Sound;

public abstract class DefaultKeyboardInputHandler implements KeyboardInputHandler {

    @Override
    public void setupKeyBindings(JFrame frame, JTextField textField) {

        InputMap inputMap = frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = frame.getRootPane().getActionMap();

        String loginActionKey = "loginByEnter";

        // Enterキー処理
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), loginActionKey);
        actionMap.put(loginActionKey, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Enterperformed(textField.getText().toUpperCase());
                textField.setText("");
            }
        });

        // キーリスナー
        frame.addKeyListener(new KeyAdapter() {

            @Override
            public void keyTyped(KeyEvent e) {
                char keyChar = e.getKeyChar();

                if (!textField.hasFocus()
                        && Character.isDefined(keyChar)
                        && !Character.isISOControl(keyChar)) {

                    textField.setText(textField.getText() + keyChar);
                    Sound.touch();
                    e.consume();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {

                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE
                        && !textField.hasFocus()) {

                    String currentText = textField.getText();
                    if (!currentText.isEmpty()) {
                        textField.setText(currentText.substring(0, currentText.length() - 1));
                    }
                    e.consume();
                }
            }
        });

        frame.setFocusable(true);
        frame.requestFocusInWindow();

        new Timer(500, e -> frame.requestFocusInWindow()).start();

        textField.setColumns(10);
    }
}