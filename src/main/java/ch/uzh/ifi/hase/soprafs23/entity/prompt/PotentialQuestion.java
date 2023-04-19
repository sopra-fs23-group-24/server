package ch.uzh.ifi.hase.soprafs23.entity.prompt;

import ch.uzh.ifi.hase.soprafs23.constant.AdditionalDisplayType;
import ch.uzh.ifi.hase.soprafs23.constant.QuestionType;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.Prompt;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "POTENTIALQUESTION")
public class PotentialQuestion implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long potentialQuestionId;

    @ManyToOne
    private Prompt associatedPrompt;

    @Column(nullable = false)
    private QuestionType questionType;

    @Column(nullable = false)
    private String questionText;

    @Column
    private boolean requiresTextInput;

    @Column
    private AdditionalDisplayType displayType;

    public Long getPotentialQuestionId() {
        return potentialQuestionId;
    }

    public void setPotentialQuestionId(Long potentialQuestionId) {
        this.potentialQuestionId = potentialQuestionId;
    }

    public Prompt getAssociatedPrompt() {
        return associatedPrompt;
    }

    public void setAssociatedPrompt(Prompt associatedPrompt) {
        this.associatedPrompt = associatedPrompt;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public boolean isRequiresTextInput() {
        return requiresTextInput;
    }

    public void setRequiresTextInput(boolean requiresTextInput) {
        this.requiresTextInput = requiresTextInput;
    }

    public AdditionalDisplayType getDisplayType() {
        return displayType;
    }

    public void setDisplayType(AdditionalDisplayType displayType) {
        this.displayType = displayType;
    }
}
