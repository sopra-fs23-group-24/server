package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.repository.PromptRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class PromptService {

    private final Logger log = LoggerFactory.getLogger(PromptService.class);

    private final PromptRepository promptRepository;


    @Autowired
    public PromptService(@Qualifier("promptRepository") PromptRepository promptRepository) {
        this.promptRepository = promptRepository;
        initialiseRepository();
    }

    private void initialiseRepository(){

    }


}
