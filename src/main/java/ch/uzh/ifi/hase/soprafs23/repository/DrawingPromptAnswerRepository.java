package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.DrawingPromptAnswer;
import ch.uzh.ifi.hase.soprafs23.entity.TextPromptAnswer;
import org.springframework.data.annotation.Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository("drawingPromptAnswerRepository")
public interface DrawingPromptAnswerRepository extends JpaRepository<DrawingPromptAnswer, Long> {
    DrawingPromptAnswer findByDrawingPromptAnswerId(long id);
    void deleteByDrawingPromptAnswerId(long id);

    List<DrawingPromptAnswer> findAllByAssociatedGamePin(String pin);

    List<DrawingPromptAnswer> findAllByAssociatedPromptNr(int promptNr);
}