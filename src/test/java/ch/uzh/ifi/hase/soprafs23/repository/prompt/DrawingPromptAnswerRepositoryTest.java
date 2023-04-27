package ch.uzh.ifi.hase.soprafs23.repository.prompt;

import ch.uzh.ifi.hase.soprafs23.constant.PromptType;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.DrawingPromptAnswer;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.PotentialQuestion;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.Prompt;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class DrawingPromptAnswerRepositoryTest {
    private DrawingPromptAnswer drawingPromptAnswer;

    @Autowired
    private DrawingPromptAnswerRepository drawingPromptAnswerRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        Prompt testPrompt = new Prompt();
        testPrompt.setPromptNr(999);
        testPrompt.setPromptText("Tell a story");
        testPrompt.setPromptType(PromptType.TRUEFALSE);

        entityManager.merge(testPrompt);
        entityManager.flush();

        drawingPromptAnswer = new DrawingPromptAnswer();
        drawingPromptAnswer.setAnswerDrawing("some drawing");
        drawingPromptAnswer.setAssociatedPromptNr(testPrompt.getPromptNr());
        drawingPromptAnswer.setAssociatedGamePin("123456");
        drawingPromptAnswer.setAssociatedPlayerId(999L);
        entityManager.merge(drawingPromptAnswer);
        entityManager.flush();

    }
    @AfterEach
    void emptyRepository() {
        drawingPromptAnswerRepository.deleteAll();
    }

    @Test
    void findDrawingPromptAnswerByAssociatedPlayerIdAndAssociatedPromptNr() {
        DrawingPromptAnswer found = drawingPromptAnswerRepository.findDrawingPromptAnswerByAssociatedPlayerIdAndAssociatedPromptNr(drawingPromptAnswer.getAssociatedPlayerId(), drawingPromptAnswer.getAssociatedPromptNr());

        assertEquals(drawingPromptAnswer.getAnswerDrawing(), found.getAnswerDrawing());
        assertNotNull(found.getDrawingPromptAnswerId());
        assertEquals(drawingPromptAnswer.getAssociatedPromptNr(), found.getAssociatedPromptNr());
        assertEquals(drawingPromptAnswer.getAssociatedPlayerId(), found.getAssociatedPlayerId());
        assertEquals(drawingPromptAnswer.getAssociatedGamePin(), found.getAssociatedGamePin());
    }

    @Test
    void findAllByAssociatedGamePinAndAssociatedPromptNr() {
        List<DrawingPromptAnswer> allFound = drawingPromptAnswerRepository.findAllByAssociatedGamePinAndAssociatedPromptNr(drawingPromptAnswer.getAssociatedGamePin(), drawingPromptAnswer.getAssociatedPromptNr());
        assertEquals(1, allFound.size());

        DrawingPromptAnswer found = allFound.get(0);
        assertEquals(drawingPromptAnswer.getAnswerDrawing(), found.getAnswerDrawing());
        assertNotNull(found.getDrawingPromptAnswerId());
        assertEquals(drawingPromptAnswer.getAssociatedPromptNr(), found.getAssociatedPromptNr());
        assertEquals(drawingPromptAnswer.getAssociatedPlayerId(), found.getAssociatedPlayerId());
        assertEquals(drawingPromptAnswer.getAssociatedGamePin(), found.getAssociatedGamePin());
    }

}