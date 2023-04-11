package ch.uzh.ifi.hase.soprafs23.rest.dto;

public class TrueFalsePromptAnswerPostDTO {

    private int associatedPromptNr;

    private long associatedPlayerId;

    private String answerText;

    private Boolean answerBoolean;


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

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public Boolean getAnswerBoolean() {
        return answerBoolean;
    }

    public void setAnswerBoolean(Boolean answerBoolean) {
        this.answerBoolean = answerBoolean;
    }
}
