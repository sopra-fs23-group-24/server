package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.QualifierAnnotationAutowireCandidateResolver;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.server.ResponseStatusException;

import javax.management.MBeanServerConnection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerServiceTest {
    private Player testPlayer;

    private Game testGame;

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private PlayerService playerService;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private GameService gameService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        //playerService.setGameService(gameService);

        testPlayer = new Player();
        testPlayer.setPlayerId(2L);
        testPlayer.setAssociatedGamePin("123456");
        testPlayer.setPlayerName("test");
        testPlayer.setToken("1");
        testPlayer.setHost(true);

        Mockito.when(playerRepository.save(Mockito.any())).thenReturn(testPlayer);
        Mockito.when(playerRepository.findByToken(Mockito.any())).thenReturn(testPlayer);
        Mockito.when(playerRepository.findAllByAssociatedGamePin(Mockito.any())).thenReturn(List.of(testPlayer));
        Mockito.when(playerRepository.findAll()).thenReturn(List.of(testPlayer));

        testGame = new Game();
        testGame.setGameId(1L);
        testGame.setGamePin("123456");

        //Mockito.when(playerService.getGameService().getGameByPin(Mockito.anyString())).thenReturn(testGame);

        //Mockito.when(playerService.getGameService().addPlayerToGame(testPlayer)).thenReturn(testGame);
        //Mockito.when(gameService.addPlayerToGame(testPlayer)).thenReturn(testGame);
        //Mockito.when(playerService.getGameService().getGameByPin(testPlayer.getAssociatedGamePin())).thenReturn(testGame);
        //Mockito.when(gameService.getGameByPin(Mockito.any())).thenReturn(testGame);
        //Mockito.when(gameRepository.findByGamePin(Mockito.any())).thenReturn(testGame);
    }

    @Test
    public void getPlayers_success(){
        List<Player> allPlayers = playerService.getPlayers();
        assertEquals(allPlayers, List.of(testPlayer));
    }

    @Test
    public void getPlayersWithPin_success(){
        List<Player> allPlayers = playerService.getPlayersWithPin(testPlayer.getAssociatedGamePin());
        assertEquals(allPlayers, List.of(testPlayer));
    }

    @Test
    public void getPlayersWithPin_failure(){
        Mockito.when(playerRepository.findAllByAssociatedGamePin("invalidPin")).thenReturn(List.of());

        assertThrows(ResponseStatusException.class, () -> playerService.getPlayersWithPin("invalidPin"));
    }



    //TODO: figure out what to do because cannot properly stub gameService
    /*@Test
    public void createPlayerAndAddToGame_success() {
        Mockito.when(playerService.getGameService().getGameByPin(Mockito.anyString())).thenReturn(testGame);

        //Mockito.when(playerService.getGameService().addPlayerToGame(testPlayer)).thenReturn(testGame);

        Player createdPlayer = playerService.createPlayerAndAddToGame(testPlayer);
        Mockito.verify(playerRepository, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(gameRepository, Mockito.times(1)).save(Mockito.any());

        assertEquals(testPlayer.getPlayerId(), createdPlayer.getPlayerId());
        assertEquals(testPlayer.getPlayerName(), createdPlayer.getPlayerName());
        assertEquals(testPlayer.getAssociatedGamePin(), createdPlayer.getAssociatedGamePin());
        assertEquals(testPlayer.getToken(), createdPlayer.getToken());
        assertEquals(0, createdPlayer.getScore());
    }*/

    @Test
    public void changePlayerUsername_success(){
        String loggedInPlayerToken = "1";

        Mockito.when(playerRepository.findByPlayerId(testPlayer.getPlayerId())).thenReturn(testPlayer);
        Mockito.when(playerRepository.findByToken(loggedInPlayerToken)).thenReturn(testPlayer);

        assertEquals(playerRepository.findByPlayerId(testPlayer.getPlayerId()).getPlayerName(), testPlayer.getPlayerName());

        testPlayer.setPlayerName("newName");

        Player updatedPlayer = playerService.changePlayerUsername(testPlayer, loggedInPlayerToken);

        assertEquals(updatedPlayer.getPlayerName(), testPlayer.getPlayerName());
    }

    @Test
    public void changePlayerUsername_invalidPlayerId(){
        Player tokenPlayer = new Player();
        tokenPlayer.setPlayerId(2L);
        tokenPlayer.setAssociatedGamePin("123456");
        tokenPlayer.setPlayerName("otherPlayer");
        tokenPlayer.setToken("2");

        String loggedInPlayerToken = "2";

        Mockito.when(playerRepository.findByPlayerId(testPlayer.getPlayerId())).thenReturn(testPlayer);
        Mockito.when(playerRepository.findByToken(loggedInPlayerToken)).thenReturn(tokenPlayer);

        assertNotEquals(playerRepository.findByPlayerId(testPlayer.getPlayerId()), playerRepository.findByToken(loggedInPlayerToken));

        assertThrows(ResponseStatusException.class, () -> playerService.changePlayerUsername(testPlayer, loggedInPlayerToken));
    }
    @Test
    public void changePlayerUsername_idAndTokenDoNotMatch(){
        String loggedInPlayerToken = "1";

        Mockito.when(playerRepository.findByPlayerId(testPlayer.getPlayerId())).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        assertThrows(ResponseStatusException.class, () -> playerService.changePlayerUsername(testPlayer, loggedInPlayerToken));
    }

    @Test
    public void changePlayerUsername_emptyUsername(){
        String loggedInPlayerToken = "1";
        testPlayer.setPlayerName("");

        Mockito.when(playerRepository.findByPlayerId(testPlayer.getPlayerId())).thenReturn(testPlayer);
        Mockito.when(playerRepository.findByToken(loggedInPlayerToken)).thenReturn(testPlayer);

        assertNotNull(playerRepository.findByPlayerId(testPlayer.getPlayerId()));
        assertEquals(playerRepository.findByPlayerId(testPlayer.getPlayerId()), playerRepository.findByToken(loggedInPlayerToken));

        Mockito.when(playerRepository.findByPlayerId(testPlayer.getPlayerId())).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        assertThrows(ResponseStatusException.class, () -> playerService.changePlayerUsername(testPlayer, loggedInPlayerToken));
    }


    /**
     * Helper functions tests
     */
    @Test
    public void getPlayerById_success(){
        try{
            Mockito.when(playerRepository.findById(testPlayer.getPlayerId())).thenReturn(Optional.ofNullable(testPlayer));

            Player foundPlayer = playerService.getById(testPlayer.getPlayerId());

            assertEquals(testPlayer.getPlayerId(), foundPlayer.getPlayerId());
            assertEquals(testPlayer.getPlayerName(), foundPlayer.getPlayerName());
            assertEquals(testPlayer.getAssociatedGamePin(), foundPlayer.getAssociatedGamePin());
            assertEquals(testPlayer.getToken(), foundPlayer.getToken());
            assertEquals(0, foundPlayer.getScore());
        }catch(ResponseStatusException e){
            Assertions.fail();
        }

    }
    @Test
    public void getPlayerById_failure(){
        Mockito.when(playerRepository.findById(testPlayer.getPlayerId())).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> playerService.getById(testPlayer.getPlayerId()));
    }

    @Test
    public void getPlayerByToken_success(){
        try{
            Mockito.when(playerRepository.findByToken(testPlayer.getToken())).thenReturn(testPlayer);

            Player foundPlayer = playerService.getByToken(testPlayer.getToken());

            assertEquals(testPlayer.getPlayerId(), foundPlayer.getPlayerId());
            assertEquals(testPlayer.getPlayerName(), foundPlayer.getPlayerName());
            assertEquals(testPlayer.getAssociatedGamePin(), foundPlayer.getAssociatedGamePin());
            assertEquals(testPlayer.getToken(), foundPlayer.getToken());
            assertEquals(0, foundPlayer.getScore());
        }catch(ResponseStatusException e){
            Assertions.fail();
        }

    }
    @Test
    public void getPlayerByToken_failure(){
        Mockito.when(playerRepository.findByToken(testPlayer.getToken())).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> playerService.getById(testPlayer.getPlayerId()));
    }

}