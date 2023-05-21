package ch.uzh.ifi.hase.soprafs23.repository.prompt;

import ch.uzh.ifi.hase.soprafs23.constant.PromptType;
import ch.uzh.ifi.hase.soprafs23.constant.QuestionType;
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

@DataJpaTest
public class PotentialQuestionsRepositoryIntegrationTest {
    PotentialQuestion testPQ;

    @Autowired
    private PotentialQuestionRepository pqRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setup() {
        Prompt testPrompt = new Prompt();
        testPrompt.setPromptNr(999);
        testPrompt.setPromptText("Tell a story");
        testPrompt.setPromptType(PromptType.TRUEFALSE);

        entityManager.persist(testPrompt);
        entityManager.flush();

        testPQ = new PotentialQuestion();
        testPQ.setQuestionType(QuestionType.PLAYER);
        testPQ.setQuestionText("Test question about a story of %s?");
        testPQ.setRequiresTextInput(true);
        testPQ.setAssociatedPrompt(testPrompt);
        testPQ.setPotentialQuestionId(999L);

        entityManager.merge(testPQ);
        entityManager.flush();
    }

    @AfterEach
    void emptyRepository() {
        pqRepository.deleteAll();
    }


    @Test
    public void findAllByAssociatedPrompt() {
        List<PotentialQuestion> allFound = pqRepository.findAllByAssociatedPrompt(testPQ.getAssociatedPrompt());
        PotentialQuestion found = allFound.get(0);

        assertEquals(1, allFound.size());
        assertEquals(testPQ.getQuestionType(), found.getQuestionType());
        assertEquals(testPQ.getQuestionText(), found.getQuestionText());
        assertEquals(testPQ.getAssociatedPrompt(), found.getAssociatedPrompt());
        assertEquals(testPQ.isRequiresTextInput(), found.isRequiresTextInput());
    }

    @Test
    public void findAllByQuestionType() {
        List<PotentialQuestion> allFound = pqRepository.findAllByQuestionType(testPQ.getQuestionType());
        PotentialQuestion found = allFound.get(0);

        assertEquals(1, allFound.size());
        assertEquals(testPQ.getQuestionType(), found.getQuestionType());
        assertEquals(testPQ.getQuestionText(), found.getQuestionText());
        assertEquals(testPQ.getAssociatedPrompt(), found.getAssociatedPrompt());
        assertEquals(testPQ.isRequiresTextInput(), found.isRequiresTextInput());
    }

}