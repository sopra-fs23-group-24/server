package ch.uzh.ifi.hase.soprafs23.repository.prompt;

import ch.uzh.ifi.hase.soprafs23.constant.PromptType;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.DrawingPromptAnswer;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.Prompt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

class DrawingPromptAnswerRepositoryTest {
    private DrawingPromptAnswer drawingPromptAnswer;

    @Autowired
    private PotentialQuestionRepository pqRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        Prompt testPrompt = new Prompt();
        testPrompt.setPromptNr(999);
        testPrompt.setPromptText("Tell a story");
        testPrompt.setPromptType(PromptType.TRUEFALSE);

        entityManager.persist(testPrompt);
        entityManager.flush();

        drawingPromptAnswer.setAnswerDrawing("some drawing");
        drawingPromptAnswer.setAssociatedPromptNr(testPrompt.getPromptNr());
        drawingPromptAnswer.setAssociatedGamePin("123456");
        entityManager.persist(drawingPromptAnswer);
        entityManager.flush();

    }

    @Test
    void findDrawingPromptAnswerByAssociatedPlayerIdAndAssociatedPromptNr() {
        //todo: test
    }

    @Test
    void findAllByAssociatedGamePinAndAssociatedPromptNr() {
        //todo: test
    }

    @Test
    void deleteAllByAssociatedGamePin() {
        //todo: test
    }

}