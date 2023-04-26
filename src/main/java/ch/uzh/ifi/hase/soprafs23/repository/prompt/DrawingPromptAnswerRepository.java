package ch.uzh.ifi.hase.soprafs23.repository.prompt;

import ch.uzh.ifi.hase.soprafs23.entity.prompt.DrawingPromptAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository("drawingPromptAnswerRepository")
public interface DrawingPromptAnswerRepository extends JpaRepository<DrawingPromptAnswer, Long> {

    DrawingPromptAnswer findDrawingPromptAnswerByAssociatedPlayerIdAndAssociatedPromptNr(Long playerId, int promptNr);

    List<DrawingPromptAnswer> findAllByAssociatedGamePinAndAssociatedPromptNr(String pin, int promptNr);

    void deleteAllByAssociatedGamePin(String gamePin);
}