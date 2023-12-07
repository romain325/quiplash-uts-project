package fr.kelkchoz.utsproj.client;

import fr.kelkchoz.utsproj.client.gui.GUI;

import javax.swing.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainClient {
    public static final String ADDRESS = "127.0.0.1";

    public static void main(String[] args) {
        Client cl = new Client();
        try {
            cl.init();
        }catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "Error while setting up the socket");
        }

        GUI g = new GUI(cl);
        ServerListener serverListener = new ServerListener(cl, g);
        serverListener.run();
    }
}
