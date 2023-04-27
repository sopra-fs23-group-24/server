package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.CompletionStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.GamePutDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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
            gamesGetDTOs.add(convertGameToDTO(game));
        }

        return gamesGetDTOs;
    }

    @GetMapping("/games/{pin}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGetDTO getGameByPin(@PathVariable("pin") String gamePin) {
        Game currentGame = gameService.getGameByPin(gamePin);

        return convertGameToDTO(currentGame);
    }

    @PutMapping("/games/{pin}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGetDTO updateGameStatus(@RequestBody GamePutDTO newStatus, @RequestHeader("playerToken") String loggedInToken, @PathVariable("pin") String gamePin) {
        Game newStatusGame = DTOMapper.INSTANCE.convertFromGamePutDTO(newStatus);
        Game updatedGame = gameService.changeGameStatus(newStatusGame.getStatus(), gamePin, loggedInToken);
        return DTOMapper.INSTANCE.convertToGameGetDTO(updatedGame);
    }

    @PutMapping("/games/{pin}/quizQuestions")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGetDTO requestNextQuizQuestion(@PathVariable("pin") String gamePin, @RequestHeader("playerToken") String loggedInToken) {
        Game updatedGame = gameService.changeToNextQuestion(gamePin, loggedInToken);

        return convertGameToDTO(updatedGame);
    }

    @DeleteMapping("/games/{pin}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ResponseBody
    public String deleteGame(@RequestHeader("playerToken") String loggedInToken, @PathVariable("pin") String gamePin) {
        gameService.deleteGameByPin(gamePin, loggedInToken);

        return "Deleted game successfully";
    }

    private GameGetDTO convertGameToDTO(Game game) {
        GameGetDTO gameGetDTO = DTOMapper.INSTANCE.convertToGameGetDTO(game);
        gameGetDTO.setCurrentQuestion(DTOMapper.INSTANCE.convertToQuizQuestionGetDTO(game.getCurrentQuestion()));
        if (game.getCurrentQuestion() != null && game.getCurrentQuestion().getQuestionStatus() == CompletionStatus.NOT_FINISHED) {
            gameGetDTO.getCurrentQuestion().setCorrectAnswer(null);
        }
        return gameGetDTO;
    }
}
