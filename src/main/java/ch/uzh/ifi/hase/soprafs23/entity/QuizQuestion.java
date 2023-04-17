package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.constant.CompletionStatus;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "QUIZQUESTION")
public class QuizQuestion implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long questionId;

    @Column
    private CompletionStatus questionStatus = CompletionStatus.NOT_FINISHED;

    @Column(nullable = false)
    private String quizQuestionText;

    @Column
    private String imageToDisplay = null;

    @Column
    private String storyToDisplay = null;


    @ManyToOne
    @JoinColumn(name = "gamePin")
    private Game associatedGame;


    @ManyToOne
    @JoinColumn(name = "promptNr")
    private Prompt associatedPrompt;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnswerOption> answerOptions = new ArrayList<>();

    @OneToOne
    private AnswerOption correctAnswer;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizAnswer> receivedAnswers = new ArrayList<>();




    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public CompletionStatus getQuestionStatus() {
        return questionStatus;
    }

    public void setQuestionStatus(CompletionStatus questionStatus) {
        this.questionStatus = questionStatus;
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

    public void addAnswerOption(AnswerOption option){
        this.answerOptions.add(option);
    }

    public List<String> getAnswerOptionStrings(){
        List<String> allAnswerOptionTexts = new ArrayList<>();
        for(AnswerOption answerOption : answerOptions){
            allAnswerOptionTexts.add(answerOption.getAnswerOptionText());
        }
        return allAnswerOptionTexts;
    }

    public CompletionStatus addReceivedAnswer(QuizAnswer answer){
        receivedAnswers.add(answer);
        List<Player> playersThatAnswered = new ArrayList<>();
        for(QuizAnswer receivedAnswer : receivedAnswers){
            playersThatAnswered.add(receivedAnswer.getAssociatedPlayer());
        }
        for(Player player : associatedGame.getPlayerGroup()){
            if(!playersThatAnswered.contains(player)){
                return CompletionStatus.NOT_FINISHED;
            }
        }
        return CompletionStatus.FINISHED;
    }
}
