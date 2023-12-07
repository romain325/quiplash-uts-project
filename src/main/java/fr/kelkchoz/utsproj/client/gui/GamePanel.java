package fr.kelkchoz.utsproj.client.gui;

import fr.kelkchoz.utsproj.client.Client;
import fr.kelkchoz.utsproj.client.ViewElement;
import fr.kelkchoz.utsproj.client.gui.helpers.H2Font;
import fr.kelkchoz.utsproj.client.gui.helpers.TextChangeListener;
import fr.kelkchoz.utsproj.shared.DTO;
import fr.kelkchoz.utsproj.shared.GameState;
import fr.kelkchoz.utsproj.shared.Player;

import javax.swing.*;
import javax.swing.plaf.BorderUIResource;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class GamePanel extends ViewElement {
    private Client client;
    private final JLabel currentQuestion = new JLabel("The next question will be");
    private final JLabel timer = new JLabel("timer");
    private final JLabel stage = new JLabel("stage");

    private final JPanel answerContainer = new JPanel();
    private Map<Player, String> answers = new HashMap<>();
    private final Map<Player, JLabel> answersView = new HashMap<>();

    private final JTextField playerAnswer = new JTextField();

    private boolean isVote;

    public GamePanel(Client client) {
        this.client = client;
        setSize(500, HEIGHT);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        currentQuestion.setFont(new H2Font());
        timer.setForeground(Color.RED);

        add(currentQuestion);
        add(stage);
        add(timer);

        // add other player answers
        answerContainer.setLayout(new BoxLayout(answerContainer, BoxLayout.Y_AXIS));
        add(answerContainer);

        playerAnswer.setSize(500, 30);
        add(playerAnswer);
        playerAnswer.getDocument().addDocumentListener(new TextChangeListener() {
            @Override
            public void callback() {
                if(!isVote) {
                    try {
                        client.send(new DTO(DTO.GAME_RUNNING, playerAnswer.getText()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    @Override
    public void onMessageRecieved(DTO message) {
        if (Objects.equals(message.key, DTO.GAME_RUNNING) || Objects.equals(message.key, DTO.VOTE)) {
            GameState state = (GameState) message.data;
            this.currentQuestion.setText("<html><h2>" + state.getCurrentQuestion() + "</h2></html>");
            this.timer.setText(Integer.toString(state.getTimer()));
            timer.revalidate();
            answers = state.getPlayersAnswer();

            if(answersView.isEmpty()) {
                for (Player p : answers.keySet()) {
                    JLabel tmp = new JLabel(answers.get(p));
                    tmp.setBorder(new BorderUIResource.BevelBorderUIResource(1));
                    tmp.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            if(isVote && !client.getPlayer().equals(p)){
                                for (Component component : answerContainer.getComponents()) {
                                    component.setBackground(Color.WHITE);
                                }
                                tmp.setBackground(Color.DARK_GRAY);

                                try {
                                    client.send(new DTO(DTO.VOTE, p));
                                } catch (IOException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                        }
                    });

                    answerContainer.add(tmp);
                    answersView.put(p, tmp);
                }
            }

            for (Player p : answers.keySet()) {
                String rep = answers.get(p);
                if (rep != null) {
                    answersView.get(p).setText(rep);
                }
            }

            if (Objects.equals(message.key, DTO.GAME_RUNNING)) {
                isVote = false;
                this.stage.setText("Write your answer");
                for (Component component : answerContainer.getComponents()) {
                    component.setBackground(Color.WHITE);
                }

                playerAnswer.setEditable(true);
            } else {
                isVote = true;
                this.stage.setText("Voting");
                this.playerAnswer.setText("");
                playerAnswer.setEditable(false);
            }

            this.validate();
            this.repaint();
        }
    }
}
