package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PlayerServiceTest {
    private Player testPlayer;

    private Game testGame;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private PlayerService playerService;


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
        testGame.setHostId(2L); //bc cannot be 1 bc game is 1 already

        Mockito.when(gameRepository.save(Mockito.any())).thenReturn(testGame);
        Mockito.when(gameRepository.findByGamePin(testGame.getGamePin())).thenReturn(testGame);
        Mockito.when(gameRepository.findAll()).thenReturn(List.of(testGame));

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
    public void getScoreOfAllPlayersFromGame() {
        List<Player> allPlayers = playerService.getPlayersWithPin(testPlayer.getAssociatedGamePin());
        for (Player player : allPlayers) {
            assert player.getScore() == 0;
        }
    }

    @Test
    public void getPlayersWithPin_failure() {
        Mockito.when(playerRepository.findAllByAssociatedGamePin("invalidPin")).thenReturn(List.of());

        assertThrows(ResponseStatusException.class, () -> playerService.getPlayersWithPin("invalidPin"));
    }

    @Test
    public void createPlayerAndAddToGame_success() {
        Player newPlayer = new Player();
        newPlayer.setPlayerName("name");
        newPlayer.setHost(false);
        newPlayer.setAssociatedGamePin(testGame.getGamePin());

        testGame.setStatus(GameStatus.LOBBY);

        Mockito.when(playerRepository.save(Mockito.any())).thenReturn(newPlayer);

        Player addedPlayer = playerService.createPlayerAndAddToGame(newPlayer);
        assertEquals(addedPlayer.getPlayerName(), newPlayer.getPlayerName());
        assertEquals(addedPlayer.getScore(), 0);
    }

    @Test
    public void createPlayerAndAddToGame_invalidGamePin() {
        Player newPlayer = new Player();
        newPlayer.setPlayerName("name");
        newPlayer.setHost(false);
        newPlayer.setAssociatedGamePin("invalidPin");

        Mockito.when(gameRepository.findByGamePin("invalidPin")).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> playerService.createPlayerAndAddToGame(newPlayer));
    }

    @Test
    public void createPlayerAndAddToGame_notInLobby() {
        Player newPlayer = new Player();
        newPlayer.setPlayerName("name");
        newPlayer.setHost(false);
        newPlayer.setAssociatedGamePin(testGame.getGamePin());

        testGame.setStatus(GameStatus.SELECTION);

        assertThrows(ResponseStatusException.class, () -> playerService.createPlayerAndAddToGame(newPlayer));
    }

    @Test
    public void createPlayerAndAddToGame_alreadyHasHost() {
        Player newPlayer = new Player();
        newPlayer.setPlayerName("name");
        newPlayer.setHost(true);
        newPlayer.setAssociatedGamePin(testGame.getGamePin());

        testGame.setStatus(GameStatus.LOBBY);

        assertThrows(ResponseStatusException.class, () -> playerService.createPlayerAndAddToGame(newPlayer));
    }

    @Test
    public void createPlayerAndAddToGame_duplicateUsername() {
        Player newPlayer = new Player();
        newPlayer.setPlayerName("name");
        newPlayer.setHost(true);
        newPlayer.setAssociatedGamePin(testGame.getGamePin());

        testGame.setStatus(GameStatus.LOBBY);
        Mockito.when(playerRepository.findByPlayerNameAndAssociatedGamePin(Mockito.anyString(), Mockito.anyString())).thenReturn(new Player());

        assertThrows(ResponseStatusException.class, () -> playerService.createPlayerAndAddToGame(newPlayer));
    }


}