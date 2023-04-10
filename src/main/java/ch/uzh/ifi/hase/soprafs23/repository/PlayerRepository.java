package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("playerRepository")
public interface PlayerRepository extends JpaRepository<Player, Long> {

    //TODO: test
  Player findByPlayerId(Long id);
  Player findByToken(String token);

  List<Player> findAllByAssociatedGamePin(String pin);
}
