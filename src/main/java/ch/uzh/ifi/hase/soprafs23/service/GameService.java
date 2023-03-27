package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class GameService {

    private final Logger log = LoggerFactory.getLogger(GameService.class);

    private final GameRepository gameRepository;

    @Autowired
    private PlayerService playerService;

    @Autowired
    public GameService(@Qualifier("gameRepository") GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public List<Game> getGames() {
        return this.gameRepository.findAll();
    }

    @Transactional
    public Game createGame() {
        Game newGame = new Game();
        newGame.setStatus(GameStatus.LOBBY);

        // generate and check pin
        boolean noPin = true;
        while (noPin) {
            String pin = newGame.generateGamePin();
            if(gameRepository.findByGamePin(pin) == null) {
                newGame.setGamePin(pin);
                noPin = false;
            }
        }


        Player host = playerService.createUser(newGame.getGamePin());
        newGame.setHostId(host.getPlayerId());
        newGame.addPlayer(host);

        newGame = gameRepository.save(newGame);
        gameRepository.flush();

        log.debug("Created Information for User: {}", newGame);
        return newGame;
    }

}
