package fr.kelkchoz.utsproj.client.gui;

import fr.kelkchoz.utsproj.client.Client;
import fr.kelkchoz.utsproj.client.ViewElement;
import fr.kelkchoz.utsproj.shared.DTO;
import fr.kelkchoz.utsproj.shared.Player;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PlayerListView extends ViewElement {

    @Override
    public void onMessageRecieved(DTO message) {
        switch (message.key) {
            case DTO.CONNECT:
                setPlayers((List<Player>) message.data);
                break;
            case DTO.DISCONNECT:
                removePlayer((Player) message.data);
                break;
            case DTO.SCORE:
                updateScore((List<Player>) message.data);
                break;
            default:
                break;
        }
    }

    public void updateScore(List<Player> players) {
        System.out.println(players);
        for (Player player : players) {
            JLabel scoreField = playerList.get(player).scoreField;
            scoreField.setText(Integer.toString(player.getScore()));
            scoreField.revalidate();
            scoreField.repaint();
        }
    }

    public static class PlayerItem extends JPanel {
        public PlayerItem(Player player) {
            nameField.setText(player.getName());
            scoreField.setText(Integer.toString(player.getScore()));
            this.setSize(100,50);
            nameField.setSize(50,50);
            scoreField.setSize(50,50);
            add(nameField);
            add(scoreField);
        }

        private JLabel nameField = new JLabel();
        private JLabel scoreField = new JLabel();

        public void setScore(int score) {
            scoreField.setText(Integer.toString(score));
        }
    }
    private Map<Player, PlayerItem> playerList = new HashMap<>();

    private Client client;

    public PlayerListView(Client client) throws HeadlessException {
        super();
        this.client = client;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setSize(100,50);
    }

    public void setPlayers(List<Player> players) {
        for (Player player: players) {
            if(playerList.containsKey(player)){
                Player currentP = playerList.keySet().stream().filter(player1 -> player1.equals(player)).findFirst().get();
                if(currentP.getScore() != player.getScore()) {
                    currentP.setScore(player.getScore());
                    playerList.get(currentP).setScore(player.getScore());
                }
            } else {
                PlayerItem item = new PlayerItem(player);
                playerList.put(player, item);
                this.add(item);
                revalidate();
            }
        }
        playerList.get(client.getPlayer()).setBackground(Color.lightGray);
    }

    public void removePlayer(Player player) {
        this.remove(playerList.get(player));
        playerList.remove(player);
        revalidate();
    }
}
