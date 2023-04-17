package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.*;
import ch.uzh.ifi.hase.soprafs23.helpers.ZipDataURL;
import ch.uzh.ifi.hase.soprafs23.repository.DrawingPromptAnswerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.TextPromptAnswerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.TrueFalsePromptAnswerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.swing.text.BadLocationException;
import java.io.IOException;

@Service

public class PromptAnswerService {
    private final Logger log = LoggerFactory.getLogger(PromptAnswerService.class);

    private GameService gameService;
    private PlayerService playerService;
    private final TextPromptAnswerRepository textPromptAnswerRepository;
    private final TrueFalsePromptAnswerRepository trueFalsePromptAnswerRepository;
    private final DrawingPromptAnswerRepository drawingPromptAnswerRepository;


    // only one can have @Autowired - what does that do, and does it work like this with the two constructors?
    // I have no idea if this is going to work, but it seems better than multiple constructors...
    // the question is, do I need to pass these arguments? - bc in the Controller, where this constructor is used,
    // there are no arguments passed... just a blank call.
    @Autowired
    public PromptAnswerService(@Qualifier("textPromptAnswerRepository") TextPromptAnswerRepository textPromptAnswerRepository,
                               @Qualifier("trueFalsePromptAnswerRepository") TrueFalsePromptAnswerRepository trueFalsePromptAnswerRepository
            , @Qualifier("drawingPromptAnswerRepository") DrawingPromptAnswerRepository drawingPromptAnswerRepository) {
        this.textPromptAnswerRepository = textPromptAnswerRepository;
        this.trueFalsePromptAnswerRepository = trueFalsePromptAnswerRepository;
        this.drawingPromptAnswerRepository = drawingPromptAnswerRepository;
    }

    @Autowired
    private void setGameService(GameService gameService) {
        this.gameService = gameService;
    }

    @Autowired
    private void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }


    public TextPromptAnswer saveTextPromptAnswer(TextPromptAnswer answer, String playerToken, String gamePin) {

        gameService.getGameByPin(gamePin); // throws if not correct
        answer.setAssociatedGamePin(gamePin);
        Player player = playerService.getByToken(playerToken); //throws if not correct
        answer.setAssociatedPlayerId(player.getPlayerId());

        if (answer.getAnswer().equals("")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No answer provided");
        }

        textPromptAnswerRepository.save(answer);
        textPromptAnswerRepository.flush();

        log.debug("created  new: {}", answer);
        return answer;
    }

    public TrueFalsePromptAnswer saveTrueFalsePromptAnswer(TrueFalsePromptAnswer answer, String playerToken, String gamePin) {
        gameService.getGameByPin(gamePin); // throws if not correct
        answer.setAssociatedGamePin(gamePin);
        Player player = playerService.getByToken(playerToken); //throws if not correct
        answer.setAssociatedPlayerId(player.getPlayerId());

        if (answer.getAnswerText().equals("")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No answer provided");
        }
        // test if there is a boolean value in answerBoolean...?

        trueFalsePromptAnswerRepository.save(answer);
        trueFalsePromptAnswerRepository.flush();

        log.debug("created  new: {}", answer);
        return answer;
    }

    public DrawingPromptAnswer saveDrawingPromptAnswer(DrawingPromptAnswer answer, String playerToken, String gamePin) {

        gameService.getGameByPin(gamePin); // throws if not correct
        answer.setAssociatedGamePin(gamePin);
        Player player = playerService.getByToken(playerToken); //throws if not correct
        answer.setAssociatedPlayerId(player.getPlayerId());

        if (answer.getAnswerDrawing().equals("")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No answer provided");
        }

        // zip the answer to be smaller:
        try{
            String shortAnswer = ZipDataURL.zip(answer.getAnswerDrawing());
            answer.setAnswerDrawing(shortAnswer);
        }catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "image could not be zipped.");
        }


        drawingPromptAnswerRepository.save(answer);
        drawingPromptAnswerRepository.flush();

        log.debug("created  new: {}", answer);
        return answer;
    }
}
