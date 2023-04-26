package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the UserResource REST resource.
 *
 * @see GameService
 */
@WebAppConfiguration
@SpringBootTest
public class GameServiceIntegrationTest {
    @Qualifier("gameRepository")
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameService gameService;

    @BeforeEach
    public void setup() {
        gameRepository.deleteAll();
    }

    @Test
    public void createGame_success() {
        assertTrue(gameRepository.findAll().isEmpty());

        Game createdGame = gameService.createGame();

        assertNotNull(createdGame.getGameId());
        assertNotNull(createdGame.getGamePin());
        assertEquals(GameStatus.LOBBY, createdGame.getStatus());
        assertNull(createdGame.getHostId());
        assertTrue(createdGame.getPlayerGroup().isEmpty());
        assertTrue(createdGame.getPromptSet().isEmpty());
        assertTrue(createdGame.getQuizQuestionSet().isEmpty());
        assertNull(createdGame.getCurrentQuestion());

        assertNotNull(gameRepository.findByGamePin(createdGame.getGamePin()));
    }


    //TODO: figure out if we can/need to also test rest (getters, update, delete, etc.)

}