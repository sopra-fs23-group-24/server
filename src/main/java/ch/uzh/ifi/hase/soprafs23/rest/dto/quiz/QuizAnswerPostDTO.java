package ch.uzh.ifi.hase.soprafs23.rest.dto.quiz;

import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.quiz.AnswerOption;


public class QuizAnswerPostDTO {

    private long pickedAnswerOptionId;


    public long getPickedAnswerOptionId() {
        return pickedAnswerOptionId;
    }

    public void setPickedAnswerOptionId(long pickedAnswerOptionId) {
        this.pickedAnswerOptionId = pickedAnswerOptionId;
    }
}
