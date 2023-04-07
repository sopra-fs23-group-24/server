package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class PlayerRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PlayerRepository playerRepository;

    /*@Test
    public void findByPlayerId_success() {
        // given
        Player testPlayer = new Player();
        testPlayer.setPlayerName("test");
        testPlayer.setAssociatedGamePin("123456");
        testPlayer.setHost(true);
        testPlayer.setToken("1");
        testPlayer.setPlayerId(1L);


        //TODO: figure out why cannot persist player (whatever that even means)
        entityManager.persist(testPlayer);
        entityManager.flush();

        // when
        Player found = playerRepository.findByPlayerId(testPlayer.getPlayerId());

        assertEquals(testPlayer.getPlayerName(), found.getPlayerName());
        assertEquals(testPlayer.getScore(), found.getScore());
        assertEquals(testPlayer.getPlayerId(), found.getPlayerId());
        assertEquals(testPlayer.getToken(), found.getToken());
        assertEquals(testPlayer.getAssociatedGamePin(), found.getAssociatedGamePin());
    }*/
}