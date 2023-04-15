package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.constant.PromptType;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "PROMPT")
public class Prompt implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long promptId;

    @Column(nullable = false, unique = true)
    private Integer promptNr;

    @Column(nullable = false)
    private PromptType promptType;

    @Column(nullable = false)
    private String promptText;

    public Long getPromptId() {
        return promptId;
    }

    public void setPromptId(Long promptId) {
        this.promptId = promptId;
    }

    public Integer getPromptNr() {
        return promptNr;
    }

    public void setPromptNr(Integer promptNr) {
        this.promptNr = promptNr;
    }

    public PromptType getPromptType() {
        return promptType;
    }

    public void setPromptType(PromptType promptType) {
        this.promptType = promptType;
    }

    public String getPromptText() {
        return promptText;
    }

    public void setPromptText(String promptText) {
        this.promptText = promptText;
    }
}
