package ch.uzh.ifi.hase.soprafs23.repository.prompt;

import ch.uzh.ifi.hase.soprafs23.constant.PromptType;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.Prompt;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.TextPromptAnswer;
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
class TextPromptAnswerRepositoryTest {
    private TextPromptAnswer textPromptAnswer;

    @Autowired
    private TextPromptAnswerRepository textPromptAnswerRepository;

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

        textPromptAnswer = new TextPromptAnswer();
        textPromptAnswer.setAnswer("some answer");
        textPromptAnswer.setAssociatedPromptNr(testPrompt.getPromptNr());
        textPromptAnswer.setAssociatedGamePin("123456");
        textPromptAnswer.setAssociatedPlayerId(999L);
        entityManager.merge(textPromptAnswer);
        entityManager.flush();

    }

    @AfterEach
    void emptyRepository() {
        textPromptAnswerRepository.deleteAll();
    }

    @Test
    void findTextPromptAnswerByAssociatedPlayerIdAndAssociatedPromptNr() {
        TextPromptAnswer found = textPromptAnswerRepository.findTextPromptAnswerByAssociatedPlayerIdAndAssociatedPromptNr(textPromptAnswer.getAssociatedPlayerId(), textPromptAnswer.getAssociatedPromptNr());

        assertEquals(textPromptAnswer.getAnswer(), found.getAnswer());
        assertNotNull(found.getTextPromptAnswerId());
        assertEquals(textPromptAnswer.getAssociatedPromptNr(), found.getAssociatedPromptNr());
        assertEquals(textPromptAnswer.getAssociatedPlayerId(), found.getAssociatedPlayerId());
        assertEquals(textPromptAnswer.getAssociatedGamePin(), found.getAssociatedGamePin());
    }

    @Test
    void findAllByAssociatedGamePinAndAssociatedPromptNr() {
        List<TextPromptAnswer> allFound = textPromptAnswerRepository.findAllByAssociatedGamePinAndAssociatedPromptNr(textPromptAnswer.getAssociatedGamePin(), textPromptAnswer.getAssociatedPromptNr());
        assertEquals(1, allFound.size());

        TextPromptAnswer found = allFound.get(0);
        assertEquals(textPromptAnswer.getAnswer(), found.getAnswer());
        assertNotNull(found.getTextPromptAnswerId());
        assertEquals(textPromptAnswer.getAssociatedPromptNr(), found.getAssociatedPromptNr());
        assertEquals(textPromptAnswer.getAssociatedPlayerId(), found.getAssociatedPlayerId());
        assertEquals(textPromptAnswer.getAssociatedGamePin(), found.getAssociatedGamePin());
    }

}