package ch.uzh.ifi.hase.soprafs23.constant;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public enum PromptType {

    TEXT("TEXT", "1"),
    TRUEFALSE("TRUEFALSE", "2"),
    DRAWING("DRAWING", "3");

    private final String statusAsString;
    private final String statusAsStringNr;
    PromptType(String statusAsString, String statusAsStringNr){
        this.statusAsString = statusAsString;
        this.statusAsStringNr = statusAsStringNr;
    }

    public static PromptType transformToType(String wantedStatus){
        for(PromptType s : PromptType.values()){
            if(wantedStatus.equals(s.statusAsString) || wantedStatus.equals(s.statusAsStringNr)){
                return s;
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid prompt type.");
    }
}
