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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {
    private Game testGame;

    private Player testPlayer;

    @Mock
    private GameRepository gameRepository;
    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private DrawingPromptAnswerRepository drawingPromptAnswerRepository;

    @Mock
    private TextPromptAnswerRepository textPromptAnswerRepository;

    @Mock
    private TrueFalsePromptAnswerRepository trueFalsePromptAnswerRepository;

    @Mock
    private QuizQuestionRepository quizQuestionRepository;

    @InjectMocks
    private GameService gameService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        testPlayer = new Player();
        testPlayer.setPlayerId(2L);
        testPlayer.setAssociatedGamePin("123456");
        testPlayer.setPlayerName("test");
        testPlayer.setToken("1");
        Mockito.when(playerRepository.findByToken(Mockito.anyString())).thenReturn(testPlayer);

        testGame = new Game();
        testGame.setGameId(1L);
        testGame.setGamePin("123456");
        testGame.setHostId(2L); //bc cannot be 1 bc game is 1 already
        testGame.setPlayerGroup(List.of(testPlayer, testPlayer, testPlayer, testPlayer));

        Mockito.when(gameRepository.save(Mockito.any())).thenReturn(testGame);
        Mockito.when(gameRepository.findByGamePin(testGame.getGamePin())).thenReturn(testGame);
        Mockito.when(gameRepository.findAll()).thenReturn(List.of(testGame));

    }

    @Test
    void createGame_success() {
        Game createdGame = gameService.createGame();
        Mockito.verify(gameRepository, Mockito.times(1)).save(Mockito.any());

        assertEquals(testGame.getGameId(), createdGame.getGameId());
        assertEquals(testGame.getGamePin(), createdGame.getGamePin());
        assertEquals(testGame.getHostId(), createdGame.getHostId());
        assertEquals(GameStatus.LOBBY, createdGame.getStatus());
        assertEquals(testGame.getPlayerGroup(), createdGame.getPlayerGroup());
    }

    @Test
    void getGames_success() {
        List<Game> allGames = gameService.getGames();
        assertEquals(allGames, List.of(testGame));
    }

    @Test
    void getGameByPin_success() {
        Game foundGame = gameService.getGameByPin(testGame.getGamePin());

        assertEquals(testGame.getGameId(), foundGame.getGameId());
        assertEquals(testGame.getGamePin(), foundGame.getGamePin());
        assertEquals(testGame.getHostId(), foundGame.getHostId());
        assertEquals(GameStatus.LOBBY, foundGame.getStatus());
        assertEquals(testGame.getPlayerGroup(), foundGame.getPlayerGroup());
    }

    @Test
    void getGameByPin_failure() {
        Mockito.when(gameRepository.findByGamePin("invalidPin")).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> gameService.getGameByPin("invalidPin"));
    }


    @Test
    void changeGameStatus_ToSelection_success() {
        Player testHost = new Player();
        testHost.setPlayerId(testGame.getHostId());
        testHost.setToken("1");

        Mockito.when(playerRepository.findByToken(Mockito.anyString())).thenReturn(testHost);

        Game updatedGame = gameService.changeGameStatus(GameStatus.SELECTION, testGame.getGamePin(), testHost.getToken());

        assertEquals(GameStatus.SELECTION, updatedGame.getStatus());
    }

    @Test
    void changeGameStatus_ToSelection_notEnoughPlayers() {
        testGame.setPlayerGroup(List.of(testPlayer));

        Player testHost = new Player();
        testHost.setPlayerId(testGame.getHostId());
        testHost.setToken("1");

        assertThrows(ResponseStatusException.class, () -> gameService.changeGameStatus(GameStatus.SELECTION, testGame.getGamePin(), testHost.getToken()));
    }

    @Test
    void changeGameStatus_ToLobby_success() {
        Player testHost = new Player();
        testHost.setPlayerId(testGame.getHostId());
        testHost.setToken("1");
        testHost.setScore(100);
        testPlayer.setScore(100);
        testGame.setPlayerGroup(List.of(testHost, testPlayer));

        Mockito.when(playerRepository.findByToken(Mockito.anyString())).thenReturn(testHost);

        Game updatedGame = gameService.changeGameStatus(GameStatus.LOBBY, testGame.getGamePin(), testHost.getToken());

        assertEquals(GameStatus.LOBBY, updatedGame.getStatus());
        assertEquals(0, testHost.getScore());
        assertEquals(0, testPlayer.getScore());
    }

    @Test
    void changeGameStatus_invalidStatus() {
        Player testHost = new Player();
        testHost.setPlayerId(testGame.getHostId());
        testHost.setToken("1");

        Mockito.when(playerRepository.findByToken(Mockito.anyString())).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> gameService.changeGameStatus(GameStatus.PROMPT, testGame.getGamePin(), testHost.getToken()));
        assertThrows(ResponseStatusException.class, () -> gameService.changeGameStatus(GameStatus.QUIZ, testGame.getGamePin(), testHost.getToken()));
        assertThrows(ResponseStatusException.class, () -> gameService.changeGameStatus(GameStatus.END, testGame.getGamePin(), testHost.getToken()));
    }

    @Test
    void changeGameStatus_invalidToken() {
        Player testHost = new Player();
        testHost.setPlayerId(testGame.getHostId());
        testHost.setToken("1");

        Mockito.when(playerRepository.findByToken(Mockito.anyString())).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> gameService.changeGameStatus(GameStatus.SELECTION, testGame.getGamePin(), testHost.getToken()));
    }

    @Test
    void changeGameStatus_notHost() {
        Player testHost = new Player();
        testHost.setPlayerId(testGame.getHostId());
        testHost.setToken("1");

        Player notHost = new Player();
        notHost.setPlayerId(3L);

        Mockito.when(playerRepository.findByToken(Mockito.anyString())).thenReturn(notHost);

        assertThrows(ResponseStatusException.class, () -> gameService.changeGameStatus(GameStatus.SELECTION, testGame.getGamePin(), testHost.getToken()));
    }

    @Test
    void deleteGameByPin_success() {
        Player testHost = new Player();
        testHost.setPlayerId(testGame.getHostId());
        testHost.setToken("1");

        Mockito.when(playerRepository.findByToken(Mockito.anyString())).thenReturn(testHost);

        gameService.deleteGameByPin(testGame.getGamePin(), testHost.getToken());
        //needs assert
        assertEquals(testGame, gameService.deleteGameByPin(testGame.getGamePin(), testHost.getToken()));
        }

    @Test
    void deleteGameByPin_invalidToken() {
        Player testHost = new Player();
        testHost.setPlayerId(testGame.getHostId());
        testHost.setToken("1");

        Mockito.when(playerRepository.findByToken(Mockito.anyString())).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> gameService.deleteGameByPin(testGame.getGamePin(), testHost.getToken()));
    }

    @Test
    void deleteGameByPin_notHost() {
        Player testHost = new Player();
        testHost.setPlayerId(testGame.getHostId());
        testHost.setToken("1");

        Player notHost = new Player();
        notHost.setPlayerId(3L);

        Mockito.when(playerRepository.findByToken(Mockito.anyString())).thenReturn(notHost);

        assertThrows(ResponseStatusException.class, () -> gameService.deleteGameByPin(testGame.getGamePin(), testHost.getToken()));
    }

    @Test
    void changeToNextQuestion_success_endGame() {
        testGame.setStatus(GameStatus.QUIZ);
        List<QuizQuestion> aQuestion = new ArrayList<>();
        aQuestion.add(new QuizQuestion());
        testGame.setQuizQuestionSet(aQuestion);
        testGame.setCurrentQuestion(null);

        Game returnedGame = gameService.changeToNextQuestion(testGame.getGamePin(), testPlayer.getToken());
        assertEquals( GameStatus.QUIZ, returnedGame.getStatus());

        returnedGame = gameService.changeToNextQuestion(testGame.getGamePin(), testPlayer.getToken());
        assertEquals(GameStatus.END, returnedGame.getStatus());
    }

    @Test
    void changeToNextQuestion_notHost() {
        Player testHost = new Player();
        testHost.setPlayerId(testGame.getHostId());
        testHost.setToken("1");

        Player notHost = new Player();
        notHost.setPlayerId(3L);

        Mockito.when(playerRepository.findByToken(Mockito.anyString())).thenReturn(notHost);

        assertThrows(ResponseStatusException.class, () -> gameService.changeToNextQuestion(testGame.getGamePin(), testHost.getToken()));
    }

    @Test
    void game_nextQuestion() {
        Assertions.assertTrue(testGame.getQuizQuestionSet().isEmpty());
        Assertions.assertNull(testGame.getCurrentQuestion());

        //empty question set
        QuizQuestion nextQuestion = testGame.nextQuestion();
        Assertions.assertNull(nextQuestion);
        QuizQuestion currentQuestion = testGame.getCurrentQuestion();
        Assertions.assertNull(currentQuestion);

        QuizQuestion question1 = new QuizQuestion();
        QuizQuestion question2 = new QuizQuestion();

        List<QuizQuestion> questions = new ArrayList<>(List.of(question1, question2));
        testGame.addQuizQuestions(questions);

        //first question
        nextQuestion = testGame.nextQuestion();
        Assertions.assertEquals(question1, nextQuestion);
        currentQuestion = testGame.getCurrentQuestion();
        Assertions.assertEquals(question1, currentQuestion);

        //next question
        nextQuestion = testGame.nextQuestion();
        Assertions.assertEquals(question2, nextQuestion);
        currentQuestion = testGame.getCurrentQuestion();
        Assertions.assertEquals(question2, currentQuestion);

        //last question
        nextQuestion = testGame.nextQuestion();
        Assertions.assertNull(nextQuestion);
        currentQuestion = testGame.getCurrentQuestion();
        Assertions.assertNull(currentQuestion);
    }

}


