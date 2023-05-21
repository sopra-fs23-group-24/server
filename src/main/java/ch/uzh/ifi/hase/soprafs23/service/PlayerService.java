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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class PlayerService {

    private final Logger log = LoggerFactory.getLogger(PlayerService.class);

    private final PlayerRepository playerRepository;

    private final GameRepository gameRepository;

    @Autowired
    public PlayerService(@Qualifier("playerRepository") PlayerRepository playerRepository, @Qualifier("gameRepository") GameRepository gameRepository) {
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
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

    public Player createPlayerAndAddToGame(Player newPlayer) {

        newPlayer.setToken(UUID.randomUUID().toString());
        Player playerByUsernameAndPin = playerRepository.findByPlayerNameAndAssociatedGamePin(newPlayer.getPlayerName(), newPlayer.getAssociatedGamePin());

        if (playerByUsernameAndPin != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "There already is a player with this username in the game.");
        }

        newPlayer = playerRepository.save(newPlayer);
        playerRepository.flush();

        Game joinedGame = addPlayerToGame(newPlayer);

        log.debug("Created Information for Player: {}", newPlayer);
        log.debug("Added to Game: {}", joinedGame);

        return newPlayer;
    }

    private Game addPlayerToGame(Player newPlayer) {

        Game joinedGame = gameRepository.findByGamePin(newPlayer.getAssociatedGamePin());
        if (joinedGame == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No game with this pin found.");
        }

        if (joinedGame.getStatus() != GameStatus.LOBBY) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Game is already running. Try again with a different pin or join later.");
        }

        joinedGame.addPlayer(newPlayer);
        if (newPlayer.isHost()) {
            if (joinedGame.getHostId() != null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Game already has a host. Please try to join instead.");
            }
            joinedGame.setHostId(newPlayer.getPlayerId());
        }

        joinedGame = gameRepository.save(joinedGame);
        gameRepository.flush();

        log.debug("Added to game: {}", joinedGame);
        return joinedGame;
    }

    public Player changePlayerUsername(Player newPlayerInfo, String loggedInPlayerToken) {
        Player playerById = playerRepository.findByPlayerId(newPlayerInfo.getPlayerId());
        Player loggedInPlayer = playerRepository.findByToken(loggedInPlayerToken);

        if (playerById == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No player with this id found.");
        }
        if (playerById != loggedInPlayer) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorised to do this action.");
        }

        Player playerByUsernameAndPin = playerRepository.findByPlayerNameAndAssociatedGamePin(newPlayerInfo.getPlayerName(), newPlayerInfo.getAssociatedGamePin());
        if (newPlayerInfo.getPlayerName().isBlank() || playerByUsernameAndPin != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid username, might be empty or already in use.");
        }

        playerById.setPlayerName(newPlayerInfo.getPlayerName());
        playerById = playerRepository.save(playerById);
        playerRepository.flush();

        return playerById;
    }

    public Player deletePlayer(long playerToBeDeletedId, String loggedInPlayerToken, String gamePin) {
        Player loggedInPlayer = getByToken(loggedInPlayerToken);
        Player playerToDelete = getById(playerToBeDeletedId);

        if (checkIfHost(gamePin, playerToBeDeletedId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "May not delete host.");
        }

        if (playerToBeDeletedId != loggedInPlayer.getPlayerId() && !checkIfHost(gamePin, loggedInPlayer.getPlayerId())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorised to do this action.");
        }


        Game gameByPin = gameRepository.findByGamePin(playerToDelete.getAssociatedGamePin());
        gameByPin.removePlayer(playerToDelete);

        playerRepository.delete(playerToDelete);
        playerRepository.flush();

        return playerToDelete;
    }





    private Player getById(long id) {
        Optional<Player> player = playerRepository.findById(id);
        if (player.isPresent()) {
            return player.get();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No player with this id found.");
    }

    private Player getByToken(String token) {
        Player player = playerRepository.findByToken(token);
        if (player == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No player with this token found.");
        }
        return player;
    }

    private boolean checkIfHost(String gamePin, long userId) {
        return gameRepository.findByGamePin((gamePin)).getHostId() == userId;
    }

    public List<Player> sortPlayersByScore(List<Player> players) {
        players.sort(Comparator.comparingInt(Player::getScore).reversed()); // e.g. 20, 10, 0.
        return players;
    }
}
