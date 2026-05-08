package library.gui;

import javax.swing.JFrame;
import javax.swing.JTextField;

public interface KeyboardInputHandler {
    void setupKeyBindings(JFrame frame, JTextField textField);
    void Enterperformed(String input);
}
