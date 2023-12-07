package fr.kelkchoz.utsproj.server;

import fr.kelkchoz.utsproj.shared.Player;

import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Client {
    private Player player;
    private ObjectOutputStream out;

    public Client(String name, ObjectOutputStream printWriter) {
        this.player = new Player(name, 0);
        this.out = printWriter;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    public void setOut(ObjectOutputStream out) {
        this.out = out;
    }
}
