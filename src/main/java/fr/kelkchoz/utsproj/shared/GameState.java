package fr.kelkchoz.utsproj.shared;

import java.io.Serializable;
import java.util.Map;

public class GameState implements Serializable {
    private String currentQuestion;
    private Map<Player, String> playersAnswer;
    private int timer;
    private String state;

    public GameState(String currentQuestion, Map<Player, String> playersAnswer, int timer) {
        this.currentQuestion = currentQuestion;
        this.playersAnswer = playersAnswer;
        this.timer = timer;
        this.state = "Game running";
    }

    public String getCurrentQuestion() {
        return currentQuestion;
    }

    public void setCurrentQuestion(String currentQuestion) {
        this.currentQuestion = currentQuestion;
    }

    public Map<Player, String> getPlayersAnswer() {
        return playersAnswer;
    }

    public void setPlayersAnswer(Map<Player, String> playersAnswer) {
        this.playersAnswer = playersAnswer;
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "GameState{" +
                ", playersAnswer=" + playersAnswer +
                ", timer=" + timer +
                '}';

    }
}
