package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

public class GameServiceTest {
    private Game testGame;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private GameService gameService;



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
    public void getGameByPin_success(){
        Game foundGame = gameService.getGameByPin(testGame.getGamePin());

        assertEquals(testGame.getGameId(), foundGame.getGameId());
        assertEquals(testGame.getGamePin(), foundGame.getGamePin());
        assertEquals(testGame.getHostId(), foundGame.getHostId());
        assertEquals(GameStatus.LOBBY, foundGame.getStatus());
        assertEquals(testGame.getPlayerGroup(), foundGame.getPlayerGroup());
    }

    @Test
    public void getGameByPin_failure(){
        Mockito.when(gameRepository.findByGamePin("invalidPin")).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> gameService.getGameByPin("invalidPin"));
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
}


