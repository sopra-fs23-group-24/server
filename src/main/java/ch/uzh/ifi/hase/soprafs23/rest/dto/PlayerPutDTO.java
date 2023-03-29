package ch.uzh.ifi.hase.soprafs23.rest.dto;

public class PlayerPutDTO {
    private Long playerId;

    private String playerName;


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
}
