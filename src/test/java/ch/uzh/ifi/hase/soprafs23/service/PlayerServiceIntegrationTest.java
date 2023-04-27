package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

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


    @BeforeEach
    public void setup() {
        playerRepository.deleteAll();
    }

    //TODO: cannot test because again relies on gameService

}