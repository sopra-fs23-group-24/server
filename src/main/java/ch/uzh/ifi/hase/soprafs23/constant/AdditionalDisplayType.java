package ch.uzh.ifi.hase.soprafs23.constant;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public enum AdditionalDisplayType {

    IMAGE("IMAGE"),
    TEXT("TEXT"),
    NONE("NONE");

    private final String displayTypeAsString;
    AdditionalDisplayType(String displayTypeAsString){
        this.displayTypeAsString = displayTypeAsString;
    }

    public static AdditionalDisplayType transformToType(String displayType){
        for(AdditionalDisplayType s : AdditionalDisplayType.values()){
            if(displayType.equals(s.displayTypeAsString)){
                return s;
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid display type.");
    }
}
