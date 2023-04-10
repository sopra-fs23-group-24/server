package ch.uzh.ifi.hase.soprafs23.constant;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public enum QuestionType {

    PLAYER("PLAYER", "1"),
    PROMPTANSWER("PROMPTANSWER", "2"),
    BOOLEAN("BOOLEAN", "3");

    private final String questionTypeAsString;
    private final String questionTypeAsNr;
    QuestionType(String questionTypeAsString, String questionTypeAsNr){
        this.questionTypeAsString = questionTypeAsString;
        this.questionTypeAsNr = questionTypeAsNr;
    }

    public static QuestionType transformToType(String questionType){
        for(QuestionType s : QuestionType.values()){
            if(questionType.equals(s.questionTypeAsString) || questionType.equals(s.questionTypeAsNr)){
                return s;
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid question type.");
    }
}
