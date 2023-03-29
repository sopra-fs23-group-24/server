package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GameJoinDTO {
  private String gamePin;

  public String getGamePin() {
    return gamePin;
  }

  public void setGamePin(String gamePin) {
    this.gamePin = gamePin;
  }
}
