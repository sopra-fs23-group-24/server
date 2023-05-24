package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs23.service.prompt.PromptAnswerService;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

@WebAppConfiguration
@SpringBootTest
public class PlayerServiceIntegrationTest {
    Game testGame;
    Player testPlayer;

    @Qualifier("playerRepository")
    @Autowired
    private PlayerRepository playerRepository;

    @Qualifier("gameRepository")
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PlayerService playerService;

    @BeforeEach
    public void setup() {
        playerRepository.deleteAll();
        gameRepository.deleteAll();

        testGame = new Game();
        testGame.setStatus(GameStatus.LOBBY);
        testGame.setGamePin("123456");
        testGame = gameRepository.save(testGame);
        gameRepository.flush();

        testPlayer = new Player();
        testPlayer.setPlayerName("testPlayer");
        testPlayer.setAssociatedGamePin(testGame.getGamePin());
        testPlayer.setHost(false);
    }


    @Test
    public void createPlayerAndAddToGame(){
        Game foundGame = gameRepository.findByGamePin(testGame.getGamePin());
        Assertions.assertNotNull(foundGame);

        Player createdPlayer = playerService.createPlayerAndAddToGame(testPlayer);
        Assertions.assertEquals(testPlayer.getPlayerName(), createdPlayer.getPlayerName());
        Assertions.assertNotNull(createdPlayer.getPlayerId());
        Assertions.assertNotNull(createdPlayer.getToken());
        Assertions.assertEquals(createdPlayer.getScore(), 0);
        Assertions.assertEquals(createdPlayer.getLatestScore(), 0);
    }

    @Test
    public void deletePlayer(){
        testPlayer.setToken("token");
        testPlayer.setScore(0);
        testPlayer.setLatestScore(0);
        testPlayer = playerRepository.save(testPlayer);
        playerRepository.flush();

        testGame.setHostId(100L);
        gameRepository.save(testGame);
        gameRepository.flush();

        Player foundPlayer = playerRepository.findByPlayerId(testPlayer.getPlayerId());
        Assertions.assertNotNull(foundPlayer);

        playerService.deletePlayer(testPlayer.getPlayerId(), testPlayer.getToken(), testPlayer.getAssociatedGamePin());

        foundPlayer = playerRepository.findByPlayerId(testPlayer.getPlayerId());
        Assertions.assertNull(foundPlayer);
    }

}