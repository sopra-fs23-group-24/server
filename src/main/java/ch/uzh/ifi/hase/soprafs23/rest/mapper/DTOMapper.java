package ch.uzh.ifi.hase.soprafs23.rest.mapper;

import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.Prompt;
import ch.uzh.ifi.hase.soprafs23.rest.dto.*;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * DTOMapper
 * This class is responsible for generating classes that will automatically
 * transform/map the internal representation
 * of an entity (e.g., the User) to the external/API representation (e.g.,
 * UserGetDTO for getting, UserPostDTO for creating)
 * and vice versa.
 * Additional mappers can be defined for new entities.
 * Always created one mapper for getting information (GET) and one mapper for
 * creating information (POST).
 */

@Mapper
public interface DTOMapper {

    DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);


  @Mapping(source = "gameId", target = "gameId")
  @Mapping(source = "gamePin", target = "gamePin")
  @Mapping(source = "status", target = "status")
  GameGetDTO convertToGameGetDTO(Game game);
  @Mapping(source = "status", target = "status")
  Game convertFromGamePutDTO(GamePutDTO game);


  @Mapping(source = "playerId", target = "playerId")
  @Mapping(source = "playerName", target = "playerName")
  @Mapping(source = "score", target = "score")
  PlayerGetDTO convertToPlayerGetDTO(Player player);

  @Mapping(source = "playerName", target = "playerName")
  @Mapping(source = "host", target = "host")
  Player convertFromPlayerPostDTO(PlayerPostDTO game);

  @Mapping(source = "playerName", target = "playerName")
  Player convertFromPlayerPutDTO(PlayerPutDTO player);

  @Mapping(source = "promptId", target = "promptId")
  @Mapping(source = "promptType", target = "promptType")
  @Mapping(source = "promptText", target = "promptText")
  PromptGetDTO convertToPromptGetDTO(Prompt prompt);

}
