package ch.uzh.ifi.hase.soprafs23.constant;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public enum CompletionStatus {

    FINISHED("FINISHED"),
    NOT_FINISHED("NOT_FINISHED");

    private final String completionStatusAsString;

    CompletionStatus(String completionStatusAsString) {
        this.completionStatusAsString = completionStatusAsString;
    }

    public static CompletionStatus transformToType(String displayType) {
        for (CompletionStatus s : CompletionStatus.values()) {
            if (displayType.equals(s.completionStatusAsString)) {
                return s;
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid display type.");
    }
}
