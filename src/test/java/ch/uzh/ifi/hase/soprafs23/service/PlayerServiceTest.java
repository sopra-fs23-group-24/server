package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;

public class PlayerServiceTest {

    @Mock
    private PlayerRepository gameRepository;

    @InjectMocks
    private PlayerService gameService;

    private Player testPlayer;
}