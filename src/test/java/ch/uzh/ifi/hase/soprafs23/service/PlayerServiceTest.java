package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerServiceTest {
    private Player testPlayer;

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private PlayerService playerService;

    /*@Mock
    private GameRepository gameRepository;

    @InjectMocks
    private GameService gameService;*/

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

        Game testGame = new Game();
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
    public void getPlayers_success() {
        List<Player> allPlayers = playerService.getPlayers();
        assertEquals(allPlayers, List.of(testPlayer));
    }

    @Test
    public void getPlayersWithPin_success() {
        List<Player> allPlayers = playerService.getPlayersWithPin(testPlayer.getAssociatedGamePin());
        assertEquals(allPlayers, List.of(testPlayer));
    }

    // score must either not be declared or not set other than 0 in the setup
    @Test
    public void getScoreOfAllPlayersFromGame(){
        List<Player> allPlayers = playerService.getPlayersWithPin(testPlayer.getAssociatedGamePin());
        for(Player player : allPlayers) {
            assert player.getScore() == 0;
        }
    }

    @Test
    public void getPlayersWithPin_failure() {
        Mockito.when(playerRepository.findAllByAssociatedGamePin("invalidPin")).thenReturn(List.of());

        assertThrows(ResponseStatusException.class, () -> playerService.getPlayersWithPin("invalidPin"));
    }


    //TODO: addPlayer tests
    /*@Test
    public void addPlayerToGame_success() {
        Mockito.when(gameRepository.findByGamePin(testPlayer.getAssociatedGamePin())).thenReturn(testGame);

        assertEquals(gameService.getGameByPin(testGame.getGamePin()).getPlayerGroup(), new ArrayList<Player>());

        testGame.setStatus(GameStatus.LOBBY);
        testGame.setHostId(null);

        Game gameWithPlayer = gameService.addPlayerToGame(testPlayer);

        Mockito.verify(gameRepository, Mockito.times(1)).save(Mockito.any());
        assertTrue(gameWithPlayer.getPlayerGroup().contains(testPlayer));
    }

    @Test
    public void addPlayerToGame_invalidGamePin() {
        Mockito.when(gameRepository.findByGamePin(testPlayer.getAssociatedGamePin())).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        testGame.setStatus(GameStatus.LOBBY);
        testGame.setHostId(null);

        assertThrows(ResponseStatusException.class, () -> gameService.addPlayerToGame(testPlayer));
    }

    @Test
    public void addPlayerToGame_gameNotInLobbyStage() {
        Mockito.when(gameRepository.findByGamePin(testPlayer.getAssociatedGamePin())).thenReturn(testGame);

        testPlayer.setHost(true);
        testGame.setStatus(GameStatus.SELECTION);

        assertThrows(ResponseStatusException.class, () -> gameService.addPlayerToGame(testPlayer));
    }

    @Test
    public void addPlayerToGame_alreadyHasHost() {
        Mockito.when(gameRepository.findByGamePin(testPlayer.getAssociatedGamePin())).thenReturn(testGame);

        assertNotNull(gameService.getGameByPin(testGame.getGamePin()).getHostId());

        testPlayer.setHost(true);
        testGame.setStatus(GameStatus.LOBBY);

        assertThrows(ResponseStatusException.class, () -> gameService.addPlayerToGame(testPlayer));
    }*/

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
    public void changePlayerUsername_success() {
        String loggedInPlayerToken = "1";

        Mockito.when(playerRepository.findByPlayerId(testPlayer.getPlayerId())).thenReturn(testPlayer);
        Mockito.when(playerRepository.findByToken(loggedInPlayerToken)).thenReturn(testPlayer);

        assertEquals(playerRepository.findByPlayerId(testPlayer.getPlayerId()).getPlayerName(), testPlayer.getPlayerName());

        testPlayer.setPlayerName("newName");

        Player updatedPlayer = playerService.changePlayerUsername(testPlayer, loggedInPlayerToken);

        assertEquals(updatedPlayer.getPlayerName(), testPlayer.getPlayerName());
    }

    @Test
    public void changePlayerUsername_invalidPlayerId() {
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
    public void changePlayerUsername_idAndTokenDoNotMatch() {
        String loggedInPlayerToken = "1";

        Mockito.when(playerRepository.findByPlayerId(testPlayer.getPlayerId())).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        assertThrows(ResponseStatusException.class, () -> playerService.changePlayerUsername(testPlayer, loggedInPlayerToken));
    }

    @Test
    public void changePlayerUsername_emptyUsername() {
        String loggedInPlayerToken = "1";
        testPlayer.setPlayerName("");

        Mockito.when(playerRepository.findByPlayerId(testPlayer.getPlayerId())).thenReturn(testPlayer);
        Mockito.when(playerRepository.findByToken(loggedInPlayerToken)).thenReturn(testPlayer);

        assertNotNull(playerRepository.findByPlayerId(testPlayer.getPlayerId()));
        assertEquals(playerRepository.findByPlayerId(testPlayer.getPlayerId()), playerRepository.findByToken(loggedInPlayerToken));

        Mockito.when(playerRepository.findByPlayerId(testPlayer.getPlayerId())).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        assertThrows(ResponseStatusException.class, () -> playerService.changePlayerUsername(testPlayer, loggedInPlayerToken));
    }

    /*
    @Test
    public void deletePlayer_success() {
        // must be host or must be the player itself, cannot be host
        // here I use a player that deletes itself
        Player tokenPlayer = new Player();
        tokenPlayer.setPlayerId(2L);
        tokenPlayer.setAssociatedGamePin("123456");
        tokenPlayer.setPlayerName("otherPlayer");
        tokenPlayer.setToken("2");
        String loggedInPlayerToken = "2";

        Mockito.when(playerRepository.findByPlayerId(testPlayer.getPlayerId())).thenReturn(tokenPlayer);
        Mockito.when(playerRepository.findByToken(loggedInPlayerToken)).thenReturn(tokenPlayer);

        Mockito.when(playerService.getByToken(loggedInPlayerToken)).thenReturn(tokenPlayer);
        Mockito.when(playerService.getById(tokenPlayer.getPlayerId())).thenReturn(tokenPlayer);
        Mockito.when(playerService.deletePlayer(tokenPlayer.getPlayerId(),tokenPlayer.getToken(),tokenPlayer.getAssociatedGamePin())).thenReturn(tokenPlayer);

        // test if player exists
        Player existingPlayer = playerRepository.findByPlayerId(tokenPlayer.getPlayerId());
        assertNotNull(existingPlayer);
        // delete player
        Player player = playerService.deletePlayer(tokenPlayer.getPlayerId(),tokenPlayer.getToken(),tokenPlayer.getAssociatedGamePin());
        // test if player does not exist anymore
        assertEquals(player, tokenPlayer);
    }

    @Test
    public void deletePlayer_failure() {
        // if host, or if another player as non-host

        // some assertThrows
    }

     */



    /**
     * Helper functions tests
     */
    @Test
    public void getPlayerById_success() {
        try {
            Mockito.when(playerRepository.findById(testPlayer.getPlayerId())).thenReturn(Optional.ofNullable(testPlayer));

            Player foundPlayer = playerService.getById(testPlayer.getPlayerId());

            assertEquals(testPlayer.getPlayerId(), foundPlayer.getPlayerId());
            assertEquals(testPlayer.getPlayerName(), foundPlayer.getPlayerName());
            assertEquals(testPlayer.getAssociatedGamePin(), foundPlayer.getAssociatedGamePin());
            assertEquals(testPlayer.getToken(), foundPlayer.getToken());
            assertEquals(0, foundPlayer.getScore());
        }
        catch (ResponseStatusException e) {
            Assertions.fail();
        }

    }

    @Test
    public void getPlayerById_failure() {
        Mockito.when(playerRepository.findById(testPlayer.getPlayerId())).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> playerService.getById(testPlayer.getPlayerId()));
    }

    @Test
    public void getPlayerByToken_success() {
        try {
            Mockito.when(playerRepository.findByToken(testPlayer.getToken())).thenReturn(testPlayer);

            Player foundPlayer = playerService.getByToken(testPlayer.getToken());

            assertEquals(testPlayer.getPlayerId(), foundPlayer.getPlayerId());
            assertEquals(testPlayer.getPlayerName(), foundPlayer.getPlayerName());
            assertEquals(testPlayer.getAssociatedGamePin(), foundPlayer.getAssociatedGamePin());
            assertEquals(testPlayer.getToken(), foundPlayer.getToken());
            assertEquals(0, foundPlayer.getScore());
        }
        catch (ResponseStatusException e) {
            Assertions.fail();
        }

    }

    @Test
    public void getPlayerByToken_failure() {
        Mockito.when(playerRepository.findByToken(testPlayer.getToken())).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> playerService.getById(testPlayer.getPlayerId()));
    }

}