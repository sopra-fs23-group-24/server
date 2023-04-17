package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.constant.CompletionStatus;
import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.entity.AnswerOption;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Prompt;
import ch.uzh.ifi.hase.soprafs23.entity.QuizAnswer;

import javax.persistence.*;
import java.io.Serial;
import java.util.List;

public class QuizQuestionGetDTO {
    private static final long serialVersionUID = 1L;

    private Long questionId;

    private Game associatedGame;

    private Prompt associatedPrompt;

    private List<AnswerOption> answerOptions;

    private AnswerOption correctAnswer;

    private List<QuizAnswer> receivedAnswers;

    private CompletionStatus questionStatus = CompletionStatus.NOT_FINISHED;

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Game getAssociatedGame() {
        return associatedGame;
    }

    public void setAssociatedGame(Game associatedGame) {
        this.associatedGame = associatedGame;
    }

    public Prompt getAssociatedPrompt() {
        return associatedPrompt;
    }

    public void setAssociatedPrompt(Prompt associatedPrompt) {
        this.associatedPrompt = associatedPrompt;
    }

    public List<AnswerOption> getAnswerOptions() {
        return answerOptions;
    }

    public void setAnswerOptions(List<AnswerOption> answerOptions) {
        this.answerOptions = answerOptions;
    }

    public AnswerOption getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(AnswerOption correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public List<QuizAnswer> getReceivedAnswers() {
        return receivedAnswers;
    }

    public void setReceivedAnswers(List<QuizAnswer> receivedAnswers) {
        this.receivedAnswers = receivedAnswers;
    }

    public CompletionStatus getQuestionStatus() {
        return questionStatus;
    }

    public void setQuestionStatus(CompletionStatus questionStatus) {
        this.questionStatus = questionStatus;
    }
}
