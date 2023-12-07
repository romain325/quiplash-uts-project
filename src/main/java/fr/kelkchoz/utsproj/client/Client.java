package fr.kelkchoz.utsproj.client;

import fr.kelkchoz.utsproj.server.MainServer;
import fr.kelkchoz.utsproj.shared.DTO;
import fr.kelkchoz.utsproj.shared.Player;

import javax.swing.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Objects;
import java.util.logging.Logger;

public class Client {
    private Logger log = Logger.getLogger(Client.class.getName());
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Socket socket;
    private Player player;

    public Player getPlayer() {
        return player;
    }

    public void init() throws IOException {
        try{
            InetAddress addr = InetAddress.getByName(MainClient.ADDRESS);
            System.out.println("addr = " + addr);
            socket = new Socket(addr, MainServer.PORT);
            System.out.println("socket = " + socket);
            in = new ObjectInputStream(socket.getInputStream());
            // Output is automatically flushed
            // by PrintWriter:
            out = new ObjectOutputStream(socket.getOutputStream());
            String name = null;
            do {
                name = JOptionPane.showInputDialog("Your name");
            } while (name == null || name.isBlank());
            System.out.println("sending name to server");
            player = new Player(name, 0);
            out.writeObject(new DTO(DTO.CONNECT, player));
        }catch (Exception e){
            System.out.println("exception: "+e);
            System.out.println("closing...");
            socket.close();
        }
    }

    public void sendMessage(String s) throws IOException {
        send(new DTO(DTO.CHAT, s));
    }

    public void send(DTO s) throws IOException {
        out.writeObject(s);
        if(Objects.equals(s.key, DTO.DISCONNECT)) {
            stop();
        }
    }
    public DTO receive() throws IOException, ClassNotFoundException {
        return (DTO) in.readObject();
    }

    public void stop() {
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("Error while closing socket");
            System.err.println(e.getMessage());
        }
    }
}