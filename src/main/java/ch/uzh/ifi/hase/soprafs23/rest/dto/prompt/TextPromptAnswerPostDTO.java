package ch.uzh.ifi.hase.soprafs23.rest.dto.prompt;

public class TextPromptAnswerPostDTO {

    private int associatedPromptNr;

    private String answer;


    public int getAssociatedPromptNr() {
        return associatedPromptNr;
    }

    public void setAssociatedPromptNr(int associatedPromptNr) {
        this.associatedPromptNr = associatedPromptNr;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
