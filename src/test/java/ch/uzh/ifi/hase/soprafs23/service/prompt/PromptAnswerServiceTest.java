package ch.uzh.ifi.hase.soprafs23.service.prompt;

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
import ch.uzh.ifi.hase.soprafs23.repository.prompt.TextPromptAnswerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.prompt.TrueFalsePromptAnswerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class PromptAnswerServiceTest {
    private Game testGame;
    private Player testPlayer;
    private TextPromptAnswer testTextPromptAnswer;
    private DrawingPromptAnswer testDrawingPromptAnswer;
    private TrueFalsePromptAnswer testTFPromptAnswer;

    @Mock
    private TextPromptAnswerRepository textPromptAnswerRepository;
    @Mock
    private TrueFalsePromptAnswerRepository trueFalsePromptAnswerRepository;
    @Mock
    private DrawingPromptAnswerRepository drawingPromptAnswerRepository;
    @Mock
    private GameRepository gameRepository;
    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private PromptAnswerService promptAnswerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testGame = new Game();
        testGame.setGameId(1L);
        testGame.setGamePin("123456");
        testGame.setHostId(2L);
        Mockito.when(gameRepository.findByGamePin(testGame.getGamePin())).thenReturn(testGame);
        Mockito.when(gameRepository.findByGamePin("invalidPin")).thenReturn(null);


        testPlayer = new Player();
        testPlayer.setPlayerId(2L);
        testPlayer.setAssociatedGamePin("123456");
        testPlayer.setPlayerName("test");
        testPlayer.setToken("1");
        Mockito.when(playerRepository.findByToken(testPlayer.getToken())).thenReturn(testPlayer);
        Mockito.when(playerRepository.findByToken("invalidToken")).thenReturn(null);

        testTextPromptAnswer = new TextPromptAnswer();
        testTextPromptAnswer.setAssociatedPromptNr(3);
        testTextPromptAnswer.setAnswer("TestAnswer");
        Mockito.when(textPromptAnswerRepository.save(Mockito.any())).thenReturn(testTextPromptAnswer);

        testDrawingPromptAnswer = new DrawingPromptAnswer();
        testDrawingPromptAnswer.setAssociatedPromptNr(2);
        testDrawingPromptAnswer.setAnswerDrawing("Drawing");
        Mockito.when(drawingPromptAnswerRepository.save(Mockito.any())).thenReturn(testDrawingPromptAnswer);


        testTFPromptAnswer = new TrueFalsePromptAnswer();
        testTFPromptAnswer.setAssociatedPromptNr(1);
        testTFPromptAnswer.setAnswerText("TestAnswer");
        testTFPromptAnswer.setAnswerBoolean(true);
        Mockito.when(trueFalsePromptAnswerRepository.save(Mockito.any())).thenReturn(testTFPromptAnswer);

    }

    @Test
    void saveTextPromptAnswer_success() {
        TextPromptAnswer savedAnswer = promptAnswerService.saveTextPromptAnswer(testTextPromptAnswer, testPlayer.getToken(), testGame.getGamePin());

        Assertions.assertEquals(savedAnswer.getAnswer(), testTextPromptAnswer.getAnswer());
        Assertions.assertEquals(savedAnswer.getAssociatedPromptNr(), testTextPromptAnswer.getAssociatedPromptNr());
        Assertions.assertEquals(savedAnswer.getAssociatedPlayerId(), testPlayer.getPlayerId());
        Assertions.assertEquals(savedAnswer.getAssociatedGamePin(), testGame.getGamePin());
        Mockito.verify(textPromptAnswerRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void saveTextPromptAnswer_invalidPin() {
        assertThrows(ResponseStatusException.class, () -> promptAnswerService.saveTextPromptAnswer(testTextPromptAnswer, testPlayer.getToken(), "invalidPin"));
    }

    @Test
    void saveTextPromptAnswer_invalidToken() {
        assertThrows(ResponseStatusException.class, () -> promptAnswerService.saveTextPromptAnswer(testTextPromptAnswer, "invalidToken", testGame.getGamePin()));
    }

    @Test
    void saveTextPromptAnswer_emptyAnswer() {
        testTextPromptAnswer.setAnswer("");
        assertThrows(ResponseStatusException.class, () -> promptAnswerService.saveTextPromptAnswer(testTextPromptAnswer, testPlayer.getToken(), testGame.getGamePin()));
    }

    @Test
    void saveTrueFalsePromptAnswer_success() {
        TrueFalsePromptAnswer savedAnswer = promptAnswerService.saveTrueFalsePromptAnswer(testTFPromptAnswer, testPlayer.getToken(), testGame.getGamePin());

        Assertions.assertEquals(savedAnswer.getAnswerText(), testTFPromptAnswer.getAnswerText());
        Assertions.assertEquals(savedAnswer.getAnswerBoolean(), testTFPromptAnswer.getAnswerBoolean());
        Assertions.assertEquals(savedAnswer.getAssociatedPromptNr(), testTFPromptAnswer.getAssociatedPromptNr());
        Assertions.assertEquals(savedAnswer.getAssociatedPlayerId(), testPlayer.getPlayerId());
        Assertions.assertEquals(savedAnswer.getAssociatedGamePin(), testGame.getGamePin());
        Mockito.verify(trueFalsePromptAnswerRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void saveTrueFalsePromptAnswer_invalidPin() {
        assertThrows(ResponseStatusException.class, () -> promptAnswerService.saveTrueFalsePromptAnswer(testTFPromptAnswer, testPlayer.getToken(), "invalidPin"));
    }

    @Test
    void saveTrueFalsePromptAnswer_invalidToken() {
        assertThrows(ResponseStatusException.class, () -> promptAnswerService.saveTrueFalsePromptAnswer(testTFPromptAnswer, "invalidToken", testGame.getGamePin()));
    }

    @Test
    void saveTrueFalsePromptAnswer_emptyAnswer() {
        testTFPromptAnswer.setAnswerText("");
        assertThrows(ResponseStatusException.class, () -> promptAnswerService.saveTrueFalsePromptAnswer(testTFPromptAnswer, testPlayer.getToken(), testGame.getGamePin()));
    }

    @Test
    void saveDrawingPromptAnswer_success() {
        DrawingPromptAnswer savedAnswer = promptAnswerService.saveDrawingPromptAnswer(testDrawingPromptAnswer, testPlayer.getToken(), testGame.getGamePin());

        Assertions.assertEquals(savedAnswer.getAnswerDrawing(), testDrawingPromptAnswer.getAnswerDrawing());
        Assertions.assertEquals(savedAnswer.getAssociatedPromptNr(), testDrawingPromptAnswer.getAssociatedPromptNr());
        Assertions.assertEquals(savedAnswer.getAssociatedPlayerId(), testPlayer.getPlayerId());
        Assertions.assertEquals(savedAnswer.getAssociatedGamePin(), testGame.getGamePin());
        Mockito.verify(drawingPromptAnswerRepository, Mockito.times(1)).save(Mockito.any());

    }

    @Test
    void saveDrawingPromptAnswer_invalidPin() {
        assertThrows(ResponseStatusException.class, () -> promptAnswerService.saveDrawingPromptAnswer(testDrawingPromptAnswer, testPlayer.getToken(), "invalidPin"));
    }

    @Test
    void saveDrawingPromptAnswer_invalidToken() {
        assertThrows(ResponseStatusException.class, () -> promptAnswerService.saveDrawingPromptAnswer(testDrawingPromptAnswer, "invalidToken", testGame.getGamePin()));
    }

    @Test
    void saveDrawingPromptAnswer_emptyAnswer() {
        testDrawingPromptAnswer.setAnswerDrawing("");
        assertThrows(ResponseStatusException.class, () -> promptAnswerService.saveDrawingPromptAnswer(testDrawingPromptAnswer, testPlayer.getToken(), testGame.getGamePin()));
    }

    @Test
    void haveAllPlayersAnsweredAllPrompts_true() {
        Prompt testPrompt1 = new Prompt();
        testPrompt1.setPromptType(PromptType.TEXT);
        testPrompt1.setPromptNr(99);
        Prompt testPrompt2 = new Prompt();
        testPrompt2.setPromptType(PromptType.DRAWING);
        testPrompt2.setPromptNr(100);
        Prompt testPrompt3 = new Prompt();
        testPrompt3.setPromptType(PromptType.TRUEFALSE);
        testPrompt3.setPromptNr(101);

        testGame.setPromptSet(List.of(testPrompt1, testPrompt2, testPrompt3));
        testGame.setPlayerGroup(List.of(testPlayer));

        Mockito.when(textPromptAnswerRepository.findTextPromptAnswerByAssociatedPlayerIdAndAssociatedPromptNr(Mockito.anyLong(), Mockito.anyInt()))
                .thenReturn(testTextPromptAnswer);
        Mockito.when(drawingPromptAnswerRepository.findDrawingPromptAnswerByAssociatedPlayerIdAndAssociatedPromptNr(Mockito.anyLong(), Mockito.anyInt()))
                .thenReturn(testDrawingPromptAnswer);
        Mockito.when(trueFalsePromptAnswerRepository.findTrueFalsePromptAnswerByAssociatedPlayerIdAndAssociatedPromptNr(Mockito.anyLong(), Mockito.anyInt()))
                .thenReturn(testTFPromptAnswer);

        Boolean returnedBoolean = promptAnswerService.haveAllPlayersAnsweredAllPrompts(testGame.getGamePin());
        Assertions.assertTrue(returnedBoolean);
    }

    @Test
    void haveAllPlayersAnsweredAllPrompts_false() {
        Prompt testPrompt = new Prompt();
        testPrompt.setPromptType(PromptType.TEXT);
        testPrompt.setPromptNr(99);

        testGame.setPromptSet(List.of(testPrompt));
        testGame.setPlayerGroup(List.of(testPlayer));

        Mockito.when(textPromptAnswerRepository.findTextPromptAnswerByAssociatedPlayerIdAndAssociatedPromptNr(Mockito.anyLong(), Mockito.anyInt()))
                .thenReturn(null);
        Boolean returnedBoolean = promptAnswerService.haveAllPlayersAnsweredAllPrompts(testGame.getGamePin());
        Assertions.assertFalse(returnedBoolean);
    }
}