package fr.kelkchoz.utsproj.shared;

import java.io.Serializable;

public class DTO implements Serializable {

    public static final String CHAT = "chat";
    public static final String CONNECT = "connect";
    public static final String DISCONNECT = "disconnect";
    public static final String READY = "ready";
    public static final String START_GAME = "start_game";
    public static final String RESULT = "result";
    public static final String GAME_RUNNING = "game";
    public static final String VOTE = "vote";
    public static final String SCORE = "score";


    public String key;
    public Serializable data;

    public DTO(String key, Serializable data) {
        this.key = key;
        this.data = data;
    }

    @Override
    public String toString() {
        return "DTO{" +
                "key='" + key + '\'' +
                ", data=" + data +
                '}';
    }
}
