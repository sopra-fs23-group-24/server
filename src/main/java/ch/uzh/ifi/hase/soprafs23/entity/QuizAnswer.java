package ch.uzh.ifi.hase.soprafs23.entity;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "QUIZANSWERS")
public class QuizAnswer implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long quizAnswerId;

    @Column(nullable = false)
    @OneToOne
    private AnswerOption pickedAnswer;

    @Column(nullable = false)
    @ManyToOne
    private Player associatedPlayer;

    public Long getQuizAnswerId() {
        return quizAnswerId;
    }

    public void setQuizAnswerId(Long quizAnswerId) {
        this.quizAnswerId = quizAnswerId;
    }

    public AnswerOption getPickedAnswer() {
        return pickedAnswer;
    }

    public void setPickedAnswer(AnswerOption pickedAnswer) {
        this.pickedAnswer = pickedAnswer;
    }

    public Player getAssociatedPlayer() {
        return associatedPlayer;
    }

    public void setAssociatedPlayer(Player associatedPlayer) {
        this.associatedPlayer = associatedPlayer;
    }
}
