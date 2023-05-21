package ch.uzh.ifi.hase.soprafs23.rest.dto;

public class PlayerPostDTO {
    private String playerName;
    private boolean isHost;


    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public boolean isHost() {
        return isHost;
    }

    public void setIsHost(boolean isHost) {
        this.isHost = isHost;
    }
}
