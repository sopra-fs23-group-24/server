package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

/**
 * Test class for the UserResource REST resource.
 *
 * @see PlayerService
 */
@WebAppConfiguration
@SpringBootTest
public class PlayerServiceIntegrationTest {
    @Qualifier("playerRepository")
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private GameService gameService;

    @BeforeEach
    public void setup() {
        playerRepository.deleteAll();
    }

    //TODO: canno test because again relies on gameService
    /*@Test
    public void createPlayerAndAddToGame_success() {
        Game testGame = new Game();
        testGame.setGameId(1L);
        testGame.setGamePin("123456");


        Player testPlayer = new Player();
        testPlayer.setPlayerName("test");
        testPlayer.setHost(true);
        testPlayer.setAssociatedGamePin("123456");

        // given
        assertEquals(new ArrayList<Player>(),playerRepository.findAll());

        given(gameService.getGameByPin(Mockito.anyString())).willReturn(testGame);
        // when
        Player createdPlayer = playerService.createPlayerAndAddToGame(testPlayer);

        // then
        assertNotNull(createdPlayer.getPlayerId());
        assertNotNull(createdPlayer.getToken());
        assertEquals(createdPlayer.getPlayerName(), testPlayer.getPlayerName());
        assertEquals(createdPlayer.getScore(), testPlayer.getScore());
        assertTrue(gameService.checkIfHost(gameService.getGameByPin(createdPlayer.getAssociatedGamePin()), createdPlayer.getPlayerId()));

        assertNotNull(playerRepository.findById(createdPlayer.getPlayerId()));
    }*/
}