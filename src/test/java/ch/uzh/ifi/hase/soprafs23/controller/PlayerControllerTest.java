package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PlayerPostDTO;
import ch.uzh.ifi.hase.soprafs23.service.PlayerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PlayerController.class)
public class PlayerControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlayerService playerService;

    private Player testPlayer;

    @BeforeEach
    public void setup(){
        testPlayer = new Player();
        testPlayer.setPlayerName("test");
        testPlayer.setAssociatedGamePin("123456");
        testPlayer.setHost(true);
        testPlayer.setToken("1");
        testPlayer.setPlayerId(1L);
    }

    @Test
    public void newPlayerInGame_returnsPlayerAndToken() throws Exception {
        PlayerPostDTO playerPostDTO = new PlayerPostDTO();
        playerPostDTO.setPlayerName("test");
        playerPostDTO.setIsHost(true);

        given(playerService.createPlayerAndAddToGame(Mockito.any())).willReturn(testPlayer);

        // when
        MockHttpServletRequestBuilder postRequest = post("/games/123456/players")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(playerPostDTO));

        // then
        MvcResult mvcResult = mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.score", is(testPlayer.getScore())))
                .andExpect(jsonPath("$.playerName", is(testPlayer.getPlayerName())))
                .andReturn();
        assertEquals(mvcResult.getResponse().getHeader("playerToken"), testPlayer.getToken());
    }

    @Test
    public void newPlayerInGame_invalidPin() throws Exception{
        PlayerPostDTO playerPostDTO = new PlayerPostDTO();
        playerPostDTO.setPlayerName("test");
        playerPostDTO.setIsHost(true);

        given(playerService.createPlayerAndAddToGame(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        // when
        MockHttpServletRequestBuilder postRequest = post("/games/123456/players")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(playerPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isNotFound());
    }

    @Test
    public void newPlayerInGame_gameRunning() throws Exception{
        PlayerPostDTO playerPostDTO = new PlayerPostDTO();
        playerPostDTO.setPlayerName("test");
        playerPostDTO.setIsHost(true);

        given(playerService.createPlayerAndAddToGame(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));

        // when
        MockHttpServletRequestBuilder postRequest = post("/games/123456/players")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(playerPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void newPlayerInGame_gameAlreadyHasHost() throws Exception{
        PlayerPostDTO playerPostDTO = new PlayerPostDTO();
        playerPostDTO.setPlayerName("test");
        playerPostDTO.setIsHost(true);

        given(playerService.createPlayerAndAddToGame(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));

        // when
        MockHttpServletRequestBuilder postRequest = post("/games/123456/players")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(playerPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getAllPlayers_returnsAllPlayers() throws Exception {
        List<Player> allPlayers = Collections.singletonList(testPlayer);

        given(playerService.getPlayers()).willReturn(allPlayers);

        // when
        MockHttpServletRequestBuilder getRequest = get("/players").contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].playerName", is(testPlayer.getPlayerName())))
                .andExpect(jsonPath("$[0].score", is(testPlayer.getScore())));
    }

    @Test
    public void getAllPlayersOfGame_returnsAllPlayersOfGame() throws Exception {
        List<Player> allPlayers = Collections.singletonList(testPlayer);

        given(playerService.getPlayersWithPin(Mockito.anyString())).willReturn(allPlayers);

        // when
        MockHttpServletRequestBuilder getRequest = get("/games/123456/players").contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].playerName", is(testPlayer.getPlayerName())))
                .andExpect(jsonPath("$[0].score", is(testPlayer.getScore())));
    }


    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("The request body could not be created.%s", e.toString()));
        }
    }
}