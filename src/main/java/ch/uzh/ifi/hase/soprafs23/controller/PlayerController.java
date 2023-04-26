package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PlayerGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PlayerPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PlayerPutDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
public class PlayerController {
    //headers: @RequestHeader("playerToken") String loggedInToken, @PathVariable ("pin") String gamePin
    /*
      System.out.println("Received PlayerToken: " + loggedInToken);
      System.out.println("Received GamePin: " + gamePin);
    */
    private final PlayerService playerService;

    PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping("/games/{pin}/players")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public PlayerGetDTO newPlayerInGame(@RequestBody PlayerPostDTO newPlayerDTO, @PathVariable("pin") String gamePin, HttpServletResponse response) {
        Player newPlayer = DTOMapper.INSTANCE.convertFromPlayerPostDTO(newPlayerDTO);
        newPlayer.setAssociatedGamePin(gamePin);

        newPlayer = playerService.createPlayerAndAddToGame(newPlayer);

        response.addHeader("playerToken", newPlayer.getToken()); //add Token via Header
        response.addHeader("Access-Control-Allow-Headers", "playerToken"); //make Token Header available on frontend
        response.addHeader("Access-Control-Expose-Headers", "playerToken"); //make Token Header available on frontend
        //convert to ...
        return DTOMapper.INSTANCE.convertToPlayerGetDTO(newPlayer);
    }

    @GetMapping("/players")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<PlayerGetDTO> getAllPlayers() {
        List<Player> players = playerService.getPlayers();
        List<PlayerGetDTO> playerGetDTOs = new ArrayList<>();

        for (Player player : players) {
            playerGetDTOs.add(DTOMapper.INSTANCE.convertToPlayerGetDTO(player));
        }
        return playerGetDTOs;
    }

    @GetMapping("/games/{pin}/players")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<PlayerGetDTO> getAllPlayersOfGame(@PathVariable("pin") String gamePin) {
        List<Player> players = playerService.getPlayersWithPin(gamePin);

        // sort players
        List<Player> sortedPlayers = playerService.sortPlayersByScore(players);

        // convert to DTO
        List<PlayerGetDTO> playerGetDTOs = new ArrayList<>();
        for (Player player : sortedPlayers) {
            playerGetDTOs.add(DTOMapper.INSTANCE.convertToPlayerGetDTO(player));
        }
        return playerGetDTOs;
    }


    @PutMapping("/players/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public PlayerGetDTO changeUsername(@RequestBody PlayerPutDTO changes, @PathVariable("id") long playerToBeChangedId, @RequestHeader("playerToken") String loggedInToken) {
        Player updatedPlayerInfo = DTOMapper.INSTANCE.convertFromPlayerPutDTO(changes);
        updatedPlayerInfo.setPlayerId(playerToBeChangedId);

        Player updatedPlayer = playerService.changePlayerUsername(updatedPlayerInfo, loggedInToken);

        return DTOMapper.INSTANCE.convertToPlayerGetDTO(updatedPlayer);
    }

    @DeleteMapping("/games/{pin}/players/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ResponseBody
    public PlayerGetDTO deletePlayer(@PathVariable("id") long playerToBeDeletedId, @RequestHeader("playerToken") String loggedInToken, @PathVariable("pin") String gamePin) {
        Player deletedPlayer = playerService.deletePlayer(playerToBeDeletedId, loggedInToken, gamePin);

        return DTOMapper.INSTANCE.convertToPlayerGetDTO(deletedPlayer);
    }

}
