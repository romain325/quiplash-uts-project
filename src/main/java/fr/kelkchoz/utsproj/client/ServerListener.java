package fr.kelkchoz.utsproj.client;

import fr.kelkchoz.utsproj.client.gui.GUI;

import java.io.IOException;

public class ServerListener extends Thread {
    private Client client;
    private GUI view;

    public ServerListener(Client client, GUI viewElement) {
        this.client = client;
        this.view = viewElement;
    }

    @Override
    public void run() {
        while(true) {
            try {
                sleep(500);
            }catch (InterruptedException _e) {}

            try {
                view.onMessageRecieved(client.receive());
            } catch (IOException | ClassNotFoundException e) {
                break;
            }
        }
        System.out.println("Thread recieved stop from server");
        client.stop();
    }
}
