package ch.uzh.ifi.hase.soprafs23.service.prompt;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.constant.PromptType;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.*;
import ch.uzh.ifi.hase.soprafs23.entity.quiz.QuizQuestion;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.prompt.DrawingPromptAnswerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.prompt.TextPromptAnswerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.prompt.TrueFalsePromptAnswerRepository;
import ch.uzh.ifi.hase.soprafs23.service.quiz.QuizQuestionGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class PromptAnswerService {
    private final Logger log = LoggerFactory.getLogger(PromptAnswerService.class);
    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final TextPromptAnswerRepository textPromptAnswerRepository;
    private final TrueFalsePromptAnswerRepository trueFalsePromptAnswerRepository;
    private final DrawingPromptAnswerRepository drawingPromptAnswerRepository;
    private QuizQuestionGenerator quizQuestionGenerator;

    private static final String NO_ANSWER_MESSAGE = "No answer provided";
    private static final String LOGGER_CAPSULE = "created  new: {}";


    // only one can have @Autowired - what does that do, and does it work like this with the two constructors?
    // I have no idea if this is going to work, but it seems better than multiple constructors...
    // the question is, do I need to pass these arguments? - bc in the Controller, where this constructor is used,
    // there are no arguments passed... just a blank call.
    @Autowired
    public PromptAnswerService(@Qualifier("textPromptAnswerRepository") TextPromptAnswerRepository textPromptAnswerRepository,
                               @Qualifier("trueFalsePromptAnswerRepository") TrueFalsePromptAnswerRepository trueFalsePromptAnswerRepository,
                               @Qualifier("drawingPromptAnswerRepository") DrawingPromptAnswerRepository drawingPromptAnswerRepository,
                               @Qualifier("gameRepository") GameRepository gameRepository,
                               @Qualifier("playerRepository") PlayerRepository playerRepository) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.textPromptAnswerRepository = textPromptAnswerRepository;
        this.trueFalsePromptAnswerRepository = trueFalsePromptAnswerRepository;
        this.drawingPromptAnswerRepository = drawingPromptAnswerRepository;
    }

    @Autowired
    private void setQuizQuestionGenerator(QuizQuestionGenerator quizQuestionGenerator) {
        this.quizQuestionGenerator = quizQuestionGenerator;
    }


    public TextPromptAnswer saveTextPromptAnswer(TextPromptAnswer answer, String playerToken, String gamePin) {
        findGameByPin(gamePin);
        answer.setAssociatedGamePin(gamePin);
        Player player = getByToken(playerToken);
        answer.setAssociatedPlayerId(player.getPlayerId());

        if (answer.getAnswer().equals("")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, NO_ANSWER_MESSAGE);
        }

        textPromptAnswerRepository.save(answer);
        textPromptAnswerRepository.flush();

        log.debug(LOGGER_CAPSULE, answer);
        return answer;
    }

    public TrueFalsePromptAnswer saveTrueFalsePromptAnswer(TrueFalsePromptAnswer answer, String playerToken, String gamePin) {
        findGameByPin(gamePin);
        answer.setAssociatedGamePin(gamePin);
        Player player = getByToken(playerToken);
        answer.setAssociatedPlayerId(player.getPlayerId());

        if (answer.getAnswerText().equals("")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, NO_ANSWER_MESSAGE);
        }
        // test if there is a boolean value in answerBoolean...?

        trueFalsePromptAnswerRepository.save(answer);
        trueFalsePromptAnswerRepository.flush();

        log.debug(LOGGER_CAPSULE, answer);
        return answer;
    }

    public DrawingPromptAnswer saveDrawingPromptAnswer(DrawingPromptAnswer answer, String playerToken, String gamePin) {
        findGameByPin(gamePin);
        answer.setAssociatedGamePin(gamePin);
        Player player = getByToken(playerToken);
        answer.setAssociatedPlayerId(player.getPlayerId());

        if (answer.getAnswerDrawing().equals("")) { // throws if the content is empty
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, NO_ANSWER_MESSAGE);
        }

        drawingPromptAnswerRepository.save(answer);
        drawingPromptAnswerRepository.flush();

        log.debug(LOGGER_CAPSULE, answer);
        return answer;
    }


    // check if all users have answered all prompts
    // change game status below in a separate method
    public Boolean haveAllPlayersAnsweredAllPrompts(String gamePin) {
        Game gameByPin = findGameByPin(gamePin);

        List<Player> players = gameByPin.getPlayerGroup();
        List<Prompt> prompts = gameByPin.getPromptSet();

        for (Player player : players) {
            Long playerId = player.getPlayerId();
            for (Prompt prompt : prompts) {
                PromptType type = prompt.getPromptType();
                int promptNr = prompt.getPromptNr();
                PromptAnswer found = null;
                // TODO: maybe refactor to use overloading instead of switch statement
                switch (type) {
                    case TEXT -> found = textPromptAnswerRepository
                            .findTextPromptAnswerByAssociatedPlayerIdAndAssociatedPromptNr(playerId, promptNr);
                    case DRAWING -> found = drawingPromptAnswerRepository
                            .findDrawingPromptAnswerByAssociatedPlayerIdAndAssociatedPromptNr(playerId, promptNr);
                    case TRUEFALSE -> found = trueFalsePromptAnswerRepository
                            .findTrueFalsePromptAnswerByAssociatedPlayerIdAndAssociatedPromptNr(playerId, promptNr);

                }
                if (found == null) {
                    return false;
                }
            }
        }
        return true;
    }

    // game controller method
    public void changeFromPromptAnsweringToQuizStage(String gamePin) {

        if (Boolean.TRUE.equals(haveAllPlayersAnsweredAllPrompts(gamePin))) {
            // change Status to Quiz
            findGameByPin(gamePin).setStatus(GameStatus.QUIZ);

            // initialize change to Quiz stage
            List<QuizQuestion> generatedQuestions = quizQuestionGenerator.createQuizQuestions(gamePin);
            log.debug("Generated {} questions", generatedQuestions.size());
        }
    }


    private Game findGameByPin(String gamePin) {
        Game foundGame = gameRepository.findByGamePin(gamePin);
        if (foundGame == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No game with this pin found.");
        }
        return foundGame;
    }


    private Player getByToken(String token) {
        Player player = playerRepository.findByToken(token);
        if (player == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No player with this token found.");
        }
        return player;
    }
}


