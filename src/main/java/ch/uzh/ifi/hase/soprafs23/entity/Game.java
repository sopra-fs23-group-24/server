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

    //TODO: Find out how to get this values / change them to be e.g. length 6
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    private Long gameId;

    @Column
    @OneToMany
    private List<Player> playerGroup = new ArrayList<Player>();
    //TODO: should we use players or id? - and why does id not work?

    @Column(nullable = false)
    private GameStatus status;

    @Column
    private int hostId;

    //@Column @ManyToMany
    //private List<Prompt> promptSet = new ArrayList<Prompt>();

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

    public int getHostId() {
        return hostId;
    }

    public void setHostId(int hostId) {
        this.hostId = hostId;
    }


    //-------------------

    public void addPlayer(Player player) {
        playerGroup.add(player);
    }

    public void removePlayer(Player player) {
        playerGroup.remove(player);
    }

    // public nextQuestion() {
    // TODO: implement
    // }

}
