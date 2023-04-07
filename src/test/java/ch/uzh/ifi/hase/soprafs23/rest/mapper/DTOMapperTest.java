package ch.uzh.ifi.hase.soprafs23.rest.mapper;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.GamePutDTO;
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
}