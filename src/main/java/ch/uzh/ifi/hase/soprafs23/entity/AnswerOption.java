package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.constant.AdditionalDisplayType;
import ch.uzh.ifi.hase.soprafs23.constant.QuestionType;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "ANSWEROPTION")
public class AnswerOption implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long answerOptionId;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String answerOptionText;

    public Long getAnswerOptionId() {
        return answerOptionId;
    }

    public void setAnswerOptionId(Long answerOptionId) {
        this.answerOptionId = answerOptionId;
    }

    public String getAnswerOptionText() {
        return answerOptionText;
    }

    public void setAnswerOptionText(String answerOptionText) {
        this.answerOptionText = answerOptionText;
    }
}
