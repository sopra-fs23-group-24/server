package ch.uzh.ifi.hase.soprafs23.rest.dto.quiz;

import ch.uzh.ifi.hase.soprafs23.constant.CompletionStatus;
import ch.uzh.ifi.hase.soprafs23.constant.DisplayType;
import ch.uzh.ifi.hase.soprafs23.entity.quiz.AnswerOption;

import java.util.List;

public class QuizQuestionGetDTO {
    private static final long serialVersionUID = 1L;

    private Long questionId;

    private String quizQuestionText;

    private String imageToDisplay;

    private String storyToDisplay;

    private List<AnswerOption> answerOptions;

    private DisplayType answerDisplayType;

    private AnswerOption correctAnswer;

    private CompletionStatus questionStatus = CompletionStatus.NOT_FINISHED;

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
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

    public List<AnswerOption> getAnswerOptions() {
        return answerOptions;
    }

    public void setAnswerOptions(List<AnswerOption> answerOptions) {
        this.answerOptions = answerOptions;
    }

    public DisplayType getAnswerDisplayType() {
        return answerDisplayType;
    }

    public void setAnswerDisplayType(DisplayType answerDisplayType) {
        this.answerDisplayType = answerDisplayType;
    }

    public AnswerOption getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(AnswerOption correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public CompletionStatus getQuestionStatus() {
        return questionStatus;
    }

    public void setQuestionStatus(CompletionStatus questionStatus) {
        this.questionStatus = questionStatus;
    }
}
