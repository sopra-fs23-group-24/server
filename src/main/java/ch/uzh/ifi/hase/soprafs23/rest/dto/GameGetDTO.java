package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;

public class GameGetDTO {
    private Long gameId;
    private String gamePin;
    private GameStatus status;

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

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
