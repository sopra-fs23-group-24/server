package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;

public class GamePutDTO {
    private GameStatus status;

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

}
