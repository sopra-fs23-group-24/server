package ch.uzh.ifi.hase.soprafs23.rest.dto.quiz;

public class QuizAnswerPostDTO {

    private long pickedAnswerOptionId;

    private Integer timer;


    public long getPickedAnswerOptionId() {
        return pickedAnswerOptionId;
    }

    public void setPickedAnswerOptionId(long pickedAnswerOptionId) {
        this.pickedAnswerOptionId = pickedAnswerOptionId;
    }

    public Integer getTimer() {
        return timer;
    }

    public void setTimer(Integer timer) {
        this.timer = timer;
    }
}
