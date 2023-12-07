package fr.kelkchoz.utsproj.client.gui;

import fr.kelkchoz.utsproj.client.Client;
import fr.kelkchoz.utsproj.client.ViewElement;
import fr.kelkchoz.utsproj.shared.DTO;
import fr.kelkchoz.utsproj.shared.Player;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class ChatPanel extends ViewElement {
    private TextArea serv;
    private TextField cl;

    private Client client;

    public ChatPanel(Client client) {
        this.client = client;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setSize(150, HEIGHT);
        serv = new TextArea();
        serv.setEditable(false);
        serv.setSize(50,500);
        cl = new TextField();
        add(serv);
        add(cl);


        cl.addActionListener(e -> {
            try {
                client.sendMessage(cl.getText());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            cl.setText("");
        });
    }


    @Override
    public void onMessageRecieved(DTO message) {
        switch (message.key){
            case DTO.CHAT:
                serv.append(message.data + "\n");
                break;
            case DTO.DISCONNECT:
                Player player = (Player) message.data ;
                serv.append(player.getName() + " just left the game with " + player.getScore() + "\n");
                break;
        }
    }
}
