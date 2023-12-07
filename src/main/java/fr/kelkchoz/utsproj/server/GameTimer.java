package fr.kelkchoz.utsproj.server;

import fr.kelkchoz.utsproj.shared.DTO;
import fr.kelkchoz.utsproj.shared.GameState;
import fr.kelkchoz.utsproj.shared.Player;
import fr.kelkchoz.utsproj.shared.VoteState;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GameTimer {
    public static final int GAME_DURATION = 31;
    public static final int VOTE_DURATION = 16;
    public static final int NUMBER_OF_ROUND = 1;
    private Timer timer = new Timer();
    private boolean isGameStarted = false;
    private int clickCount = 0;
    private int roundCount = 0;


    private GameState gameState;
    private VoteState voteState;

    private final List<Client> clients;

    public GameTimer(List<Client> clients) {
        this.clients = clients;
    }

    public void startGame() {
        if (isGameStarted) {
            return;
        }

        isGameStarted = true;
        clickCount = GAME_DURATION;

        int count = 0;
        Scanner scanner = new Scanner(getClass().getClassLoader().getResourceAsStream("question.txt"));
        while(scanner.hasNextLine()) {
            System.out.println(scanner.nextLine());
            count++;
        }
        int index = (int) (Math.random() * count-1);
        System.out.println(index);
        scanner.close();
        scanner = new Scanner(getClass().getClassLoader().getResourceAsStream("question.txt"));
        while(--index > 0) {
            scanner.nextLine();
        }


        Map<Player, String> answers = clients.stream().map(Client::getPlayer).collect(Collectors.toMap(Function.identity(), x -> ""));
        gameState = new GameState(scanner.nextLine(), answers, clickCount);

        scanner.close();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (clickCount <= 0) {
                    timer.cancel();
                    timer = new Timer();
                    startVote();
                    return;
                }

                clickCount--;
                gameState.setTimer(clickCount);
                System.out.println("Game: " + clickCount);

                try {
                    ServerUtils.purge(clients);
                    ServerUtils.sendObject(clients, new DTO(DTO.GAME_RUNNING, gameState));
                } catch (IOException e) {
                    System.err.println("error while sending game state");
                }
            }
        }, 0, 1000);

    }

    private void startVote() {
        isGameStarted = false;
        clickCount = VOTE_DURATION;

        voteState = new VoteState(gameState.getCurrentQuestion(), gameState.getPlayersAnswer(), clickCount);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (clickCount <= 0) {
                    addVotesToScore();
                    timer.cancel();
                    timer = new Timer();
                    if (roundCount >= NUMBER_OF_ROUND) {
                        endGame();
                    } else {
                        roundCount++;
                        startGame();
                    }
                    return;
                }
                clickCount--;
                voteState.setTimer(clickCount);
                System.out.println("Vote: " + clickCount);

                try {
                    ServerUtils.purge(clients);
                    ServerUtils.sendObject(clients, new DTO(DTO.VOTE, voteState));
                } catch (IOException e) {
                    System.err.println("error while sending game state");
                }
            }
        }, 0, 1000);
    }

    private void endGame() {
        try {
            gameState.getPlayersAnswer().keySet().forEach(x -> x.setReady(false));
            roundCount = NUMBER_OF_ROUND;
            ServerUtils.sendObject(clients, new DTO(DTO.RESULT, new ArrayList<>(gameState.getPlayersAnswer().keySet())));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void updatePlayerAnswer(Player player, String answer) {
        gameState.getPlayersAnswer().put(player, answer);
    }

    public void updatePlayerVote(Player player, Player vote) {
        if(player == vote) {
            return;
        }
        voteState.getVotes().put(player, vote);
    }

    public void addVotesToScore() {
        Map<Player, Integer> count = new HashMap<>();
        for (Player value : voteState.getVotes().values()) {
            if(value != null) {
                count.merge(value, 1, Integer::sum);
            }
        }

        // if player forget to vote then the best player gets the remaining points
        int nbPlayer = voteState.getPlayersAnswer().size();
        int countVote = count.values().stream().mapToInt(Integer::intValue).sum();
        if(nbPlayer != countVote){
            if(count.entrySet().isEmpty()) {
                return;
            }
            Map.Entry<Player, Integer> best = Collections.max(count.entrySet(), Map.Entry.comparingByValue());
            if(best != null) {
                count.put(best.getKey(), best.getKey().getScore() + (nbPlayer - countVote));
            }
        }

        for (Player player : gameState.getPlayersAnswer().keySet()) {
            player.setScore(player.getScore() + count.getOrDefault(player, 0));
        }

        try {
            ServerUtils.purge(clients);
            ServerUtils.sendObject(clients, new DTO(DTO.SCORE, (Serializable) gameState.getPlayersAnswer().keySet().stream().toList()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
