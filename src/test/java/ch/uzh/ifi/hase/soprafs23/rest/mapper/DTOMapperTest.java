package ch.uzh.ifi.hase.soprafs23.rest.mapper;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.rest.dto.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * DTOMapperTest
 * Tests if the mapping between the internal and the external/API representation
 * works.
 */
public class DTOMapperTest {

    @Test
    public void convertToGameGetDTO_fromGame_success() {
        Game game = new Game();

        game.setGamePin("123456");
        game.setGameId(1L);
        game.setStatus(GameStatus.LOBBY);

        GameGetDTO gameGetDTO = DTOMapper.INSTANCE.convertToGameGetDTO(game);

        assertEquals(gameGetDTO.getGamePin(), game.getGamePin());
        assertEquals(gameGetDTO.getGameId(), game.getGameId());
        assertEquals(gameGetDTO.getStatus(), game.getStatus());
    }

    @Test
    public void convertFromGamePutDTO_toGame_success(){
        GamePutDTO gamePutDTO = new GamePutDTO();
        gamePutDTO.setStatus(GameStatus.SELECTION);

        Game game = DTOMapper.INSTANCE.convertFromGamePutDTO(gamePutDTO);
        assertEquals(gamePutDTO.getStatus(), game.getStatus());
    }

    @Test
    public void convertFromPlayerPostDTO_toPlayer_success(){
        PlayerPostDTO playerPostDTO = new PlayerPostDTO();
        playerPostDTO.setPlayerName("test");
        playerPostDTO.setIsHost(true);

        Player player = DTOMapper.INSTANCE.convertFromPlayerPostDTO(playerPostDTO);
        assertEquals(playerPostDTO.getPlayerName(), player.getPlayerName());
        assertEquals(playerPostDTO.isHost(), player.isHost());
    }
    @Test
    public void convertToPlayerGetDTO_fromPlayer_success(){
        Player player = new Player();
        player.setPlayerName("test");
        player.setPlayerId(1L);
        player.setScore(0);

        PlayerGetDTO playerGetDTO = DTOMapper.INSTANCE.convertToPlayerGetDTO(player);
        assertEquals(playerGetDTO.getPlayerName(), player.getPlayerName());
        assertEquals(playerGetDTO.getScore(), player.getScore());
        assertEquals(playerGetDTO.getPlayerId(), player.getPlayerId());
    }
    @Test
    public void convertFromPlayerPutDTO_toPlayer_success(){
        PlayerPutDTO playerPutDTO = new PlayerPutDTO();
        playerPutDTO.setPlayerName("test");

        Player player = DTOMapper.INSTANCE.convertFromPlayerPutDTO(playerPutDTO);
        assertEquals(playerPutDTO.getPlayerName(), player.getPlayerName());
    }

    @Test
    public void convertFromTextPromptAnswerDTO_toTextPromptAnswer_success() {
        // implement
    }

    @Test
    public void convertFromTrueFalsePromptAnswerPostDTO_toTrueFalsePromptAnswer_success() {
        // implement
    }


}