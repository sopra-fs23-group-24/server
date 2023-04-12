package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Internal User Representation
 * This class composes the internal representation of the user and defines how
 * the user is stored in the database.
 * Every variable will be mapped into a database field with the @Column
 * annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes
 * the primary key
 */
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


    @Column
    @OneToMany(cascade=CascadeType.ALL)
    private List<Player> playerGroup = new ArrayList<Player>();
    //TODO: should we use players or id? - and why does id not work?

    @Column(nullable = false)
    private GameStatus status = GameStatus.LOBBY;

    @Column
    private Long hostId;

    @Column
    @ManyToMany(cascade=CascadeType.ALL)
    private List<Prompt> promptSet = new ArrayList<Prompt>();

    //@Column @OneToMany
    //private List<GameQuestion> quizQuestionSet = new ArrayList<GameQuestion>();

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

    // public nextQuestion() {
    // TODO: implement
    // }


}
