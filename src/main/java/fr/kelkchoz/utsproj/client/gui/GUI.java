package fr.kelkchoz.utsproj.client.gui;

import fr.kelkchoz.utsproj.client.Client;
import fr.kelkchoz.utsproj.client.ViewElement;
import fr.kelkchoz.utsproj.shared.DTO;
import fr.kelkchoz.utsproj.shared.Player;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.View;
import java.awt.*;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class GUI extends JFrame {

    public static final int WIDTH = 800;
    public static final int HEIGHT = 500;

    private List<ViewElement> viewElementList = new LinkedList<>();

    private final Client client;
    public GUI(Client client){
        this.client = client;

        setupView();
    }

    private ViewElement gamePanel;

    private void setupView() {
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle(client.getPlayer().getName());
        this.setSize(WIDTH,HEIGHT);
        this.setVisible(true);

        //view
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.X_AXIS));


        this.addPanel(new PlayerListView(client));

        gamePanel = new WaitingPanel(client);
        this.addPanel(gamePanel);

        this.addPanel(new ChatPanel(client));

    }

    private void addPanel(ViewElement element) {
        this.add(element);
        viewElementList.add(element);
    }

    private int removePanel(ViewElement element) {
        int index = element.getParent().getComponentZOrder(gamePanel);
        viewElementList.remove(element);
        this.remove(element);
        return index;
    }

    private void addPanel(ViewElement element, int index) {
        this.add(element, index);
        viewElementList.add(element);
    }


    public void onMessageRecieved(DTO message) {
        if (Objects.equals(message.key, DTO.START_GAME)) {
            int index = removePanel(gamePanel);
            gamePanel = new GamePanel(client);
            addPanel(gamePanel, index);
            this.validate();
            this.repaint();
            return;
        }
        if(Objects.equals(message.key, DTO.RESULT)) {
            int index = removePanel(gamePanel);
            gamePanel = new EndPanel(client, (List<Player>) message.data, this::getBackHome);
            addPanel(gamePanel, index);
            this.validate();
            this.repaint();
            return;
        }
        for (ViewElement elem: viewElementList) {
            elem.onMessageRecieved(message);
        }
    }

    private Void getBackHome(Void unused) {
        int index = removePanel(gamePanel);
        gamePanel = new WaitingPanel(client);
        addPanel(gamePanel, index);
        this.validate();
        this.repaint();

        return unused;
    }
}