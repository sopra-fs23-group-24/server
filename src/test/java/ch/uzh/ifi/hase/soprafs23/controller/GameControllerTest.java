package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.constant.PromptType;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Prompt;
import ch.uzh.ifi.hase.soprafs23.rest.dto.GamePutDTO;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GameController.class)
public class GameControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameService gameService;

    @Test
    public void createNewGame_returnsGame() throws Exception {
        // given
        Game game = new Game();
        game.setGamePin("123456");
        game.setStatus(GameStatus.LOBBY);

        given(gameService.createGame()).willReturn(game);

        // when
        MockHttpServletRequestBuilder postRequest = post("/games").contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.gamePin", is(game.getGamePin())))
                .andExpect(jsonPath("$.status", is(game.getStatus().toString())));
    }

    @Test
    public void getGames_returnsAllGames() throws Exception {
        // given
        Game game = new Game();
        game.setGamePin("123456");
        game.setStatus(GameStatus.LOBBY);


        List<Game> allGames = Collections.singletonList(game);

        given(gameService.getGames()).willReturn(allGames);

        // when
        MockHttpServletRequestBuilder getRequest = get("/games").contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].gamePin", is(game.getGamePin())))
                .andExpect(jsonPath("$[0].status", is(game.getStatus().toString())));
    }

    @Test
    public void getGameByPin_returnsGame() throws Exception {
        // given
        Game game = new Game();
        game.setGamePin("123456");
        game.setStatus(GameStatus.LOBBY);

        given(gameService.getGameByPin(Mockito.anyString())).willReturn(game);

        // when
        MockHttpServletRequestBuilder getRequest = get("/games/123456").contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gamePin", is(game.getGamePin())))
                .andExpect(jsonPath("$.status", is(game.getStatus().toString())));
    }

    @Test
    public void getGameByPin_invalidPin_throwsNotFound() throws Exception {
        // given
        Game game = new Game();
        game.setGamePin("123456");
        game.setStatus(GameStatus.LOBBY);

        given(gameService.getGameByPin(Mockito.anyString())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        // when
        MockHttpServletRequestBuilder getRequest = get("/games/654321").contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateGameStatus_returnsGame() throws Exception {
        // given
        Game game = new Game();
        game.setGamePin("123456");
        game.setStatus(GameStatus.SELECTION);

        GamePutDTO gamePutDTO = new GamePutDTO();
        gamePutDTO.setStatus(GameStatus.SELECTION);

        given(gameService.changeGameStatus(Mockito.any(), Mockito.anyString(), Mockito.anyString())).willReturn(game);

        // when
        MockHttpServletRequestBuilder getRequest = put("/games/123456")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gamePutDTO))
                .header("playerToken", "1");

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gamePin", is(game.getGamePin())))
                .andExpect(jsonPath("$.status", is(game.getStatus().toString())));
    }

    @Test
    public void updateGameStatus_notByHost() throws Exception {
        // given
        Game game = new Game();
        game.setGamePin("123456");
        game.setStatus(GameStatus.SELECTION);

        GamePutDTO gamePutDTO = new GamePutDTO();
        gamePutDTO.setStatus(GameStatus.SELECTION);

        given(gameService.changeGameStatus(Mockito.any(), Mockito.anyString(), Mockito.anyString())).willThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        // when
        MockHttpServletRequestBuilder getRequest = put("/games/123456")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gamePutDTO))
                .header("playerToken", "1");

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isUnauthorized());
    }
    @Test
    public void deleteGame_success() throws Exception {
        // given
        Game game = new Game();
        game.setGamePin("123456");
        game.setStatus(GameStatus.LOBBY);

        given(gameService.deleteGameByPin(Mockito.anyString(), Mockito.anyString())).willReturn(game);

        // when
        MockHttpServletRequestBuilder deleteRequest = delete("/games/123456")
                .contentType(MediaType.APPLICATION_JSON)
                .header("playerToken", "1");

        // then
        mockMvc.perform(deleteRequest)
                .andExpect(status().isAccepted());
    }

    @Test
    public void deleteGame_notByHost() throws Exception {
        // given
        Game game = new Game();
        game.setGamePin("123456");
        game.setStatus(GameStatus.SELECTION);

        given(gameService.deleteGameByPin(Mockito.anyString(), Mockito.anyString())).willThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        // when
        MockHttpServletRequestBuilder deleteRequest = delete("/games/123456")
                .contentType(MediaType.APPLICATION_JSON)
                .header("playerToken", "1");

        // then
        mockMvc.perform(deleteRequest)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void getAllPrompts_returnsPromptsOfGame() throws Exception {
      // given
      Prompt prompt = new Prompt();
      prompt.setPromptNr(999);
      prompt.setPromptType(PromptType.TEXT);
      prompt.setPromptText("example prompt");
      prompt.setPromptId(999L);


      List<Prompt> allPrompts = Collections.singletonList(prompt);

      given(gameService.getPromptsOfGame(Mockito.anyString())).willReturn(allPrompts);

      // when
      MockHttpServletRequestBuilder getRequest = get("/games/123456/prompts").contentType(MediaType.APPLICATION_JSON);

      // then
      mockMvc.perform(getRequest)
              .andExpect(status().isOk())
              .andExpect(jsonPath("$", hasSize(1)))
              .andExpect(jsonPath("$[0].promptNr", is(prompt.getPromptNr())))
              .andExpect(jsonPath("$[0].promptType", is(prompt.getPromptType().toString())))
              .andExpect(jsonPath("$[0].promptText", is(prompt.getPromptText())));
    }

    @Test
    public void getAllPrompts_invalidGamePin() throws Exception {

        given(gameService.getPromptsOfGame(Mockito.anyString())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        // when
        MockHttpServletRequestBuilder getRequest = get("/games/123456/prompts").contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isNotFound());
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