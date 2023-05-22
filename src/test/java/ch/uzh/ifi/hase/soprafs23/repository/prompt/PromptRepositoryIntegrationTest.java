package ch.uzh.ifi.hase.soprafs23.repository.prompt;

import ch.uzh.ifi.hase.soprafs23.constant.PromptType;
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
public class PromptRepositoryIntegrationTest {
    Prompt testPrompt;

    @Autowired
    private PromptRepository promptRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setup() {
        testPrompt = new Prompt();
        testPrompt.setPromptNr(999);
        testPrompt.setPromptText("Tell a story");
        testPrompt.setPromptType(PromptType.TRUEFALSE);

        entityManager.persist(testPrompt);
        entityManager.flush();
    }

    @AfterEach
    void emptyRepository() {
        promptRepository.deleteAll();
    }


    @Test
    public void findByPromptNr() {
        Prompt found = promptRepository.findByPromptNr(testPrompt.getPromptNr());

        assertEquals(testPrompt.getPromptNr(), found.getPromptNr());
        assertEquals(testPrompt.getPromptType(), found.getPromptType());
        assertEquals(testPrompt.getPromptText(), found.getPromptText());
    }

    @Test
    public void findAllByPromptType() {
        List<Prompt> allFound = promptRepository.findAllByPromptType(testPrompt.getPromptType());
        Prompt found = allFound.get(0);

        assertEquals(1, allFound.size());
        assertEquals(testPrompt.getPromptNr(), found.getPromptNr());
        assertEquals(testPrompt.getPromptType(), found.getPromptType());
        assertEquals(testPrompt.getPromptText(), found.getPromptText());
    }


}