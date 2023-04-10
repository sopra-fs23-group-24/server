package ch.uzh.ifi.hase.soprafs23.constant;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public enum PromptType {

    TEXT("TEXT", "1"),
    TRUEFALSE("TRUEFALSE", "2"),
    DRAWING("DRAWING", "3");

    private final String prompttypeAsString;
    private final String prompttypeAsNr;
    PromptType(String prompttypeAsString, String prompttypeAsNr){
        this.prompttypeAsString = prompttypeAsString;
        this.prompttypeAsNr = prompttypeAsNr;
    }

    public static PromptType transformToType(String promptType){
        for(PromptType s : PromptType.values()){
            if(promptType.equals(s.prompttypeAsString) || promptType.equals(s.prompttypeAsNr)){
                return s;
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid prompt type.");
    }
}
