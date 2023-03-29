package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.GameJoinDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PlayerGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class GameController {
    //headers: @RequestHeader("playerId") long playerId, @RequestHeader("gamePin") String gamePin
    /*
      System.out.println("Received PlayerId: " + playerId);
      System.out.println("Received GamePin: " + gamePin);
    */
    private final GameService gameService;

    GameController(GameService gameService) {
        this.gameService = gameService;
    }

  @GetMapping("/allGames")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<GameGetDTO> getAllGames() {
    List<Game> allGames = gameService.getGames();

    List<GameGetDTO> gamesGetDTOs = new ArrayList<>();

    for (Game game : allGames) {
      gamesGetDTOs.add(DTOMapper.INSTANCE.convertToGameGetDTO(game));
    }

    return gamesGetDTOs;
  }


    @PostMapping("/host")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public PlayerGetDTO hostNewGame() {
        Player newHost = gameService.createGameAndReturnHost();
        //convert to ...
        return DTOMapper.INSTANCE.convertToPlayerGetDTO(newHost);
    }

  @PostMapping("/join")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public PlayerGetDTO joinGame(@RequestBody GameJoinDTO gameWithPin) {
      Player newPlayer = gameService.joinGameAndReturnUser(gameWithPin.getGamePin());

    //convert to ...
      return DTOMapper.INSTANCE.convertToPlayerGetDTO(newPlayer);

  }

}
