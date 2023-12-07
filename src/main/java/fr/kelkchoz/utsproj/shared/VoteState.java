package fr.kelkchoz.utsproj.shared;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class VoteState extends GameState implements Serializable {
    private Map<Player, Player> votes = new HashMap<>();

    public VoteState(String currentQuestion, Map<Player, String> playersAnswer, int timer) {
        super(currentQuestion, playersAnswer, timer);
        this.setState("Voting ...");
        for(Player player: playersAnswer.keySet()) {
            votes.put(player, null);
        }
    }

    public Map<Player, Player> getVotes() {
        return votes;
    }
}
