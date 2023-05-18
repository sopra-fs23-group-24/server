package ch.uzh.ifi.hase.soprafs23.entity.prompt;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "TRUEFALSEPROMPTANSWER")
public class TrueFalsePromptAnswer implements PromptAnswer, Serializable {


    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long trueFalsePromptAnswerId;

    @Column(nullable = false)
    private int associatedPromptNr;

    @Column(nullable = false)
    private long associatedPlayerId;

    @Column(nullable = false)
    private String associatedGamePin;
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String answerText;

    @Column(nullable = false)
    private Boolean answerBoolean;

    @Column
    private boolean usedAsCorrectAnswer = false;


    // getters + setters
    public Long getTrueFalsePromptAnswerId() {
        return trueFalsePromptAnswerId;
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


    public String getAssociatedGamePin() {
        return associatedGamePin;
    }

    public void setAssociatedGamePin(String associatedGamePin) {
        this.associatedGamePin = associatedGamePin;
    }

    public void setTrueFalsePromptAnswerId(Long trueFalsePromptAnswerId) {
        this.trueFalsePromptAnswerId = trueFalsePromptAnswerId;
    }

    public boolean isUsedAsCorrectAnswer() {
        return usedAsCorrectAnswer;
    }

    public void setUsedAsCorrectAnswer(boolean usedAsCorrectAnswer) {
        this.usedAsCorrectAnswer = usedAsCorrectAnswer;
    }
}
