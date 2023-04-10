package ch.uzh.ifi.hase.soprafs23.rest.dto;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

public class TextPromptAnswerPostDTO {

    private int associatedPromptNr;

    private long associatedPlayerId;

    private String answer;

    // getters + setters


    public int getAssociatedPromptNr() {
        return associatedPromptNr;
    }

    public void setAssociatedPromptNr(int associatedPromptNr) {
        this.associatedPromptNr = associatedPromptNr;
    }

    public long getAssociatedPlayerId() {
        return associatedPlayerId;
    }

    public void setAssociatedPlayerId(long associatedPlayerId) {
        this.associatedPlayerId = associatedPlayerId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
