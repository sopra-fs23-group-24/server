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
public class PlayerRepositoryIntegrationTest {
    Player testPlayer;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setup() {
        testPlayer = new Player();
        testPlayer.setPlayerName("test");
        testPlayer.setAssociatedGamePin("123456");
        testPlayer.setHost(true);
        testPlayer.setToken("1");
        testPlayer.setPlayerId(1L);

        entityManager.merge(testPlayer);
        entityManager.flush();
    }

    @AfterEach
    void emptyRepository() {
        playerRepository.deleteAll();
    }

    /*@Test
    public void findByPlayerId_success() {
        //due to OneToMany relationship Game -> Players, Players are detached entities
        //use merge instead of save


        // when
        Player found = playerRepository.findByPlayerId(testPlayer.getPlayerId());

        assertEquals(testPlayer.getPlayerName(), found.getPlayerName());
        assertEquals(testPlayer.getScore(), found.getScore());
        assertEquals(testPlayer.getPlayerId(), found.getPlayerId());
        assertEquals(testPlayer.getToken(), found.getToken());
        assertEquals(testPlayer.getAssociatedGamePin(), found.getAssociatedGamePin());
    }*/


    @Test
    public void findByToken_success() {
        //due to OneToMany relationship Game -> Players, Players are detached entities
        //use merge instead of save
        //entityManager.merge(testPlayer);
        //entityManager.flush();

        // when
        Player found = playerRepository.findByToken(testPlayer.getToken());

        assertEquals(testPlayer.getPlayerName(), found.getPlayerName());
        assertEquals(testPlayer.getToken(), found.getToken());
        assertEquals(testPlayer.getAssociatedGamePin(), found.getAssociatedGamePin());
    }

    @Test
    public void findAllByAssociatedGamePin_success() {
        //entityManager.merge(testPlayer);
        //entityManager.flush();

        // when
        List<Player> foundPlayers = playerRepository.findAllByAssociatedGamePin(testPlayer.getAssociatedGamePin());

        assertEquals(foundPlayers.size(), 1);
        assertEquals(testPlayer.getPlayerName(), foundPlayers.get(0).getPlayerName());
        assertEquals(testPlayer.getToken(), foundPlayers.get(0).getToken());
        assertEquals(testPlayer.getAssociatedGamePin(), foundPlayers.get(0).getAssociatedGamePin());
    }

    @Test
    public void findByPlayerNameAndAssociatedGamePin(){
        //todo: test
    }


}