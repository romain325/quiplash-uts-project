package fr.kelkchoz.utsproj.client.gui.helpers;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public abstract class TextChangeListener implements DocumentListener {


    @Override
    public void insertUpdate(DocumentEvent e) {
        callback();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        callback();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        callback();
    }

    public abstract void callback();
}
