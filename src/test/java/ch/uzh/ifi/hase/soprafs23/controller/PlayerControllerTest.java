package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PlayerPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PlayerPutDTO;
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
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlayerController.class)
class PlayerControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlayerService playerService;

    private Player testPlayer;

    @BeforeEach
    void setup() {
        testPlayer = new Player();
        testPlayer.setPlayerName("test");
        testPlayer.setAssociatedGamePin("123456");
        testPlayer.setHost(true);
        testPlayer.setToken("1");
        testPlayer.setPlayerId(1L);
    }

    @Test
    void newPlayerInGame_returnsPlayerAndToken() throws Exception {
        PlayerPostDTO playerPostDTO = new PlayerPostDTO();
        playerPostDTO.setPlayerName("test");
        playerPostDTO.setIsHost(true);

        given(playerService.createPlayerAndAddToGame(Mockito.any())).willReturn(testPlayer);

        MockHttpServletRequestBuilder postRequest = post("/games/123456/players")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(playerPostDTO));

        MvcResult mvcResult = mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.score", is(testPlayer.getScore())))
                .andExpect(jsonPath("$.latestScore", is(testPlayer.getLatestScore())))
                .andExpect(jsonPath("$.playerName", is(testPlayer.getPlayerName())))
                .andReturn();
        assertEquals(mvcResult.getResponse().getHeader("playerToken"), testPlayer.getToken());
    }

    @Test
    void newPlayerInGame_invalidPin() throws Exception {
        PlayerPostDTO playerPostDTO = new PlayerPostDTO();
        playerPostDTO.setPlayerName("test");
        playerPostDTO.setIsHost(true);

        given(playerService.createPlayerAndAddToGame(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        MockHttpServletRequestBuilder postRequest = post("/games/123456/players")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(playerPostDTO));

        mockMvc.perform(postRequest)
                .andExpect(status().isNotFound());
    }

    @Test
    void newPlayerInGame_gameRunning() throws Exception {
        PlayerPostDTO playerPostDTO = new PlayerPostDTO();
        playerPostDTO.setPlayerName("test");
        playerPostDTO.setIsHost(true);

        given(playerService.createPlayerAndAddToGame(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));

        MockHttpServletRequestBuilder postRequest = post("/games/123456/players")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(playerPostDTO));

        mockMvc.perform(postRequest)
                .andExpect(status().isBadRequest());
    }

    @Test
    void newPlayerInGame_gameAlreadyHasHostOrUsernameDuplicate() throws Exception {
        PlayerPostDTO playerPostDTO = new PlayerPostDTO();
        playerPostDTO.setPlayerName("test");
        playerPostDTO.setIsHost(true);

        given(playerService.createPlayerAndAddToGame(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));

        MockHttpServletRequestBuilder postRequest = post("/games/123456/players")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(playerPostDTO));

        mockMvc.perform(postRequest)
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllPlayers_returnsAllPlayers() throws Exception {
        List<Player> allPlayers = Collections.singletonList(testPlayer);

        given(playerService.getPlayers()).willReturn(allPlayers);

        MockHttpServletRequestBuilder getRequest = get("/players").contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].playerName", is(testPlayer.getPlayerName())))
                .andExpect(jsonPath("$[0].score", is(testPlayer.getScore())));
    }

    @Test
    void getAllPlayersOfGame_returnsAllPlayersOfGame() throws Exception {
        List<Player> allPlayers = Collections.singletonList(testPlayer);

        given(playerService.getPlayersWithPin(Mockito.anyString())).willReturn(allPlayers);
        given(playerService.sortPlayersByScore(Mockito.any())).willReturn(allPlayers);

        MockHttpServletRequestBuilder getRequest = get("/games/123456/players").contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].playerName", is(testPlayer.getPlayerName())))
                .andExpect(jsonPath("$[0].latestScore", is(testPlayer.getLatestScore())))
                .andExpect(jsonPath("$[0].score", is(testPlayer.getScore())));
    }

    @Test
    void getAllPlayersOfGame_invalidGamePin() throws Exception {
        given(playerService.getPlayersWithPin(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        MockHttpServletRequestBuilder getRequest = get("/games/123456/players").contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest)
                .andExpect(status().isNotFound());
    }

    @Test
    void changeUsername_success() throws Exception {
        PlayerPutDTO playerPutDTO = new PlayerPutDTO();
        playerPutDTO.setPlayerName("newName");

        Player updatedPlayer = new Player();
        updatedPlayer.setPlayerName("newName");
        updatedPlayer.setPlayerId(2L);
        updatedPlayer.setToken("2");
        updatedPlayer.setAssociatedGamePin("123456");

        given(playerService.changePlayerUsername(Mockito.any(), Mockito.anyString())).willReturn(updatedPlayer);

        MockHttpServletRequestBuilder putRequest = put("/players/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(playerPutDTO))
                .header("playerToken", "2");

        mockMvc.perform(putRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playerName", is(updatedPlayer.getPlayerName())))
                .andExpect(jsonPath("$.score", is(updatedPlayer.getScore())));
    }

    @Test
    void changeUsername_invalidPlayerId() throws Exception {
        PlayerPutDTO playerPutDTO = new PlayerPutDTO();
        playerPutDTO.setPlayerName("newName");

        given(playerService.changePlayerUsername(Mockito.any(), Mockito.anyString())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        MockHttpServletRequestBuilder putRequest = put("/players/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(playerPutDTO))
                .header("playerToken", "2");

        mockMvc.perform(putRequest)
                .andExpect(status().isNotFound());
    }

    @Test
    void changeUsername_idAndTokenDoNotMatch() throws Exception {
        PlayerPutDTO playerPutDTO = new PlayerPutDTO();
        playerPutDTO.setPlayerName("newName");

        given(playerService.changePlayerUsername(Mockito.any(), Mockito.anyString())).willThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        MockHttpServletRequestBuilder putRequest = put("/players/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(playerPutDTO))
                .header("playerToken", "1");

        mockMvc.perform(putRequest)
                .andExpect(status().isUnauthorized());
    }

    @Test
    void changeUsername_usernameIsBlankOrAlreadyUsed() throws Exception {
        PlayerPutDTO playerPutDTO = new PlayerPutDTO();
        playerPutDTO.setPlayerName("");

        given(playerService.changePlayerUsername(Mockito.any(), Mockito.anyString())).willThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));

        MockHttpServletRequestBuilder putRequest = put("/players/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(playerPutDTO))
                .header("playerToken", "2");

        mockMvc.perform(putRequest)
                .andExpect(status().isBadRequest());
    }

    @Test
    void deletePlayer_success() throws Exception {
        given(playerService.deletePlayer(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString())).willReturn(testPlayer);

        MockHttpServletRequestBuilder deleteRequest = delete("/games/123456/players/2")
                .contentType(MediaType.APPLICATION_JSON)
                .header("playerToken", "2");

        mockMvc.perform(deleteRequest)
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.playerName", is(testPlayer.getPlayerName())))
                .andExpect(jsonPath("$.score", is(testPlayer.getScore())));

    }

    @Test
    void deletePlayer_playerIsHost() throws Exception {
        given(playerService.deletePlayer(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString())).willThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));

        MockHttpServletRequestBuilder deleteRequest = delete("/games/123456/players/2")
                .contentType(MediaType.APPLICATION_JSON)
                .header("playerToken", "2");

        mockMvc.perform(deleteRequest)
                .andExpect(status().isBadRequest());

    }

    @Test
    void deletePlayer_notLoggedInAsHostOrPlayer() throws Exception {
        given(playerService.deletePlayer(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString())).willThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        MockHttpServletRequestBuilder deleteRequest = delete("/games/123456/players/2")
                .contentType(MediaType.APPLICATION_JSON)
                .header("playerToken", "2");

        mockMvc.perform(deleteRequest)
                .andExpect(status().isUnauthorized());

    }

    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("The request body could not be created.%s", e));
        }
    }
}