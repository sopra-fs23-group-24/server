package ch.uzh.ifi.hase.soprafs23.constant;

import ch.uzh.ifi.hase.soprafs23.entity.Game;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public enum GameStatus {

    LOBBY("LOBBY", "1"),
    SELECTION("SELECTION", "2"),
    PROMPT("PROMPT", "3"),
    QUIZ("QUIZ", "4"),
    END("END", "5");

    private final String statusAsString;
    private final String statusAsStringNr;
    GameStatus(String statusAsString, String statusAsStringNr){
        this.statusAsString = statusAsString;
        this.statusAsStringNr = statusAsStringNr;
    }

    public static GameStatus transformToStatus(String wantedStatus){
        for(GameStatus s : GameStatus.values()){
            if(wantedStatus.equals(s.statusAsString) || wantedStatus.equals(s.statusAsStringNr)){
                return s;
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status requested.");
    }
}