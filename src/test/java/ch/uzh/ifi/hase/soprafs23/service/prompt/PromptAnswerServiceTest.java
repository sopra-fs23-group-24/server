package ch.uzh.ifi.hase.soprafs23.service.prompt;

import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.TextPromptAnswer;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.prompt.DrawingPromptAnswerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.prompt.TextPromptAnswerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.prompt.TrueFalsePromptAnswerRepository;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class PromptAnswerServiceTest {
    private Game testGame;

    private Player testPlayer;
    private TextPromptAnswer testTPAnswer;

    @Mock
    private TextPromptAnswerRepository textPromptAnswerRepository;
    @Mock
    private TrueFalsePromptAnswerRepository trueFalsePromptAnswerRepository;
    @Mock
    private DrawingPromptAnswerRepository drawingPromptAnswerRepository;


    @InjectMocks
    private PromptAnswerService promptAnswerService;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testGame = new Game();
        testGame.setGameId(1L);
        testGame.setGamePin("123456");
        testGame.setHostId(2L); //bc cannot be 1 bc game is 1 already

        testPlayer = new Player();
        testPlayer.setPlayerId(2L);
        testPlayer.setAssociatedGamePin("123456");
        testPlayer.setPlayerName("test");
        testPlayer.setToken("1");

        testTPAnswer = new TextPromptAnswer();
        testTPAnswer.setAssociatedPromptNr(1);
        testTPAnswer.setAssociatedPlayerId(2L);
        testTPAnswer.setAssociatedGamePin("123456");
        testTPAnswer.setAnswer("TestAnswer");

    }

    @Test
    void testSaveTextPromptAnswer_success() {
        // the problem is that within the promptAnswerService, the gameService is null.

        // set it with metaprogramming but would be hard and ... -> promptAnswerService.setGameService

        //promptAnswerService.saveTextPromptAnswer(testTPAnswer,testPlayer.getToken(), testGame.getGamePin());
        //Mockito.verify(textPromptAnswerRepository, Mockito.times(1)).save(Mockito.any());


    }

    @Test
    void testSaveTextPromptAnswer_failure() {
    }

    @Test
    void testSaveTrueFalsePromptAnswer() {
    }

    @Test
    void saveDrawingPromptAnswer() {
    }

    @Test
    void haveAllPlayersAnsweredAllPrompts() {
    }

    @Test
    void changeFromPromptAnsweringToQuizStage() {
    }

    @Test
    void deleteAllPromptAnswersByGamePin() {
    }

    @Test
    void getAllTextPromptAnswers() {
    }

    @Test
    void getAllTextPromptAnswersByGamePin() {
    }
}