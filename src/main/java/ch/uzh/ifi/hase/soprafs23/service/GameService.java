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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.server.ResponseStatusException;

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

    public Game getGameByPin(String pin){
        Game gameByPin = gameRepository.findByGamePin(pin);
        if(gameByPin == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No game with this pin found.");
        }else{
            return gameByPin;
        }
    }

    public boolean checkIfHost(Game game, long userId){
        if(game.getHostId() != userId){
            return false;
        }
        return true;
    }

    @Transactional
    public Player createGameAndReturnHost() {
        Game newGame = new Game();
        newGame.setStatus(GameStatus.LOBBY);

        // generate and check pin
        boolean noPin = true;
        while (noPin) {
            String pin = newGame.generateGamePin();
            if (gameRepository.findByGamePin(pin) == null) {
                newGame.setGamePin(pin);
                noPin = false;
            }
        }

        Player host = playerService.createUser(newGame.getGamePin());
        newGame.setHostId(host.getPlayerId());
        newGame.addPlayer(host);

        newGame = gameRepository.save(newGame); // TODO: does it need the "newGame = " part?
        gameRepository.flush();

        log.debug("Created game: {}", newGame);
        log.debug("Created host: {}", host);
        return host;
    }

    public Player joinGameAndReturnUser(String gamePin) {

        Game joinedGame = getGameByPin(gamePin);

        Player user = playerService.createUser(joinedGame.getGamePin());
        joinedGame.addPlayer(user);

        joinedGame = gameRepository.save(joinedGame); // TODO: does it need the "newGame = " part?
        gameRepository.flush();

        log.debug("Added to game: {}", joinedGame);
        log.debug("created user: {}", user);
        return user;
    }

    public Game changeGameStatus(GameStatus requestedStatus, String gamePin, long userId){
        System.out.println(requestedStatus);
        //GameStatus newStatus = GameStatus.transformToStatus(requestedStatus);

        Game gameByPin = getGameByPin(gamePin);
        if(!checkIfHost(gameByPin, userId)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorised to do this action.");
        }

        gameByPin.setStatus(requestedStatus);
        gameByPin = gameRepository.save(gameByPin); // TODO: does it need the "newGame = " part?
        gameRepository.flush();

        return gameByPin;
    }


    public void deleteGameByPin(String gamePin, long userId){
        Game gameByPin = getGameByPin(gamePin);
        if(!checkIfHost(gameByPin, userId)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorised to do this action.");
        }

        gameRepository.deleteByGamePin(gamePin);
        playerService.deletePlayersByGamePin(gamePin);
        //TODO: delete questions
        //TODO: delete answers

    }

}
