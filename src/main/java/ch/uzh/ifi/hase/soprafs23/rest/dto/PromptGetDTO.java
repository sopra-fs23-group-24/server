package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.constant.PromptType;

public class PromptGetDTO {
    private Long promptId;
    private Integer promptNr;

    private PromptType promptType;

    private String promptText;

    public Long getPromptId() {
        return promptId;
    }

    public void setPromptId(Long promptId) {
        this.promptId = promptId;
    }

    public PromptType getPromptType() {
        return promptType;
    }

    public void setPromptType(PromptType promptType) {
        this.promptType = promptType;
    }

    public Integer getPromptNr() {
        return promptNr;
    }

    public void setPromptNr(Integer promptNr) {
        this.promptNr = promptNr;
    }

    public String getPromptText() {
        return promptText;
    }

    public void setPromptText(String promptText) {
        this.promptText = promptText;
    }
}
