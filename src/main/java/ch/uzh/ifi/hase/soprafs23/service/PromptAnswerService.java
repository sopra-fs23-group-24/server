package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.TextPromptAnswer;
import ch.uzh.ifi.hase.soprafs23.entity.TrueFalsePromptAnswer;
import ch.uzh.ifi.hase.soprafs23.repository.DrawingPromptAnswerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.TextPromptAnswerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.TrueFalsePromptAnswerRepository;
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
    private final TrueFalsePromptAnswerRepository trueFalsePromptAnswerRepository;
    //  private final DrawingPromptAnswerRepository drawingPromptAnswerRepository;


    // only one can have @Autowired - what does that do, and does it work like this with the two constructors?
    // I have no idea if this is going to work, but it seems better than multiple constructors...
    // the question is, do I need to pass these arguments? - bc in the Controller, where this constructor is used,
    // there are no arguments passed... just a blank call.
    @Autowired
    public PromptAnswerService(@Qualifier("textPromptAnswerRepository") TextPromptAnswerRepository textPromptAnswerRepository,
                               @Qualifier("trueFalsePromptAnswerRepository") TrueFalsePromptAnswerRepository trueFalsePromptAnswerRepository
                                ) {
        // insert this up: , @Qualifier("drawingPromptAnswerRepository") DrawingPromptAnswerRepository drawingPromptAnswerRepository
        this.textPromptAnswerRepository = textPromptAnswerRepository;
        this.trueFalsePromptAnswerRepository = trueFalsePromptAnswerRepository;
        // and uncomment this: this.drawingPromptAnswerRepository = drawingPromptAnswerRepository;
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

    public Boolean saveTrueFalsePromptAnswer(TrueFalsePromptAnswer answer) {
        // TODO: add checks if answer is not null or if anything else is wrong?  (or is the try catch ok?)
        try {
            trueFalsePromptAnswerRepository.save(answer);
            trueFalsePromptAnswerRepository.flush();
            return true;
        } catch (Exception e) {
            System.out.printf("%s has occurred while trying to save the TrueFalseAnswer", e);
            return false;
        }
    }
}
