package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.Prompt;
import ch.uzh.ifi.hase.soprafs23.entity.quiz.QuizQuestion;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.prompt.DrawingPromptAnswerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.prompt.TextPromptAnswerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.prompt.TrueFalsePromptAnswerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.quiz.QuizQuestionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class GameService {

    private final Logger log = LoggerFactory.getLogger(GameService.class);

    private final GameRepository gameRepository;

    private final PlayerRepository playerRepository;


    private final DrawingPromptAnswerRepository drawingPromptAnswerRepository;

    private final TextPromptAnswerRepository textPromptAnswerRepository;

    private final TrueFalsePromptAnswerRepository trueFalsePromptAnswerRepository;

    private final QuizQuestionRepository quizQuestionRepository;

    private final Random rand = SecureRandom.getInstanceStrong();

    @Autowired
    public GameService(@Qualifier("gameRepository") GameRepository gameRepository,
                       @Qualifier("playerRepository") PlayerRepository playerRepository,
                       @Qualifier("textPromptAnswerRepository") TextPromptAnswerRepository textPromptAnswerRepository,
                       @Qualifier("trueFalsePromptAnswerRepository") TrueFalsePromptAnswerRepository trueFalsePromptAnswerRepository,
                       @Qualifier("drawingPromptAnswerRepository") DrawingPromptAnswerRepository drawingPromptAnswerRepository,
                       @Qualifier("quizQuestionRepository") QuizQuestionRepository quizQuestionRepository
                       ) throws NoSuchAlgorithmException {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.textPromptAnswerRepository = textPromptAnswerRepository;
        this.drawingPromptAnswerRepository = drawingPromptAnswerRepository;
        this.trueFalsePromptAnswerRepository = trueFalsePromptAnswerRepository;
        this.quizQuestionRepository = quizQuestionRepository;
    }


    //TODO: test Integration?
    public List<Game> getGames() {
        return this.gameRepository.findAll();
    }

    //TODO: test Integration?
    public Game getGameByPin(String pin) {
        Game gameByPin = gameRepository.findByGamePin(pin);
        if (gameByPin == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No game with this pin found.");
        }
        else {
            return gameByPin;
        }
    }

    public Game createGame() {
        Game newGame = new Game();
        newGame.setStatus(GameStatus.LOBBY);

        String pin = generateUniqueGamePin();
        newGame.setGamePin(pin);

        newGame = gameRepository.save(newGame);
        gameRepository.flush();

        log.debug("Created game: {}", newGame);
        return newGame;
    }

    //TODO: test Integration?
    //TODO: test Service
    public Game changeGameStatus(GameStatus requestedStatus, String gamePin, String loggedInToken) {
        System.out.println(requestedStatus);
        //GameStatus newStatus = GameStatus.transformToStatus(requestedStatus);


        Player loggedInPlayer = playerRepository.findByToken(loggedInToken);
        if (loggedInPlayer == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No player with this token found.");
        }

        Game gameByPin = getGameByPin(gamePin);
        if (!checkIfHost(gameByPin, loggedInPlayer.getPlayerId())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorised to do this action.");
        }

        if(requestedStatus == GameStatus.LOBBY){
            gameByPin.emptyPromptSet();
            gameByPin.emptyQuizQuestions();
            quizQuestionRepository.deleteAllByAssociatedGamePin(gamePin);
            drawingPromptAnswerRepository.deleteAllByAssociatedGamePin(gamePin);
            textPromptAnswerRepository.deleteAllByAssociatedGamePin(gamePin);
            trueFalsePromptAnswerRepository.deleteAllByAssociatedGamePin(gamePin);
            for(Player player : gameByPin.getPlayerGroup()){
                player.setScore(0);
            }
        }
        gameByPin.setStatus(requestedStatus);
        gameByPin = gameRepository.save(gameByPin);
        gameRepository.flush();

        return gameByPin;
    }

    //TODO: test Integration?
    //TODO: test Service
    public Game deleteGameByPin(String gamePin, String loggedInToken) {
        Game gameByPin = getGameByPin(gamePin);
        Player loggedInPlayer = playerRepository.findByToken(loggedInToken);
        if (loggedInPlayer == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No player with this token found.");
        }

        if (!checkIfHost(gameByPin, loggedInPlayer.getPlayerId())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorised to do this action.");
        }

        gameRepository.deleteByGamePin(gamePin);
        drawingPromptAnswerRepository.deleteAllByAssociatedGamePin(gamePin);
        textPromptAnswerRepository.deleteAllByAssociatedGamePin(gamePin);
        trueFalsePromptAnswerRepository.deleteAllByAssociatedGamePin(gamePin);

        return gameByPin;
    }


    public Game changeToNextQuestion(String gamePin, String loggedInToken){
        Game gameByPin = getGameByPin(gamePin);
        Player loggedInPlayer = playerRepository.findByToken(loggedInToken);
        if (!checkIfHost(gameByPin, loggedInPlayer.getPlayerId())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorised to do this action.");
        }
        gameByPin.nextQuestion();
        if(gameByPin.nextQuestion() == null && gameByPin.getStatus() == GameStatus.QUIZ){
            gameByPin.setStatus(GameStatus.END);
        }

        return gameByPin;
    }


    /**
     * Helper functions
     */

    private boolean checkIfHost(Game game, long userId) {
        return game.getHostId() == userId;
    }

    //TODO: test?
    private String generateUniqueGamePin() {

        StringBuilder pin = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            pin.append(rand.nextInt(10));
        }
        String pinString = pin.toString();

        if (gameRepository.findByGamePin(pinString) == null) {
            return pinString;
        }
        else {
            return generateUniqueGamePin();
        }
    }

}
