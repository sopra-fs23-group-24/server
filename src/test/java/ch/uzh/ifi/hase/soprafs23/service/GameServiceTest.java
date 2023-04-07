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
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {
    private Game testGame;
    private Player testHost;

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
        //testGame.setStatus(GameStatus.LOBBY); // is auto-set probably
        testGame.setHostId(2L); //bc cannot be 1 bc game is 1 already

        testHost = new Player();
        testHost.setAssociatedGamePin("123456");
        testHost.setPlayerId(2L);

        Mockito.when(gameRepository.save(Mockito.any())).thenReturn(testGame);
        Mockito.when(playerRepository.save(Mockito.any())).thenReturn(testHost);

    }

    /*@Test
    public void testCreateGameAndReturnHost() {

        Player host = gameService.createGameAndReturnHost();
        Mockito.verify(gameRepository, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(playerRepository, Mockito.times(1)).save(Mockito.any());

        //test host properties
        assertEquals(testHost.getPlayerId(), host.getPlayerId());
        assertEquals(testHost.getAssociatedGamePin(), host.getAssociatedGamePin());
        assertEquals(0, host.getScore());
        assertNull(host.getPlayerName());
        assertNull(host.getToken());
        }*/
}


