package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
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
 * Game Service
 * This class is the "worker" and responsible for all functionality related to
 * the game
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
                       @Qualifier("quizQuestionRepository") QuizQuestionRepository quizQuestionRepository)
            throws NoSuchAlgorithmException {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.textPromptAnswerRepository = textPromptAnswerRepository;
        this.drawingPromptAnswerRepository = drawingPromptAnswerRepository;
        this.trueFalsePromptAnswerRepository = trueFalsePromptAnswerRepository;
        this.quizQuestionRepository = quizQuestionRepository;
    }


    //TODO: test Integration?
    //what do we need this for? - except testing for ourselves
    public List<Game> getGames() {
        return this.gameRepository.findAll();
    }

    //TODO: test Integration?
    public Game getGameByPin(String pin) {
        Game gameByPin = gameRepository.findByGamePin(pin);
        if (gameByPin == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No game with this pin found.");
        }
        // can this else be removed? since we throw anyway if null above. I think so
        //else {
        return gameByPin;
        //}
    }

    public Game createGame() {
        Game newGame = new Game();
        // we already set the status to be LOBBY by default / by initialization ...
        // newGame.setStatus(GameStatus.LOBBY);

        // generate and set gamePin
        String pin = generateUniqueGamePin();
        newGame.setGamePin(pin);

        // at this point a game should have a gameId, the LOBBY status, and a unique gamePin
        newGame = gameRepository.save(newGame);
        gameRepository.flush();

        log.debug("Created game: {}", newGame);
        return newGame;
    }

    //TODO: test Integration?
    //TODO: test Service

    // throws when no player or a non-host player tries to change the status
    // returns the same game, but which now has a new status
    public Game changeGameStatus(GameStatus requestedStatus, String gamePin, String loggedInToken) {

        Player loggedInPlayer = playerRepository.findByToken(loggedInToken);
        if (loggedInPlayer == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No player with this token found.");
        }

        Game gameByPin = getGameByPin(gamePin);
        if (!checkIfHost(gameByPin, loggedInPlayer.getPlayerId())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorised to do this action.");
        }

        if (requestedStatus == GameStatus.LOBBY) {
            // TODO: do we want checks that e.g. it is only possible to switch to lobby from the END?

            //reset player scores
            for (Player player : gameByPin.getPlayerGroup()) {
                player.setScore(0);
            }
        }

        // TODO: add additional checks for changes to other statuses (best in other methods probably, to
        //  not inflate this method here too much.)
        // checks for changeToSelection
        // checks for changeToPrompt
        // checks for changeToQuiz
        // checks for changeToEnd - Linda

        // actual changing and saving of the game / its status
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

        return gameByPin;
    }


    public Game changeToNextQuestion(String gamePin, String loggedInToken) {
        Game gameByPin = getGameByPin(gamePin);
        Player loggedInPlayer = playerRepository.findByToken(loggedInToken);
        if (!checkIfHost(gameByPin, loggedInPlayer.getPlayerId())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorised to do this action.");
        }
        QuizQuestion currentQuestion = gameByPin.nextQuestion();
        if (currentQuestion == null && gameByPin.getStatus() == GameStatus.QUIZ) {
            // clear all prompts, questions, answers
            gameByPin.emptyPromptSet();
            gameByPin.emptyQuizQuestions();
            quizQuestionRepository.deleteAllByAssociatedGamePin(gamePin);
            drawingPromptAnswerRepository.deleteAllByAssociatedGamePin(gamePin);
            textPromptAnswerRepository.deleteAllByAssociatedGamePin(gamePin);
            trueFalsePromptAnswerRepository.deleteAllByAssociatedGamePin(gamePin);

            gameByPin.setStatus(GameStatus.END);
        }
        gameByPin = gameRepository.save(gameByPin);
        gameRepository.flush();

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

        // if not already present: return, else: try again
        if (gameRepository.findByGamePin(pinString) == null) {
            return pinString;
        }
        else {
            return generateUniqueGamePin();
        }
    }

}
