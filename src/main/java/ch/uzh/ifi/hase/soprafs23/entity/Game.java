package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.Prompt;
import ch.uzh.ifi.hase.soprafs23.entity.quiz.QuizQuestion;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "GAME")
public class Game implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long gameId;


    @Column(nullable = false, unique = true)
    private String gamePin;

    @Column(nullable = false)
    private GameStatus status = GameStatus.LOBBY;

    @Column
    private Long hostId;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Player> playerGroup = new ArrayList<>();
    //TODO: should we use players or id? - and why does id not work?

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Prompt> promptSet = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    private List<QuizQuestion> quizQuestionSet = new ArrayList<>();

    //@Column
    //private QuizQuestion currentQuestion;

    // add getters and setters


    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public List<Player> getPlayerGroup() {
        return playerGroup;
    }

    public void setPlayerGroup(List<Player> playerGroup) {
        this.playerGroup = playerGroup;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public Long getHostId() {
        return hostId;
    }

    public void setHostId(Long hostId) {
        this.hostId = hostId;
    }

    public String getGamePin() {
        return gamePin;
    }

    public void setGamePin(String gamePin) {
        this.gamePin = gamePin;
    }


    public void addPlayer(Player player) {
        playerGroup.add(player);
    }

    public void removePlayer(Player player) {
        playerGroup.remove(player);
    }

    public List<Prompt> getPromptSet() {
        return promptSet;
    }

    public void setPromptSet(List<Prompt> promptSet) {
        this.promptSet = promptSet;
    }

    public void addPrompts(List<Prompt> newPrompts) {
        promptSet.addAll(newPrompts);
    }

    public List<QuizQuestion> getQuizQuestionSet() {
        return quizQuestionSet;
    }

    public void setQuizQuestionSet(List<QuizQuestion> quizQuestionSet) {
        this.quizQuestionSet = quizQuestionSet;
    }

    // public nextQuestion() {
    // TODO: implement
    // }


}
