package ch.uzh.ifi.hase.soprafs23.rest.mapper;

import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
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
  @Mapping(source = "hostId", target = "hostId")
  @Mapping(source = "playerGroup", target = "playerGroup")
  GameGetDTO convertToGameGetDTO(Game game);


  @Mapping(source = "playerId", target = "playerId")
  @Mapping(source = "associatedGamePin", target = "associatedGamePin")
  @Mapping(source = "playerName", target = "playerName")
  @Mapping(source = "token", target = "token")
  @Mapping(source = "score", target = "score")
  PlayerGetDTO convertToPlayerGetDTO(Player player);

  @Mapping(source = "gamePin", target = "gamePin")
  Game convertFromGameJoinDTO(GameJoinDTO game);

  @Mapping(source = "playerName", target = "playerName")
  Player convertFromPlayerPutDTO(PlayerPutDTO player);

/*

  @Mapping(source = "name", target = "name")
  @Mapping(source = "username", target = "username")
  Player convertUserPostDTOtoEntity(UserPutDTO userPostDTO);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "username", target = "username")
  @Mapping(source = "status", target = "status")
  PlayerGetDTO convertEntityToUserGetDTO(Player user);
 */
}
