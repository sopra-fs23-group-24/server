package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

public class PlayerGetDTO {
    private Long playerId;

    private String playerName;

    private String token;

    private int score = 0;

    private Long associatedGamePin;

    public Long getPlayerId() {
      return playerId;
    }

    public void setPlayerId(Long playerId) {
      this.playerId = playerId;
    }

    public String getPlayerName() {
      return playerName;
    }

    public void setPlayerName(String playerName) {
      this.playerName = playerName;
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

  public Long getAssociatedGamePin() {
    return associatedGamePin;
  }

  public void setAssociatedGamePin(Long associatedGamePin) {
    this.associatedGamePin = associatedGamePin;
  }
}
