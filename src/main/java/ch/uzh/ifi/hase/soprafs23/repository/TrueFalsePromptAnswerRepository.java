package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.DrawingPromptAnswer;
import ch.uzh.ifi.hase.soprafs23.entity.TextPromptAnswer;
import ch.uzh.ifi.hase.soprafs23.entity.TrueFalsePromptAnswer;
import org.springframework.data.annotation.Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository("trueFalsePromptAnswerRepository")
public interface TrueFalsePromptAnswerRepository extends JpaRepository<TrueFalsePromptAnswer, Long> {
    TrueFalsePromptAnswer findByTrueFalsePromptAnswerId(long id);
    void deleteByTrueFalsePromptAnswerId(long id);

    List<TrueFalsePromptAnswer> findAllByAssociatedGamePin(String pin);

    List<TrueFalsePromptAnswer> findAllByAssociatedPromptNr(int promptNr);

    List<TrueFalsePromptAnswer> findAllByAssociatedGamePinAndAssociatedPromptNr(String pin, int promptNr);
}