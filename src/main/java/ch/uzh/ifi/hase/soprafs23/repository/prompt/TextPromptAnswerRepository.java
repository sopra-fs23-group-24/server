package ch.uzh.ifi.hase.soprafs23.repository.prompt;

import ch.uzh.ifi.hase.soprafs23.entity.prompt.TextPromptAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository("textPromptAnswerRepository")
public interface TextPromptAnswerRepository extends JpaRepository<TextPromptAnswer, Long> {
    TextPromptAnswer findByTextPromptAnswerId(long id);
    void deleteByTextPromptAnswerId(long id);

    List<TextPromptAnswer> findAllByAssociatedGamePin(String pin);

    List<TextPromptAnswer> findAllByAssociatedPromptNr(int promptNr);

    TextPromptAnswer findTextPromptAnswerByAssociatedPlayerIdAndAssociatedPromptNr(long id, int promptNr);

    List<TextPromptAnswer> findAllByAssociatedGamePinAndAssociatedPromptNr(String pin, int promptNr);

}