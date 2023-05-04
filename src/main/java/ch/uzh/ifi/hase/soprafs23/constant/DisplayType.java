package ch.uzh.ifi.hase.soprafs23.constant;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public enum DisplayType {

    IMAGE("IMAGE"),
    TEXT("TEXT"),
    NONE("NONE");

    private final String displayTypeAsString;

    DisplayType(String displayTypeAsString) {
        this.displayTypeAsString = displayTypeAsString;
    }

    public static DisplayType transformToType(String displayType) {
        for (DisplayType s : DisplayType.values()) {
            if (displayType.equals(s.displayTypeAsString)) {
                return s;
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid display type.");
    }
}
