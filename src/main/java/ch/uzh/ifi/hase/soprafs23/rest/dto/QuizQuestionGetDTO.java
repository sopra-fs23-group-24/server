package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.constant.CompletionStatus;
import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.entity.*;

import javax.persistence.*;
import java.io.Serial;
import java.util.List;

public class QuizQuestionGetDTO {
    private static final long serialVersionUID = 1L;

    private Long questionId;

    private String associatedGamePin;


    private String quizQuestionText;

    private String imageToDisplay;

    private String storyToDisplay;

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

    public String getAssociatedGamePin() {
        return associatedGamePin;
    }

    public void setAssociatedGamePin(String associatedGamePin) {
        this.associatedGamePin = associatedGamePin;
    }

    public String getQuizQuestionText() {
        return quizQuestionText;
    }

    public void setQuizQuestionText(String quizQuestionText) {
        this.quizQuestionText = quizQuestionText;
    }

    public String getImageToDisplay() {
        return imageToDisplay;
    }

    public void setImageToDisplay(String imageToDisplay) {
        this.imageToDisplay = imageToDisplay;
    }

    public String getStoryToDisplay() {
        return storyToDisplay;
    }

    public void setStoryToDisplay(String storyToDisplay) {
        this.storyToDisplay = storyToDisplay;
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
