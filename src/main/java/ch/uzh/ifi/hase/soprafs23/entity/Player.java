package ch.uzh.ifi.hase.soprafs23.entity;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "PLAYER")
public class Player implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long playerId;

    @Column(nullable = false)
    private String associatedGamePin;

    @Column(nullable = false)
    private String playerName;

    @Column(nullable = false, unique = true)
    private String token;
    @Column
    private int score = 0;

    @Column
    private boolean isHost;


    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long id) {
        this.playerId = id;
    }

    public String getAssociatedGamePin() {
        return associatedGamePin;
    }

    public void setAssociatedGamePin(String associatedGamePin) {
        this.associatedGamePin = associatedGamePin;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String name) {
        this.playerName = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isHost() {
        return isHost;
    }

    public void setHost(boolean host) {
        isHost = host;
    }

    //-------------------------

    public void addPoints(int points) {
        score += points;
    }


}
