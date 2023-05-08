package ch.uzh.ifi.hase.soprafs23.entity.quiz;

import ch.uzh.ifi.hase.soprafs23.entity.Player;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

// is the answer a player picked (contains a AnswerOption)
@Entity
@Table(name = "QUIZANSWERS")
public class QuizAnswer implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long quizAnswerId;


    // @OneToOne // maybe change the database column to be bigInt or sth like that.
    @Column
    private long pickedAnswerOptionId;
    @ManyToOne
    private Player associatedPlayer;

    @Column // does it need to be a column, why? , why not? not sure atm
    private Integer timer;


    public long getPickedAnswerOptionId() {
        return pickedAnswerOptionId;
    }

    public void setPickedAnswerOptionId(long pickedAnswerOptionId) {
        this.pickedAnswerOptionId = pickedAnswerOptionId;
    }


    public Long getQuizAnswerId() {
        return quizAnswerId;
    }

    public void setQuizAnswerId(Long quizAnswerId) {
        this.quizAnswerId = quizAnswerId;
    }


    public Player getAssociatedPlayer() {
        return associatedPlayer;
    }

    public void setAssociatedPlayer(Player associatedPlayer) {
        this.associatedPlayer = associatedPlayer;
    }

    public Integer getTimer() {
        return timer;
    }

    public void setTimer(Integer timer) {
        this.timer = timer;
    }
}
