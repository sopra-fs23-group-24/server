package ch.uzh.ifi.hase.soprafs23.repository.prompt;

import ch.uzh.ifi.hase.soprafs23.constant.PromptType;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.DrawingPromptAnswer;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.Prompt;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.TextPromptAnswer;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.TrueFalsePromptAnswer;
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
class TrueFalsePromptAnswerRepositoryTest {

    private TrueFalsePromptAnswer trueFalsePromptAnswer;

    @Autowired
    private TrueFalsePromptAnswerRepository trueFalsePromptAnswerRepository;

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

        trueFalsePromptAnswer = new TrueFalsePromptAnswer();
        trueFalsePromptAnswer.setAnswerText("some story");
        trueFalsePromptAnswer.setAnswerBoolean(true);
        trueFalsePromptAnswer.setAssociatedPromptNr(testPrompt.getPromptNr());
        trueFalsePromptAnswer.setAssociatedGamePin("123456");
        trueFalsePromptAnswer.setAssociatedPlayerId(999L);
        entityManager.merge(trueFalsePromptAnswer);
        entityManager.flush();

    }
    @AfterEach
    void emptyRepository() {
        trueFalsePromptAnswerRepository.deleteAll();
    }

    @Test
    void findTrueFalsePromptAnswerByAssociatedPlayerIdAndAssociatedPromptNr() {
        TrueFalsePromptAnswer found = trueFalsePromptAnswerRepository.findTrueFalsePromptAnswerByAssociatedPlayerIdAndAssociatedPromptNr(trueFalsePromptAnswer.getAssociatedPlayerId(), trueFalsePromptAnswer.getAssociatedPromptNr());

        assertEquals(trueFalsePromptAnswer.getAnswerText(), found.getAnswerText());
        assertEquals(trueFalsePromptAnswer.getAnswerBoolean(), found.getAnswerBoolean());
        assertNotNull(found.getTrueFalsePromptAnswerId());
        assertEquals(trueFalsePromptAnswer.getAssociatedPromptNr(), found.getAssociatedPromptNr());
        assertEquals(trueFalsePromptAnswer.getAssociatedPlayerId(), found.getAssociatedPlayerId());
        assertEquals(trueFalsePromptAnswer.getAssociatedGamePin(), found.getAssociatedGamePin());
    }

    @Test
    void findAllByAssociatedGamePinAndAssociatedPromptNr() {
        List<TrueFalsePromptAnswer> allFound = trueFalsePromptAnswerRepository.findAllByAssociatedGamePinAndAssociatedPromptNr(trueFalsePromptAnswer.getAssociatedGamePin(), trueFalsePromptAnswer.getAssociatedPromptNr());
        assertEquals(1, allFound.size());

        TrueFalsePromptAnswer found = allFound.get(0);
        assertEquals(trueFalsePromptAnswer.getAnswerText(), found.getAnswerText());
        assertEquals(trueFalsePromptAnswer.getAnswerBoolean(), found.getAnswerBoolean());
        assertNotNull(found.getTrueFalsePromptAnswerId());
        assertEquals(trueFalsePromptAnswer.getAssociatedPromptNr(), found.getAssociatedPromptNr());
        assertEquals(trueFalsePromptAnswer.getAssociatedPlayerId(), found.getAssociatedPlayerId());
        assertEquals(trueFalsePromptAnswer.getAssociatedGamePin(), found.getAssociatedGamePin());
    }
}