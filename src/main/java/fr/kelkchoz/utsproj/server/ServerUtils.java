package fr.kelkchoz.utsproj.server;

import fr.kelkchoz.utsproj.shared.DTO;

import java.io.IOException;
import java.util.List;

public class ServerUtils {


    public static void sendMessage(List<Client> clients, String message) throws IOException {
        for (Client client : clients) {
            client.getOut().writeObject(new DTO("chat", message));
        }
    }

    public static void sendObject(List<Client> clients, DTO dataObject) throws IOException {
        for(Client client : clients) {
            client.getOut().writeObject(dataObject);
        }
    }

    public static void purge(List<Client> clients) throws IOException {
        for(Client client : clients) {
            client.getOut().reset();
        }
    }

}
