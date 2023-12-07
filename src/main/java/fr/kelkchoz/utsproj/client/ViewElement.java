package fr.kelkchoz.utsproj.client;

import fr.kelkchoz.utsproj.shared.DTO;

import javax.swing.*;
import java.awt.*;

public abstract class ViewElement extends JPanel {
    public abstract void onMessageRecieved(DTO message);
}
