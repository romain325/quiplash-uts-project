package fr.kelkchoz.utsproj.client.gui;

import fr.kelkchoz.utsproj.client.Client;
import fr.kelkchoz.utsproj.client.ViewElement;
import fr.kelkchoz.utsproj.client.gui.helpers.TitleFont;
import fr.kelkchoz.utsproj.shared.DTO;
import fr.kelkchoz.utsproj.shared.Player;
import javafx.util.Callback;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Callable;

public class EndPanel extends ViewElement {
    private Client client;
    private List<Player> players;

    private JLabel title = new JLabel("Results");
    private JPanel results = new JPanel();
    private JButton home = new JButton("Home");


    public EndPanel(Client client, List<Player> playerList, Callback<Void, Void> getHome) {
        this.client = client;
        this.players = playerList;

        setSize(500, GUI.HEIGHT);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        title.setFont(new TitleFont());

        results.setLayout(new BoxLayout(results, BoxLayout.Y_AXIS));
        for (Player player: playerList) {
            JLabel tmp = new JLabel(player.getName() + ":\t\t" + player.getScore());
            results.add(tmp);
        }
        results.getComponents()[0].setBackground(Color.GREEN);

        home.addActionListener(e -> {
            try {
                getHome.call(null);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        add(title);
        add(results);
        add(home);
    }

    @Override
    public void onMessageRecieved(DTO message) {

    }
}
