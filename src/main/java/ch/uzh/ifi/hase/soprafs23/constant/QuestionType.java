package ch.uzh.ifi.hase.soprafs23.constant;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public enum QuestionType {

    PLAYER("PLAYER"),
    PROMPTANSWER("PROMPTANSWER"),
    BOOLEAN("BOOLEAN");

    private final String questionTypeAsString;

    QuestionType(String questionTypeAsString) {
        this.questionTypeAsString = questionTypeAsString;
    }

    public static QuestionType transformToType(String questionType) {
        for (QuestionType s : QuestionType.values()) {
            if (questionType.equals(s.questionTypeAsString)) {
                return s;
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid question type.");
    }
}
