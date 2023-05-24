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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

        testGame = new Game();
        testGame.setGamePin("123456");
        testGame.setStatus(GameStatus.LOBBY);
        testGame.setHostId(800L);

        Mockito.when(gameRepository.save(Mockito.any())).thenReturn(testGame);
        Mockito.when(gameRepository.findByGamePin(testGame.getGamePin())).thenReturn(testGame);
        Mockito.when(gameRepository.findAll()).thenReturn(List.of(testGame));

        testPlayer = new Player();
        testPlayer.setAssociatedGamePin(testGame.getGamePin());
        testPlayer.setPlayerName("test");
        testPlayer.setToken("1");
        testPlayer.setHost(true);
        testPlayer.setPlayerId(100L);

        Mockito.when(playerRepository.save(Mockito.any())).thenReturn(testPlayer);
        Mockito.when(playerRepository.findByToken(Mockito.any())).thenReturn(testPlayer);
        Mockito.when(playerRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(testPlayer));
        Mockito.when(playerRepository.findAllByAssociatedGamePin(Mockito.any())).thenReturn(List.of(testPlayer));
        Mockito.when(playerRepository.findAll()).thenReturn(List.of(testPlayer));
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
    public void createPlayerAndAddToGame_notHost_success() {
        testPlayer.setHost(false);

        Mockito.when(playerRepository.save(Mockito.any())).thenReturn(testPlayer);

        Player addedPlayer = playerService.createPlayerAndAddToGame(testPlayer);
        assertEquals(addedPlayer.getPlayerName(), testPlayer.getPlayerName());
        assertEquals(addedPlayer.getScore(), 0);
    }

    @Test
    public void createPlayerAndAddToGame_host_success() {
        testPlayer.setHost(true);
        testGame.setHostId(null);

        Mockito.when(playerRepository.save(Mockito.any())).thenReturn(testPlayer);

        Player addedPlayer = playerService.createPlayerAndAddToGame(testPlayer);
        assertEquals(addedPlayer.getPlayerName(), testPlayer.getPlayerName());
        assertEquals(addedPlayer.getScore(), 0);
    }

    @Test
    public void createPlayerAndAddToGame_invalidGamePin() {
        testPlayer.setAssociatedGamePin("invalidPin");

        Mockito.when(gameRepository.findByGamePin("invalidPin")).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> playerService.createPlayerAndAddToGame(testPlayer));
    }

    @Test
    public void createPlayerAndAddToGame_notInLobby() {
        testGame.setStatus(GameStatus.SELECTION);

        assertThrows(ResponseStatusException.class, () -> playerService.createPlayerAndAddToGame(testPlayer));
    }

    @Test
    public void createPlayerAndAddToGame_alreadyHasHost() {
        testGame.setHostId(999L);
        testPlayer.setHost(true);

        assertThrows(ResponseStatusException.class, () -> playerService.createPlayerAndAddToGame(testPlayer));
    }

    @Test
    public void createPlayerAndAddToGame_duplicateUsername() {
        Mockito.when(playerRepository.findByPlayerNameAndAssociatedGamePin(Mockito.anyString(), Mockito.anyString())).thenReturn(new Player());

        assertThrows(ResponseStatusException.class, () -> playerService.createPlayerAndAddToGame(testPlayer));
    }


    @Test
    public void changePlayerUsername_sucess() {
        testPlayer.setPlayerName("newName");
        Player addedPlayer = playerService.changePlayerUsername(testPlayer, testPlayer.getToken());
        assertEquals(addedPlayer.getPlayerName(), testPlayer.getPlayerName());
    }

    @Test
    public void changePlayerUsername_unauthorized() {
        Mockito.when(playerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new Player()));

        assertThrows(ResponseStatusException.class, () -> playerService.changePlayerUsername(testPlayer, "notTheirToken"));
    }

    @Test
    public void changePlayerUsername_duplicateUsername() {
        Mockito.when(playerRepository.findByPlayerNameAndAssociatedGamePin(Mockito.anyString(), Mockito.anyString())).thenReturn(new Player());

        assertThrows(ResponseStatusException.class, () -> playerService.changePlayerUsername(testPlayer, testPlayer.getToken()));
    }

    @Test
    public void changePlayerUsername_emptyUsername() {
        testPlayer.setPlayerName("");

        Mockito.when(playerRepository.findByPlayerNameAndAssociatedGamePin(Mockito.anyString(), Mockito.anyString())).thenReturn(new Player());

        assertThrows(ResponseStatusException.class, () -> playerService.changePlayerUsername(testPlayer, testPlayer.getToken()));
    }

    @Test
    public void changePlayerUsername_invalidId() {
        Mockito.when(playerRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(null));

        assertThrows(ResponseStatusException.class, () -> playerService.changePlayerUsername(testPlayer, testPlayer.getToken()));
    }

    @Test
    public void changePlayerUsername_invalidToken() {
        Mockito.when(playerRepository.findByToken(Mockito.anyString())).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> playerService.changePlayerUsername(testPlayer, "invalidToken"));
    }

    @Test
    public void deletePlayer_success() {
        testGame.addPlayer(testPlayer);
        Player deletedPlayer = playerService.deletePlayer(testPlayer.getPlayerId(), testPlayer.getToken(), testPlayer.getAssociatedGamePin());
        assertEquals(deletedPlayer.getPlayerName(), testPlayer.getPlayerName());
    }


    @Test
    public void deletePlayer_unauthorized() {
        testGame.setHostId(1L);
        Player notTestPlayer = new Player();
        notTestPlayer.setPlayerId(99L);
        Mockito.when(playerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(notTestPlayer));

        assertThrows(ResponseStatusException.class, () -> playerService.deletePlayer(99L, testPlayer.getToken(), testPlayer.getAssociatedGamePin()));
    }

    @Test
    public void deletePlayer_isHost() {
        testPlayer.setPlayerId(testGame.getHostId());

        assertThrows(ResponseStatusException.class, () -> playerService.deletePlayer(testPlayer.getPlayerId(), testPlayer.getToken(), testPlayer.getAssociatedGamePin()));
    }

    @Test
    public void deletePlayer_invalidId() {
        Mockito.when(playerRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(null));

        assertThrows(ResponseStatusException.class, () -> playerService.deletePlayer(testPlayer.getPlayerId(), testPlayer.getToken(), testPlayer.getAssociatedGamePin()));
    }

    @Test
    public void deletePlayer_invalidToken() {
        Mockito.when(playerRepository.findByToken(Mockito.anyString())).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> playerService.deletePlayer(testPlayer.getPlayerId(), "invalidToken", testPlayer.getAssociatedGamePin()));
    }

    @Test
    public void sortPlayersByScore_success() {
        Player player1 = new Player();
        player1.setScore(100);
        Player player2 = new Player();
        player2.setScore(50);
        Player player3 = new Player();
        player3.setScore(0);

        List<Player> unsortedList = new ArrayList<>();
        unsortedList.add(player3);
        unsortedList.add(player1);
        unsortedList.add(player2);

        List<Player> sortedList = playerService.sortPlayersByScore(unsortedList);
        assertEquals(player1, sortedList.get(0));
        assertEquals(player2, sortedList.get(1));
        assertEquals(player3, sortedList.get(2));
        for (Player player : sortedList) {
            int currentIndex = sortedList.indexOf(player);
            assert currentIndex <= 0 || (player.getScore() <= sortedList.get(currentIndex - 1).getScore());
            assert currentIndex >= sortedList.size() - 1 || (player.getScore() >= sortedList.get(currentIndex + 1).getScore());
        }
    }
}