package ch.uzh.ifi.hase.soprafs23.rest.dto;

public class TrueFalsePromptAnswerPostDTO {

    private int associatedPromptNr;

    private String answerText;

    private Boolean answerBoolean;


    // getters + setters
    public int getAssociatedPromptNr() {
        return associatedPromptNr;
    }

    public void setAssociatedPromptNr(int associatedPromptNr) {
        this.associatedPromptNr = associatedPromptNr;
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
