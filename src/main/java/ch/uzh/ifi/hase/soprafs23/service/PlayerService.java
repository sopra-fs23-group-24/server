package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
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
public class PlayerService {

  private final Logger log = LoggerFactory.getLogger(PlayerService.class);

  private final PlayerRepository userRepository;

  @Autowired
  public PlayerService(@Qualifier("playerRepository") PlayerRepository userRepository) {
    this.userRepository = userRepository;
  }

  public List<Player> getPlayers() {
    return this.userRepository.findAll();
  }

  public Player createUser(String aGamePin) {
    System.out.println("started creating host");
    Player newPlayer = new Player();
    newPlayer.setToken(UUID.randomUUID().toString());
    newPlayer.setAssociatedGamePin(aGamePin);
    System.out.println("trying to set game");
    //newUser.setStatus(UserStatus.OFFLINE);
    //checkIfUserExists(newUser);
    // saves the given entity but data is only persisted in the database once
    // flush() is called
    newPlayer = userRepository.save(newPlayer);
    userRepository.flush();

    log.debug("Created Information for User: {}", newPlayer);
    return newPlayer;
  }

}
