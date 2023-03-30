package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
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
public class PlayerService {

    private final Logger log = LoggerFactory.getLogger(PlayerService.class);

    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerService(@Qualifier("playerRepository") PlayerRepository userRepository) {
        this.playerRepository = userRepository;
    }

    public List<Player> getPlayers() {
        return this.playerRepository.findAll();
    }

    public List<Player> getPlayersWithPin(String gamePin) {
        List<Player> players = playerRepository.findAllByAssociatedGamePin(gamePin);
        if (players.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No players found for this pin.");
        }
        return players;
    }

    // TODO: use this method when a getById call is needed - instead of using individual calls - if possible.
    public Player getById(long id) {
        Optional<Player> player = playerRepository.findById(id);
        if (player.isPresent()) {
            return player.get();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No player with this id found.");
    }

    public Player createUser(String aGamePin) {
        Player newPlayer = new Player();
        newPlayer.setToken(UUID.randomUUID().toString());
        newPlayer.setAssociatedGamePin(aGamePin);
        //newUser.setStatus(UserStatus.OFFLINE);
        //checkIfUserExists(newUser);
        // saves the given entity but data is only persisted in the database once
        // flush() is called
        newPlayer = playerRepository.save(newPlayer);
        playerRepository.flush();

        log.debug("Created Information for User: {}", newPlayer);
        return newPlayer;
    }

    public Player changePlayerUsername(String username, long playerId) {
        Player playerById = playerRepository.findByPlayerId(playerId);
        if (playerById == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No player with this id found.");
        }
        else if (username.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Empty username.");
        }
        playerById.setPlayerName(username);
        playerById = playerRepository.save(playerById);
        playerRepository.flush();

        return playerById;
    }

    public void deletePlayer(long playerId) {
        Player playerToDelete = getById(playerId);
        playerRepository.delete(playerToDelete);
        playerRepository.flush();
    }
}
