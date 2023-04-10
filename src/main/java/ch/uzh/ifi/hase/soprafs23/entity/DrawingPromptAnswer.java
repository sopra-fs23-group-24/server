package ch.uzh.ifi.hase.soprafs23.entity;


import javax.persistence.*;
import java.io.Serializable;
import java.io.Serial;

@Entity
@Table(name = "DRAWINGPROMPTANSWER")
public class DrawingPromptAnswer implements PromptAnswer, Serializable {


    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long drawingPromptAnswerId;

    @Column(nullable = false)
    private int associatedPromptNr;

    @Column(nullable = false)
    private long associatedPlayerId;

    @Column(nullable = false)
    private String answer;


    // getters + setters

}