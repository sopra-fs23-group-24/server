package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class PlayerRepositoryIntegrationTest {
    Player testPlayer;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setup() {
        testPlayer = new Player();
        testPlayer.setPlayerId(1L);
        testPlayer.setAssociatedGamePin("123456");
        testPlayer.setPlayerName("test");
        testPlayer.setToken("1");
        testPlayer.setScore(100);
        testPlayer.setLatestScore(10);
        testPlayer.setHost(true);

        testPlayer = entityManager.merge(testPlayer);
        entityManager.flush();
    }

    @AfterEach
    void emptyRepository() {
        playerRepository.deleteAll();
    }

    @Test
    void findByPlayerId_success() {
        Player found = playerRepository.findByPlayerId(testPlayer.getPlayerId());

        assertEquals(testPlayer.getPlayerId(), found.getPlayerId());
        assertEquals(testPlayer.getAssociatedGamePin(), found.getAssociatedGamePin());
        assertEquals(testPlayer.getPlayerName(), found.getPlayerName());
        assertEquals(testPlayer.getToken(), found.getToken());
        assertEquals(testPlayer.getScore(), found.getScore());
        assertEquals(testPlayer.getLatestScore(), found.getLatestScore());
        assertEquals(testPlayer.isHost(), found.isHost());
    }


    @Test
    void findByToken_success() {
        Player found = playerRepository.findByToken(testPlayer.getToken());

        assertEquals(testPlayer.getPlayerId(), found.getPlayerId());
        assertEquals(testPlayer.getAssociatedGamePin(), found.getAssociatedGamePin());
        assertEquals(testPlayer.getPlayerName(), found.getPlayerName());
        assertEquals(testPlayer.getToken(), found.getToken());
        assertEquals(testPlayer.getScore(), found.getScore());
        assertEquals(testPlayer.getLatestScore(), found.getLatestScore());
        assertEquals(testPlayer.isHost(), found.isHost());
    }

    @Test
    void findAllByAssociatedGamePin_success() {
        List<Player> foundPlayers = playerRepository.findAllByAssociatedGamePin(testPlayer.getAssociatedGamePin());

        assertEquals(1, foundPlayers.size());

        Player found = foundPlayers.get(0);

        assertEquals(testPlayer.getPlayerId(), found.getPlayerId());
        assertEquals(testPlayer.getAssociatedGamePin(), found.getAssociatedGamePin());
        assertEquals(testPlayer.getPlayerName(), found.getPlayerName());
        assertEquals(testPlayer.getToken(), found.getToken());
        assertEquals(testPlayer.getScore(), found.getScore());
        assertEquals(testPlayer.getLatestScore(), found.getLatestScore());
        assertEquals(testPlayer.isHost(), found.isHost());
    }

    @Test
    void findByPlayerNameAndAssociatedGamePin() {
        Player found = playerRepository.findByPlayerNameAndAssociatedGamePin(testPlayer.getPlayerName(), testPlayer.getAssociatedGamePin());

        assertEquals(testPlayer.getPlayerId(), found.getPlayerId());
        assertEquals(testPlayer.getAssociatedGamePin(), found.getAssociatedGamePin());
        assertEquals(testPlayer.getPlayerName(), found.getPlayerName());
        assertEquals(testPlayer.getToken(), found.getToken());
        assertEquals(testPlayer.getScore(), found.getScore());
        assertEquals(testPlayer.getLatestScore(), found.getLatestScore());
        assertEquals(testPlayer.isHost(), found.isHost());
    }


}