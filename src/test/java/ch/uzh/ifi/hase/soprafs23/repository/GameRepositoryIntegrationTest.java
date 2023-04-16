package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class GameRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GameRepository gameRepository;

    @Test
    public void findByGamePin_success() {
        // given
        Game game = new Game();
        game.setGamePin("123456");
        game.setStatus(GameStatus.LOBBY);
        game.setHostId(1L);

        entityManager.persist(game);
        entityManager.flush();

        // when
        Game found = gameRepository.findByGamePin(game.getGamePin());

        // then
        assertNotNull(found.getGameId());
        assertEquals(game.getGamePin(), found.getGamePin());
        assertEquals(game.getStatus(), found.getStatus());
        assertEquals(game.getHostId(), found.getHostId());
        assertEquals(new ArrayList<Player>(), found.getPlayerGroup());
    }

    @Test
    public void deleteByGamePin_success() {
        // given
        Game game = new Game();
        game.setGamePin("123456");
        game.setStatus(GameStatus.LOBBY);
        game.setHostId(1L);

        entityManager.persist(game);
        entityManager.flush();

        // when
        Game found = gameRepository.findByGamePin(game.getGamePin());

        // then
        assertNotNull(found);

        gameRepository.deleteByGamePin(game.getGamePin());

        assertNull(gameRepository.findByGamePin(game.getGamePin()));
    }

}