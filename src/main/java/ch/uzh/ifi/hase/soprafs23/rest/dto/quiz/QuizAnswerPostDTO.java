package ch.uzh.ifi.hase.soprafs23.rest.dto.quiz;

import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.quiz.AnswerOption;


public class QuizAnswerPostDTO {

    private AnswerOption pickedAnswer;

    private Player associatedPlayer;


    public AnswerOption getPickedAnswer() {
        return pickedAnswer;
    }

    public void setPickedAnswer(AnswerOption pickedAnswer) {
        this.pickedAnswer = pickedAnswer;
    }

    public Player getAssociatedPlayer() {
        return associatedPlayer;
    }

    public void setAssociatedPlayer(Player associatedPlayer) {
        this.associatedPlayer = associatedPlayer;
    }
}
