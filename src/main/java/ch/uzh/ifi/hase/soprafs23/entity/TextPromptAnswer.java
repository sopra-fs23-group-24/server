package ch.uzh.ifi.hase.soprafs23.entity;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "TEXTPROMPTANSWER")
public class TextPromptAnswer implements PromptAnswer, Serializable {


    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long textPromptAnswerId;

    @Column(nullable = false)
    private int associatedPromptNr;

    @Column(nullable = false)
    private long associatedPlayerId;

    @Column(nullable = false)
    private String answer;


    // getters + setters

    public Long getTextPromptAnswerId() {
        return textPromptAnswerId;
    }

    //no setter for ID bc that already is done by the @Id above.

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
