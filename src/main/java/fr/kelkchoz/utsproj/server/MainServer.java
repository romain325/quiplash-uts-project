package fr.kelkchoz.utsproj.server;

import fr.kelkchoz.utsproj.shared.GameState;
import fr.kelkchoz.utsproj.shared.VoteState;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class MainServer {
    public static final int PORT = 42069;
    public static final int MAX_CAPACITY = 4;
    private static final List<Client> CLIENTS = Collections.synchronizedList(new ArrayList<>(MAX_CAPACITY));
    private static GameTimer timer;


    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        timer = new GameTimer(CLIENTS);
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                try {
                    // Serve one client request;
                    new RequestHandler(socket, CLIENTS, timer);
                } catch (IOException e) {
                    socket.close();
                }
            }
        } finally {
            serverSocket.close();
        }
    }
}