package ch.uzh.ifi.hase.soprafs23.entity.prompt;


import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "DRAWINGPROMPTANSWER")
public class DrawingPromptAnswer implements PromptAnswer, Serializable {


    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long drawingPromptAnswerId;

    @Column(nullable = false)
    private int associatedPromptNr;

    @Column(nullable = false)
    private long associatedPlayerId;

    @Column(nullable = false)
    private String associatedGamePin;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String answerDrawing;

    @Column
    private boolean usedAsCorrectAnswer = false;


    // getters + setters

    public Long getDrawingPromptAnswerId() {
        return drawingPromptAnswerId;
    }

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

    public String getAssociatedGamePin() {
        return associatedGamePin;
    }

    public void setAssociatedGamePin(String associatedGamePin) {
        this.associatedGamePin = associatedGamePin;
    }

    public String getAnswerDrawing() {
        return answerDrawing;
    }

    public void setAnswerDrawing(String answerDrawing) {
        this.answerDrawing = answerDrawing;
    }

    public void setDrawingPromptAnswerId(Long drawingPromptAnswerId) {
        this.drawingPromptAnswerId = drawingPromptAnswerId;
    }

    public boolean isUsedAsCorrectAnswer() {
        return usedAsCorrectAnswer;
    }

    public void setUsedAsCorrectAnswer(boolean usedAsCorrectAnswer) {
        this.usedAsCorrectAnswer = usedAsCorrectAnswer;
    }
}