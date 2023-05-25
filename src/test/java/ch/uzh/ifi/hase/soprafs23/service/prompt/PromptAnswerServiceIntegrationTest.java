package ch.uzh.ifi.hase.soprafs23.service.prompt;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.constant.PromptType;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.DrawingPromptAnswer;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.Prompt;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.TextPromptAnswer;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.TrueFalsePromptAnswer;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.prompt.DrawingPromptAnswerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.prompt.PromptRepository;
import ch.uzh.ifi.hase.soprafs23.repository.prompt.TextPromptAnswerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.prompt.TrueFalsePromptAnswerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

@WebAppConfiguration
@SpringBootTest
class PromptAnswerServiceIntegrationTest {

    Game testGame;
    Player testPlayer;

    @Qualifier("textPromptAnswerRepository")
    @Autowired
    private TextPromptAnswerRepository textPromptAnswerRepository;

    @Qualifier("trueFalsePromptAnswerRepository")
    @Autowired
    private TrueFalsePromptAnswerRepository trueFalsePromptAnswerRepository;

    @Qualifier("drawingPromptAnswerRepository")
    @Autowired
    private DrawingPromptAnswerRepository drawingPromptAnswerRepository;

    @Qualifier("promptRepository")
    @Autowired
    private PromptRepository promptRepository;

    @Qualifier("gameRepository")
    @Autowired
    private GameRepository gameRepository;

    @Qualifier("playerRepository")
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PromptAnswerService promptAnswerService;

    @BeforeEach
    void setup() {
        textPromptAnswerRepository.deleteAll();
        trueFalsePromptAnswerRepository.deleteAll();
        drawingPromptAnswerRepository.deleteAll();
        gameRepository.deleteAll();
        playerRepository.deleteAll();

        testGame = new Game();
        testGame.setStatus(GameStatus.SELECTION);
        testGame.setGamePin("123456");
        testGame = gameRepository.save(testGame);
        gameRepository.flush();

        testPlayer = new Player();
        testPlayer.setPlayerName("testPlayer");
        testPlayer.setToken("playerToken");
        testPlayer.setAssociatedGamePin("123456");
        testPlayer = playerRepository.save(testPlayer);
        playerRepository.flush();
    }

    @Test
    void saveTextPromptAnswer_success() {
        Game foundGame = gameRepository.findByGamePin(testGame.getGamePin());
        Assertions.assertNotNull(foundGame);
        Player foundPlayer = playerRepository.findByToken(testPlayer.getToken());
        Assertions.assertNotNull(foundPlayer);

        TextPromptAnswer testTextPromptAnswer = new TextPromptAnswer();
        testTextPromptAnswer.setAssociatedPromptNr(1);
        testTextPromptAnswer.setAnswer("some answer");

        TextPromptAnswer found = promptAnswerService.saveTextPromptAnswer(testTextPromptAnswer, testPlayer.getToken(), testGame.getGamePin());
        Assertions.assertEquals(found.getAssociatedPromptNr(), testTextPromptAnswer.getAssociatedPromptNr());
        Assertions.assertEquals(found.getAnswer(), testTextPromptAnswer.getAnswer());
        Assertions.assertEquals(found.getAssociatedPlayerId(), testPlayer.getPlayerId());
        Assertions.assertEquals(found.getAssociatedGamePin(), testGame.getGamePin());
    }

