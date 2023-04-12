package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.Prompt;
import ch.uzh.ifi.hase.soprafs23.rest.dto.*;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.IllegalFormatConversionException;
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
    //headers: @RequestHeader("playerToken") String loggedInToken, @PathVariable ("pin") String gamePin
    /*
      System.out.println("Received PlayerToken: " + loggedInToken);
      System.out.println("Received GamePin: " + gamePin);
    */
    private final GameService gameService;

    GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/games")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public GameGetDTO createNewGame() {
        Game newGame = gameService.createGame();

        return DTOMapper.INSTANCE.convertToGameGetDTO(newGame);
    }

    @GetMapping("/games")
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

    @GetMapping("/games/{pin}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGetDTO getGameByPin (@PathVariable ("pin") String gamePin){
        Game currentGame = gameService.getGameByPin(gamePin);
        return DTOMapper.INSTANCE.convertToGameGetDTO(currentGame);
    }

    @PutMapping("/games/{pin}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGetDTO updateGameStatus (@RequestBody GamePutDTO newStatus, @RequestHeader("playerToken") String loggedInToken, @PathVariable ("pin") String gamePin){
        //TODO: error catching doesn't work yet
        Game newStatusGame = DTOMapper.INSTANCE.convertFromGamePutDTO(newStatus);
        Game updatedGame = gameService.changeGameStatus(newStatusGame.getStatus(), gamePin, loggedInToken);
        return DTOMapper.INSTANCE.convertToGameGetDTO(updatedGame);
    }

    @GetMapping("/games/{pin}/prompts")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<PromptGetDTO> getAllPrompts(@PathVariable ("pin") String gamePin){
      List<Prompt> allPromptsOfGame = gameService.getPromptsOfGame(gamePin);

      List<PromptGetDTO> promptsGetDTOs = new ArrayList<>();

      for (Prompt prompt : allPromptsOfGame) {
        promptsGetDTOs.add(DTOMapper.INSTANCE.convertToPromptGetDTO(prompt));
      }

      return promptsGetDTOs;
    }

    @DeleteMapping("/games/{pin}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ResponseBody
    public String deleteGame (@RequestHeader("playerToken") String loggedInToken, @PathVariable ("pin") String gamePin){
        gameService.deleteGameByPin(gamePin, loggedInToken);

        return "Deleted game successfully";
    }
}
