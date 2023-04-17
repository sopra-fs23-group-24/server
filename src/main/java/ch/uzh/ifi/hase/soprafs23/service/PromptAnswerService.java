package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.PromptType;
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
import java.sql.SQLOutput;

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
                               @Qualifier("trueFalsePromptAnswerRepository") TrueFalsePromptAnswerRepository trueFalsePromptAnswerRepository,
                               @Qualifier("drawingPromptAnswerRepository") DrawingPromptAnswerRepository drawingPromptAnswerRepository) {
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

        drawingPromptAnswerRepository.save(answer);
        drawingPromptAnswerRepository.flush();

        log.debug("created  new: {}", answer);
        return answer;
    }

    public void mockPromptAnswersForGame(String gamePin){
        Game currentGame = gameService.getGameByPin(gamePin);
        for(Player player : currentGame.getPlayerGroup()){
            for(Prompt prompt : currentGame.getPromptSet()){
                if(prompt.getPromptType() == PromptType.DRAWING){
                    DrawingPromptAnswer drawAnswer = new DrawingPromptAnswer();
                    drawAnswer.setAnswerDrawing("some drawing for " + player.getPlayerName());
                    drawAnswer.setAssociatedPromptNr(prompt.getPromptNr());
                    DrawingPromptAnswer saved = saveDrawingPromptAnswer(drawAnswer, player.getToken(), gamePin);
                    System.out.println("Saved PromptAnswer '" + saved.getDrawingPromptAnswerId() + "' for player '" + saved.getAssociatedPlayerId() + "' with content '" + saved.getAnswerDrawing() + "'");
                }else if(prompt.getPromptType() == PromptType.TEXT){
                    TextPromptAnswer textAnswer = new TextPromptAnswer();
                    textAnswer.setAnswer("some answer from " + player.getPlayerName());
                    textAnswer.setAssociatedPromptNr(prompt.getPromptNr());
                    TextPromptAnswer saved = saveTextPromptAnswer(textAnswer, player.getToken(), gamePin);
                    System.out.println("Saved PromptAnswer '" + saved.getTextPromptAnswerId() + "' for player '" + saved.getAssociatedPlayerId() + "' with content '" + saved.getAnswer() + "'");
                }else{
                    TrueFalsePromptAnswer tfAnswer = new TrueFalsePromptAnswer();
                    tfAnswer.setAnswerText("some story from " + player.getPlayerName());
                    if(Math.random() > 0.5){
                        tfAnswer.setAnswerBoolean(true);
                    }else{
                        tfAnswer.setAnswerBoolean(false);
                    }
                    tfAnswer.setAssociatedPromptNr(prompt.getPromptNr());
                    TrueFalsePromptAnswer saved = saveTrueFalsePromptAnswer(tfAnswer, player.getToken(), gamePin);
                    System.out.println("Saved PromptAnswer '" + saved.getTrueFalsePromptAnswerId() + "' for player '" + saved.getAssociatedPlayerId() + "' with content '" + saved.getAnswerText() + "' which is set to '" + saved.getAnswerBoolean() + "'");
                }
            }
        }
    }
}
