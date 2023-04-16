package ch.uzh.ifi.hase.soprafs23.constant;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public enum PromptType {

    TEXT("TEXT"),
    TRUEFALSE("TRUEFALSE"),
    DRAWING("DRAWING");

    private final String prompttypeAsString;

    PromptType(String prompttypeAsString) {
        this.prompttypeAsString = prompttypeAsString;
    }

    public static PromptType transformToType(String promptType) {
        for (PromptType s : PromptType.values()) {
            if (promptType.equals(s.prompttypeAsString)) {
                return s;
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid prompt type.");
    }
}
