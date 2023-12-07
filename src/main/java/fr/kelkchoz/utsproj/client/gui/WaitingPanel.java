package fr.kelkchoz.utsproj.client.gui;

import fr.kelkchoz.utsproj.client.Client;
import fr.kelkchoz.utsproj.client.ViewElement;
import fr.kelkchoz.utsproj.client.gui.helpers.TitleFont;
import fr.kelkchoz.utsproj.shared.DTO;
import fr.kelkchoz.utsproj.shared.Player;

import javax.swing.*;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class WaitingPanel extends ViewElement {
    private Client client;
    private JLabel title;
    private JButton ready;
    private JLabel readyPlayer;
    private static final String textReady = "Ready players: ";

    public WaitingPanel(Client client) {
        this.client = client;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        title = new JLabel("Waiting for players");
        title.setFont(new TitleFont());
        add(title);

        ready = new JButton("Ready");
        ready.addActionListener(x -> {
            try {
                client.send(new DTO(DTO.READY, client.getPlayer()));
                ready.setEnabled(false);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        add(ready);

        readyPlayer = new JLabel(textReady);
        add(readyPlayer);
    }

    @Override
    public void onMessageRecieved(DTO message) {
        switch (message.key) {
            case DTO.READY:
                List<Player> readyPlayers = (List<Player>) message.data;
                readyPlayer.setText(textReady + readyPlayers.stream().map(Player::getName).collect(Collectors.joining(", ")));
                break;
            default:
                break;
        }
    }
}
