package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PlayerGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PlayerPutDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to
 * the user.
 * The controller will receive the request and delegate the execution to the
 * UserService and finally return the result.
 */
@RestController
public class PlayerController {
    //headers: @RequestHeader("playerId") long playerId, @RequestHeader("gamePin") String gamePin
    /*
      System.out.println("Received PlayerId: " + playerId);
      System.out.println("Received GamePin: " + gamePin);
    */
  private final PlayerService playerService;

  PlayerController(PlayerService playerService) {
    this.playerService = playerService;
  }

  @GetMapping("/allPlayers")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<PlayerGetDTO> getAllUsers() {
      List<Player> players = playerService.getPlayers();
      List<PlayerGetDTO> playerGetDTOs = new ArrayList<>();

      for (Player player : players) {
          playerGetDTOs.add(DTOMapper.INSTANCE.convertToPlayerGetDTO(player));
      }
      return playerGetDTOs;
  }

  @GetMapping("/players")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<PlayerGetDTO> getAllUsersOfGame(@RequestHeader("gamePin") String gamePin) {
    List<Player> players = playerService.getPlayersWithPin(gamePin);
    List<PlayerGetDTO> playerGetDTOs = new ArrayList<>();

    for (Player player : players) {
      playerGetDTOs.add(DTOMapper.INSTANCE.convertToPlayerGetDTO(player));
    }
    return playerGetDTOs;
  }

  @PutMapping("/players/{id}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public PlayerGetDTO changeUsername(@RequestBody PlayerPutDTO changes, @PathVariable("id") long playerId){
    Player updatedPlayerInfo = DTOMapper.INSTANCE.convertFromPlayerPutDTO(changes);

    Player updatedPlayer = playerService.changePlayerUsername(updatedPlayerInfo.getPlayerName(), playerId);

    return DTOMapper.INSTANCE.convertToPlayerGetDTO(updatedPlayer);
  }

}