    @Test
    void saveTrueFalsePromptAnswer_success() {
        Game foundGame = gameRepository.findByGamePin(testGame.getGamePin());
        Assertions.assertNotNull(foundGame);
        Player foundPlayer = playerRepository.findByToken(testPlayer.getToken());
        Assertions.assertNotNull(foundPlayer);

        TrueFalsePromptAnswer trueFalsePromptAnswer = new TrueFalsePromptAnswer();
        trueFalsePromptAnswer.setAssociatedPromptNr(1);
        trueFalsePromptAnswer.setAnswerBoolean(true);
        trueFalsePromptAnswer.setAnswerText("some text");

        TrueFalsePromptAnswer found = promptAnswerService.saveTrueFalsePromptAnswer(trueFalsePromptAnswer, testPlayer.getToken(), testGame.getGamePin());
        Assertions.assertEquals(found.getAssociatedPromptNr(), trueFalsePromptAnswer.getAssociatedPromptNr());
        Assertions.assertEquals(found.getAnswerBoolean(), trueFalsePromptAnswer.getAnswerBoolean());
        Assertions.assertEquals(found.getAnswerText(), trueFalsePromptAnswer.getAnswerText());
        Assertions.assertEquals(found.getAssociatedPlayerId(), testPlayer.getPlayerId());
        Assertions.assertEquals(found.getAssociatedGamePin(), testGame.getGamePin());
    }

    @Test
    void saveDrawingPromptAnswer_success() {
        Game foundGame = gameRepository.findByGamePin(testGame.getGamePin());
        Assertions.assertNotNull(foundGame);
        Player foundPlayer = playerRepository.findByToken(testPlayer.getToken());
        Assertions.assertNotNull(foundPlayer);

        DrawingPromptAnswer drawingPromptAnswer = new DrawingPromptAnswer();
        drawingPromptAnswer.setAssociatedPromptNr(1);
        drawingPromptAnswer.setAnswerDrawing("some drawing");

        DrawingPromptAnswer found = promptAnswerService.saveDrawingPromptAnswer(drawingPromptAnswer, testPlayer.getToken(), testGame.getGamePin());
        Assertions.assertEquals(found.getAssociatedPromptNr(), drawingPromptAnswer.getAssociatedPromptNr());
        Assertions.assertEquals(found.getAnswerDrawing(), drawingPromptAnswer.getAnswerDrawing());
        Assertions.assertEquals(found.getAssociatedPlayerId(), testPlayer.getPlayerId());
        Assertions.assertEquals(found.getAssociatedGamePin(), testGame.getGamePin());
    }

    @Test
    void haveAllPlayersAnsweredAllPrompts_shouldReturnTrue() {
        Prompt testPrompt = new Prompt();
        testPrompt.setPromptType(PromptType.TEXT);
        testPrompt.setPromptText("prompt text");
        testPrompt.setPromptNr(900);
        testPrompt = promptRepository.save(testPrompt);
        promptRepository.flush();

        testGame.setPromptSet(List.of(testPrompt));
        testGame.addPlayer(testPlayer);
        testGame.setStatus(GameStatus.PROMPT);
        testGame = gameRepository.save(testGame);
        gameRepository.flush();

        TextPromptAnswer testTextPromptAnswer = new TextPromptAnswer();
        testTextPromptAnswer.setAssociatedPromptNr(testPrompt.getPromptNr());
        testTextPromptAnswer.setAnswer("some answer to testPrompt");
        testTextPromptAnswer.setAssociatedPlayerId(testPlayer.getPlayerId());
        testTextPromptAnswer.setUsedAsCorrectAnswer(false);
        testTextPromptAnswer.setAssociatedGamePin(testGame.getGamePin());
        textPromptAnswerRepository.save(testTextPromptAnswer);
        textPromptAnswerRepository.flush();

        TextPromptAnswer foundAnswer = textPromptAnswerRepository.findTextPromptAnswerByAssociatedPlayerIdAndAssociatedPromptNr(testPlayer.getPlayerId(), testPrompt.getPromptNr());
        Assertions.assertNotNull(foundAnswer);

        Game foundGame = gameRepository.findByGamePin(testGame.getGamePin());
        Assertions.assertNotNull(foundGame);
        Assertions.assertEquals(1, foundGame.getPromptSet().size());
        Assertions.assertEquals(GameStatus.PROMPT, foundGame.getStatus());

        Boolean haveAllAnswered = promptAnswerService.haveAllPlayersAnsweredAllPrompts(testGame.getGamePin());
        Assertions.assertTrue(haveAllAnswered);
    }
}
