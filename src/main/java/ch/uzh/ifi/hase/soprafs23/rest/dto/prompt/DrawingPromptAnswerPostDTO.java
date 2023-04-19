package ch.uzh.ifi.hase.soprafs23.rest.dto.prompt;

public class DrawingPromptAnswerPostDTO {


    private int associatedPromptNr;

    private String answerDrawing;


    // getters + setters


    public int getAssociatedPromptNr() {
        return associatedPromptNr;
    }

    public void setAssociatedPromptNr(int associatedPromptNr) {
        this.associatedPromptNr = associatedPromptNr;
    }

    public String getAnswerDrawing() {
        return answerDrawing;
    }

    public void setAnswerDrawing(String answer) {
        this.answerDrawing = answer;
    }
}
