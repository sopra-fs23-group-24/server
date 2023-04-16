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

    private GameService gameService;

    @Autowired
    public PlayerService(@Qualifier("playerRepository") PlayerRepository userRepository) {
        this.playerRepository = userRepository;
    }

    @Autowired
    private void setGameService(GameService gameService) {
        this.gameService = gameService;
    }


    //TODO: test Integration?
    public List<Player> getPlayers() {
        return this.playerRepository.findAll();
    }

    //TODO: test Integration?
    public List<Player> getPlayersWithPin(String gamePin) {
        List<Player> players = playerRepository.findAllByAssociatedGamePin(gamePin);
        if (players.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No players found for this pin.");
        }
        return players;
    }

    //TODO: test Integration?
    //TODO: test Service
    public Player createPlayerAndAddToGame(Player newPlayer) {

        newPlayer.setToken(UUID.randomUUID().toString());

        newPlayer = playerRepository.save(newPlayer);
        playerRepository.flush();

        Game joinedGame = gameService.addPlayerToGame(newPlayer);

        log.debug("Created Information for Player: {}", newPlayer);
        log.debug("Added to Game: {}", joinedGame);

        return newPlayer;
    }

    //TODO: test Integration?
    public Player changePlayerUsername(Player newPlayerInfo, String loggedInPlayerToken) {
        Player playerById = playerRepository.findByPlayerId(newPlayerInfo.getPlayerId());
        Player loggedInPlayer = playerRepository.findByToken(loggedInPlayerToken);

        if (playerById == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No player with this id found.");
        }
        if (playerById != loggedInPlayer) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorised to do this action.");
        }
        if (newPlayerInfo.getPlayerName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Empty username.");
        }

        playerById.setPlayerName(newPlayerInfo.getPlayerName());
        playerById = playerRepository.save(playerById);
        playerRepository.flush();

        return playerById;
    }

    //TODO: test Integration?
    //TODO: test Service
    public Player deletePlayer(long playerToBeDeletedId, String loggedInPlayerToken, String gamePin) {
        Player loggedInPlayer = getByToken(loggedInPlayerToken);
        Player playerToDelete = getById(playerToBeDeletedId);

        if (gameService.checkIfHost(gameService.getGameByPin(gamePin), playerToDelete.getPlayerId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "May not delete host.");
        }

        if (playerToBeDeletedId != loggedInPlayer.getPlayerId() && !gameService.checkIfHost(gameService.getGameByPin(gamePin), loggedInPlayer.getPlayerId())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorised to do this action.");
        }


        Game gameByPin = gameService.getGameByPin(playerToDelete.getAssociatedGamePin());
        gameByPin.removePlayer(playerToDelete);

        playerRepository.delete(playerToDelete);
        playerRepository.flush();

        return playerToDelete;
    }

    //TODO: test Integration?
    //TODO: test Service
    public List<Player> deleteAllPlayersByGamePin(String gamePin) {
        List<Player> allPlayersToDelete = playerRepository.findAllByAssociatedGamePin(gamePin);
        for (Player player : allPlayersToDelete) {
            Game gameByPin = gameService.getGameByPin(gamePin);
            gameByPin.removePlayer(player);

            playerRepository.delete(player);
            playerRepository.flush();
        }

        return allPlayersToDelete;
    }

    /**
     * Helper functions
     */

    // TODO: use this method when a getById call is needed - instead of using individual calls - if possible.
    public Player getById(long id) {
        Optional<Player> player = playerRepository.findById(id);
        if (player.isPresent()) {
            return player.get();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No player with this id found.");
    }

    public Player getByToken(String token) {
        Player player = playerRepository.findByToken(token);
        if (player == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No player with this token found.");
        }
        return player;
    }
}
