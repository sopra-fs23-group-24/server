package ch.uzh.ifi.hase.soprafs23.entity.quiz;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

// is one answer option that is displayed and can be picked in the frontend
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
