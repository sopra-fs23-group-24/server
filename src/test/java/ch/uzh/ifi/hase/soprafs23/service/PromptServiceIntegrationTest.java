package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.repository.PotentialQuestionsRepository;
import ch.uzh.ifi.hase.soprafs23.repository.PromptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Test class for the UserResource REST resource.
 *
 * @see PlayerService
 */
@WebAppConfiguration
@SpringBootTest
public class PromptServiceIntegrationTest {
    @Qualifier("potentialQuestionRepository")
    @Autowired
    private PotentialQuestionsRepository pqRepository;

    @Qualifier("promptRepository")
    @Autowired
    private PromptRepository promptRepository;

    @Autowired
    private PromptService promptService;


}