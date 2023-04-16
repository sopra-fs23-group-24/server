package ch.uzh.ifi.hase.soprafs23.entity;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "QUIZQUESTION")
public abstract class QuizQuestion implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long questionId;

    @ManyToOne
    @JoinColumn(name = "gamePin")
    private Game associatedGame;

    @ManyToOne
    @JoinColumn(name = "promptNr")
    private Prompt associatedPrompt;

    @Column
    private String correctAnswer;

    @Column
    private String answerOptions;






    public String convertMapToString(Map<String, String> map) {
        String mapAsString = map.keySet().stream()
                .map(key -> key + "=" + map.get(key))
                .collect(Collectors.joining(", ", "{", "}"));
        return mapAsString;
    }

    public Map<String, String> convertStringToMap(String mapAsString) {
        Map<String, String> map = Arrays.stream(mapAsString.split(","))
                .map(entry -> entry.split("="))
                .collect(Collectors.toMap(entry -> entry[0], entry -> entry[1]));
        return map;
    }
}
