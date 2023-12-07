package fr.kelkchoz.utsproj.server;


import fr.kelkchoz.utsproj.shared.DTO;
import fr.kelkchoz.utsproj.shared.Player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class RequestHandler extends Thread {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private final Client client;
    private final List<Client> clients;
    private final GameTimer gameTimer;

    public RequestHandler(Socket socket, List<Client> clients, GameTimer timer) throws IOException {
        this.socket = socket;
        this.clients = clients;

        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
        this.client = new Client(null, out);

        this.gameTimer = timer;

        // start the thread lifecycle
        start();
    }

    @Override
    public void run() {
        try {
            DTO data = (DTO) in.readObject();
            System.out.println(data);
            if (Objects.equals(data.key, DTO.CONNECT)) {
                client.setPlayer((Player) data.data);
            } else {
                throw new IllegalArgumentException("Error while connecting");
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return;
        }

        clients.add(client);
        System.out.println("New client created: " + client.getPlayer().getName());
        try {
            ServerUtils.sendObject(clients, new DTO(DTO.CONNECT, (Serializable) clients.stream().map(Client::getPlayer).collect(Collectors.toList())));
        } catch (IOException e) {
            System.err.println("Error while connecting a new client");
            e.printStackTrace();
        }

        try {
            while (true) {
                DTO str = null;
                try {
                    str = (DTO) in.readObject();
                    System.out.println(str);
                } catch (ClassNotFoundException e) {
                    System.out.println("err while reading object");
                }
                if (str == null) {
                    break;
                }
                switch (str.key) {
                    case DTO.CHAT -> ServerUtils.sendMessage(clients, client.getPlayer().getName() + ": " + str.data);
                    case DTO.DISCONNECT -> {
                        throw new IOException("Disconnecting player");
                    }
                    case DTO.READY -> {
                        client.getPlayer().setReady(true);
                        if (clients.size() > 1 && clients.size() == clients.stream().filter(x -> x.getPlayer().isReady()).count()) {
                            // start game
                            ServerUtils.sendObject(clients, new DTO(DTO.START_GAME, null));
                            gameTimer.startGame();
                        } else {
                            ServerUtils.sendObject(clients, new DTO(DTO.READY, (Serializable) clients.stream().map(Client::getPlayer).filter(Player::isReady).collect(Collectors.toList())));
                        }
                    }
                    case DTO.GAME_RUNNING -> {
                        gameTimer.updatePlayerAnswer(client.getPlayer(), (String) str.data);
                        // here i could do real time text but nobody cares so keep it with 1sec refresh thx to the game timer
                    }
                    case DTO.VOTE -> {
                        gameTimer.updatePlayerVote(client.getPlayer(), (Player) str.data);
                        // maybe here the vote is important to refresh, we'll see
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Client connection lost for " + client.getPlayer().getName());
        } finally {
            try {
                clients.remove(client);
                ServerUtils.sendObject(clients, new DTO(DTO.DISCONNECT, client.getPlayer()));
                socket.close();
            } catch (IOException e) {
            }
        }
    }
}
