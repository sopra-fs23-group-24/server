package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GamePutDTO {
    private String gamePin;
    private GameStatus status;


    public String getGamePin() {
        return gamePin;
    }

    public void setGamePin(String gamePin) {
        this.gamePin = gamePin;
    }


    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

}
