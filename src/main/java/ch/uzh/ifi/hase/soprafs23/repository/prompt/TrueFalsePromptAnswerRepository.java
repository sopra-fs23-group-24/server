package ch.uzh.ifi.hase.soprafs23.repository.prompt;

import ch.uzh.ifi.hase.soprafs23.entity.prompt.TrueFalsePromptAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository("trueFalsePromptAnswerRepository")
public interface TrueFalsePromptAnswerRepository extends JpaRepository<TrueFalsePromptAnswer, Long> {
    TrueFalsePromptAnswer findByTrueFalsePromptAnswerId(long id);
    void deleteByTrueFalsePromptAnswerId(long id);

    List<TrueFalsePromptAnswer> findAllByAssociatedGamePin(String pin);

    List<TrueFalsePromptAnswer> findAllByAssociatedPromptNr(int promptNr);

    TrueFalsePromptAnswer findTrueFalsePromptAnswerByAssociatedPlayerIdAndAssociatedPromptNr(Long playerId, int promptNr);

    List<TrueFalsePromptAnswer> findAllByAssociatedGamePinAndAssociatedPromptNr(String pin, int promptNr);
}