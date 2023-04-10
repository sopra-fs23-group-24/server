package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.TextPromptAnswer;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.TextPromptAnswerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.swing.text.BadLocationException;

@Service

public class PromptAnswerService {
    private final Logger log = LoggerFactory.getLogger(PromptAnswerService.class);

    private final TextPromptAnswerRepository textPromptAnswerRepository;

    private PromptAnswerService promptAnswerService;

    @Autowired
    public PromptAnswerService(@Qualifier("textPromptAnswerRepository") TextPromptAnswerRepository textPromptAnswerRepository) {
        this.textPromptAnswerRepository = textPromptAnswerRepository;
    }

    public Boolean saveTextPromptAnswer(TextPromptAnswer answer) {

        // TODO: add checks if answer is not null or if anything else is wrong?  (or is the try catch ok?)
        try {
            textPromptAnswerRepository.save(answer);
            textPromptAnswerRepository.flush();
            return true;
        } catch (Exception e) {
            System.out.printf("%s has occurred while trying to save the TextAnswer", e);
            return false;
        }
    }

}
