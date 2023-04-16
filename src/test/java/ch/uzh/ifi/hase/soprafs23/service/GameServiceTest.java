package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.constant.PromptType;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.Prompt;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {
    private Game testGame;

    private Player testPlayer;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private GameService gameService;

    /*@Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private PlayerService playerService;*/

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        testGame = new Game();
        testGame.setGameId(1L);
        testGame.setGamePin("123456");
        testGame.setHostId(2L); //bc cannot be 1 bc game is 1 already

        Mockito.when(gameRepository.save(Mockito.any())).thenReturn(testGame);
        Mockito.when(gameRepository.findByGamePin(testGame.getGamePin())).thenReturn(testGame);
        Mockito.when(gameRepository.findAll()).thenReturn(List.of(testGame));

        testPlayer = new Player();
        testPlayer.setPlayerId(2L);
        testPlayer.setAssociatedGamePin("123456");
        testPlayer.setPlayerName("test");
        testPlayer.setToken("1");
    }

    @Test
    public void createGame_success() {

        Game createdGame = gameService.createGame();
        Mockito.verify(gameRepository, Mockito.times(1)).save(Mockito.any());

        //test host properties
        assertEquals(testGame.getGameId(), createdGame.getGameId());
        assertEquals(testGame.getGamePin(), createdGame.getGamePin());
        assertEquals(testGame.getHostId(), createdGame.getHostId());
        assertEquals(GameStatus.LOBBY, createdGame.getStatus());
        assertEquals(testGame.getPlayerGroup(), createdGame.getPlayerGroup());
    }

    @Test
    public void getGames_success() {
        List<Game> allGames = gameService.getGames();
        assertEquals(allGames, List.of(testGame));
    }

    @Test
    public void getGameByPin_success() {
        Game foundGame = gameService.getGameByPin(testGame.getGamePin());

        assertEquals(testGame.getGameId(), foundGame.getGameId());
        assertEquals(testGame.getGamePin(), foundGame.getGamePin());
        assertEquals(testGame.getHostId(), foundGame.getHostId());
        assertEquals(GameStatus.LOBBY, foundGame.getStatus());
        assertEquals(testGame.getPlayerGroup(), foundGame.getPlayerGroup());
    }

    @Test
    public void getGameByPin_failure() {
        Mockito.when(gameRepository.findByGamePin("invalidPin")).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> gameService.getGameByPin("invalidPin"));
    }

    @Test
    public void addPlayerToGame_success() {
        Mockito.when(gameRepository.findByGamePin(testPlayer.getAssociatedGamePin())).thenReturn(testGame);

        assertEquals(gameService.getGameByPin(testGame.getGamePin()).getPlayerGroup(), new ArrayList<Player>());

        testGame.setStatus(GameStatus.LOBBY);
        testGame.setHostId(null);

        Game gameWithPlayer = gameService.addPlayerToGame(testPlayer);

        Mockito.verify(gameRepository, Mockito.times(1)).save(Mockito.any());
        assertTrue(gameWithPlayer.getPlayerGroup().contains(testPlayer));
    }

    @Test
    public void addPlayerToGame_invalidGamePin() {
        Mockito.when(gameRepository.findByGamePin(testPlayer.getAssociatedGamePin())).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        testGame.setStatus(GameStatus.LOBBY);
        testGame.setHostId(null);

        assertThrows(ResponseStatusException.class, () -> gameService.addPlayerToGame(testPlayer));
    }

    @Test
    public void addPlayerToGame_gameNotInLobbyStage() {
        Mockito.when(gameRepository.findByGamePin(testPlayer.getAssociatedGamePin())).thenReturn(testGame);

        testPlayer.setHost(true);
        testGame.setStatus(GameStatus.SELECTION);

        assertThrows(ResponseStatusException.class, () -> gameService.addPlayerToGame(testPlayer));
    }

    @Test
    public void addPlayerToGame_alreadyHasHost() {
        Mockito.when(gameRepository.findByGamePin(testPlayer.getAssociatedGamePin())).thenReturn(testGame);

        assertNotNull(gameService.getGameByPin(testGame.getGamePin()).getHostId());

        testPlayer.setHost(true);
        testGame.setStatus(GameStatus.LOBBY);

        assertThrows(ResponseStatusException.class, () -> gameService.addPlayerToGame(testPlayer));
    }

    @Test
    public void getPromptsOfGame_success() {
        Prompt testPrompt = new Prompt();
        testPrompt.setPromptNr(999);
        testPrompt.setPromptText("Tell a story");
        testPrompt.setPromptType(PromptType.TRUEFALSE);

        List<Prompt> listOfPrompts = List.of(testPrompt);
        testGame.setPromptSet(listOfPrompts);

        Mockito.when(gameRepository.findByGamePin(testGame.getGamePin())).thenReturn(testGame);

        List<Prompt> foundPrompts = gameService.getPromptsOfGame(testGame.getGamePin());

        assertEquals(foundPrompts, listOfPrompts);
    }

    @Test
    public void getPromptsOfGame_invalidPin() {
        Prompt testPrompt = new Prompt();
        testPrompt.setPromptNr(999);
        testPrompt.setPromptText("Tell a story");
        testPrompt.setPromptType(PromptType.TRUEFALSE);

        List<Prompt> listOfPrompts = List.of(testPrompt);
        testGame.setPromptSet(listOfPrompts);

        Mockito.when(gameRepository.findByGamePin(testGame.getGamePin())).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        assertThrows(ResponseStatusException.class, () -> gameService.getPromptsOfGame(testGame.getGamePin()));
    }

    @Test
    public void addPromptsToGame_success() {
        Prompt testPrompt = new Prompt();
        testPrompt.setPromptNr(999);
        testPrompt.setPromptText("Tell a story");
        testPrompt.setPromptType(PromptType.TRUEFALSE);

        List<Prompt> listOfPrompts = List.of(testPrompt);

        Mockito.when(gameRepository.findByGamePin(testGame.getGamePin())).thenReturn(testGame);

        assertEquals(gameService.getGameByPin(testGame.getGamePin()).getPlayerGroup(), new ArrayList<Player>());

        testGame.setStatus(GameStatus.SELECTION);

        Game gameWithPrompts = gameService.addPromptsToGame(listOfPrompts, testGame.getGamePin());

        Mockito.verify(gameRepository, Mockito.times(1)).save(Mockito.any());
        assertTrue(gameWithPrompts.getPromptSet().contains(testPrompt));
    }

    @Test
    public void addPromptsToGame_invalidGamePin() {
        Prompt testPrompt = new Prompt();
        testPrompt.setPromptNr(999);
        testPrompt.setPromptText("Tell a story");
        testPrompt.setPromptType(PromptType.TRUEFALSE);

        List<Prompt> listOfPrompts = List.of(testPrompt);

        Mockito.when(gameRepository.findByGamePin(testGame.getGamePin())).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        testGame.setStatus(GameStatus.SELECTION);
        testGame.setPromptSet(new ArrayList<>());

        assertThrows(ResponseStatusException.class, () -> gameService.addPromptsToGame(listOfPrompts, testGame.getGamePin()));
    }

    @Test
    public void addPromptsToGame_gameNotInSelectionStage() {
        Prompt testPrompt = new Prompt();
        testPrompt.setPromptNr(999);
        testPrompt.setPromptText("Tell a story");
        testPrompt.setPromptType(PromptType.TRUEFALSE);

        List<Prompt> listOfPrompts = List.of(testPrompt);

        Mockito.when(gameRepository.findByGamePin(testGame.getGamePin())).thenReturn(testGame);

        testGame.setStatus(GameStatus.LOBBY);
        testGame.setPromptSet(new ArrayList<>());

        assertThrows(ResponseStatusException.class, () -> gameService.addPromptsToGame(listOfPrompts, testGame.getGamePin()));
    }

    @Test
    public void addPlayerToGame_alreadyHasPrompts() {
        Prompt testPrompt = new Prompt();
        testPrompt.setPromptNr(999);
        testPrompt.setPromptText("Tell a story");
        testPrompt.setPromptType(PromptType.TRUEFALSE);

        List<Prompt> listOfPrompts = List.of(testPrompt);

        Mockito.when(gameRepository.findByGamePin(testGame.getGamePin())).thenReturn(testGame);

        testGame.setStatus(GameStatus.SELECTION);
        testGame.setPromptSet(listOfPrompts);

        assertFalse(gameService.getGameByPin(testGame.getGamePin()).getPromptSet().isEmpty());

        assertThrows(ResponseStatusException.class, () -> gameService.addPromptsToGame(listOfPrompts, testGame.getGamePin()));
    }

    //TODO: figure out how to do this, fails because cannot connect to playerService/playerRepository properly

    /*@Test
    public void changeGameStatus_success(){
        Player testHost = new Player();
        testHost.setPlayerId(testGame.getHostId());
        testHost.setToken("1");

        Mockito.when(playerRepository.findByToken(Mockito.anyString())).thenReturn(testHost);

        Game updatedGame = gameService.changeGameStatus(GameStatus.SELECTION, testGame.getGamePin(), testHost.getToken());

        assertEquals(GameStatus.SELECTION, updatedGame.getStatus());
    }

    @Test
    public void deleteGameByPin_success(){

    }

    @Test
    public void deleteGameByPin_notHost(){

    }*/

    /**
     * Helper functions tests
     */

    @Test
    public void checkIfHost_true() {
        testGame.setHostId(2L);
        testPlayer.setPlayerId(2L);

        assertTrue(gameService.checkIfHost(testGame, testPlayer.getPlayerId()));
    }

    @Test
    public void checkIfHost_false() {
        testGame.setHostId(3L);
        testPlayer.setPlayerId(2L);

        assertFalse(gameService.checkIfHost(testGame, testPlayer.getPlayerId()));
    }
}


