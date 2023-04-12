package ch.uzh.ifi.hase.soprafs23.constant;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public enum AdditionalDisplayType {

    IMAGE("IMAGE", "1"),
    TEXT("TEXT", "2"),
    NONE("NONE", "3");

    private final String displayTypeAsString;
    private final String displayTypeAsNr;
    AdditionalDisplayType(String displayTypeAsString, String displayTypeAsNr){
        this.displayTypeAsString = displayTypeAsString;
        this.displayTypeAsNr = displayTypeAsNr;
    }

    public static AdditionalDisplayType transformToType(String displayType){
        for(AdditionalDisplayType s : AdditionalDisplayType.values()){
            if(displayType.equals(s.displayTypeAsString) || displayType.equals(s.displayTypeAsNr)){
                return s;
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid display type.");
    }
}
