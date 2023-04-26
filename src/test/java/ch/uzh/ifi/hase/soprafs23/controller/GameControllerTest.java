package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.CompletionStatus;
import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.quiz.AnswerOption;
import ch.uzh.ifi.hase.soprafs23.entity.quiz.QuizQuestion;
import ch.uzh.ifi.hase.soprafs23.rest.dto.GamePutDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.isNull;
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
    public void getAllGames_returnsAllGames() throws Exception {
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
    public void getGameByPin_returnsGame_currentQuestionNull() throws Exception {
        // given
        Game game = new Game();
        game.setGamePin("123456");
        game.setStatus(GameStatus.LOBBY);
        game.setCurrentQuestion(null);

        given(gameService.getGameByPin(Mockito.anyString())).willReturn(game);

        // when
        MockHttpServletRequestBuilder getRequest = get("/games/123456").contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gamePin", is(game.getGamePin())))
                .andExpect(jsonPath("$.status", is(game.getStatus().toString())));
                //.andExpect(jsonPath("$.currentQuestion", is(null)));
    }

    @Test
    public void getGameByPin_returnsGame_WithCorrectAnswer() throws Exception {
        // given
        AnswerOption testCorrectAnswer = new AnswerOption();
        testCorrectAnswer.setAnswerOptionId(999L);
        testCorrectAnswer.setAnswerOptionText("correct answer");

        QuizQuestion testQuestion = new QuizQuestion();
        testQuestion.setQuestionStatus(CompletionStatus.FINISHED);
        testQuestion.setCorrectAnswer(testCorrectAnswer);

        Game game = new Game();
        game.setGamePin("123456");
        game.setStatus(GameStatus.LOBBY);
        game.setCurrentQuestion(testQuestion);

        given(gameService.getGameByPin(Mockito.anyString())).willReturn(game);

        // when
        MockHttpServletRequestBuilder getRequest = get("/games/123456").contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gamePin", is(game.getGamePin())))
                .andExpect(jsonPath("$.status", is(game.getStatus().toString())))
                .andExpect(jsonPath("$.currentQuestion.quizQuestionText", is(testQuestion.getQuizQuestionText())))
                .andExpect(jsonPath("$.currentQuestion.correctAnswer.answerOptionText", is(testCorrectAnswer.getAnswerOptionText())));
    }

    @Test
    public void getGameByPin_returnsGame_HideCorrectAnswer() throws Exception {
        // given
        AnswerOption testCorrectAnswer = new AnswerOption();
        testCorrectAnswer.setAnswerOptionId(999L);
        testCorrectAnswer.setAnswerOptionText("correct answer");

        QuizQuestion testQuestion = new QuizQuestion();
        testQuestion.setQuestionStatus(CompletionStatus.NOT_FINISHED);
        testQuestion.setCorrectAnswer(testCorrectAnswer);

        Game game = new Game();
        game.setGamePin("123456");
        game.setStatus(GameStatus.LOBBY);
        game.setCurrentQuestion(testQuestion);

        given(gameService.getGameByPin(Mockito.anyString())).willReturn(game);

        // when
        MockHttpServletRequestBuilder getRequest = get("/games/123456").contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gamePin", is(game.getGamePin())))
                .andExpect(jsonPath("$.status", is(game.getStatus().toString())))
                .andExpect(jsonPath("$.currentQuestion.quizQuestionText", is(testQuestion.getQuizQuestionText())));
                //.andExpect(jsonPath("$.currentQuestion.correctAnswer", is(null)));
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
        MockHttpServletRequestBuilder putRequest = put("/games/123456")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gamePutDTO))
                .header("playerToken", "1");

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isUnauthorized());
    }
    @Test
    public void requestNextQuizQuestion_success() throws Exception {
        Game game = new Game();
        game.setGamePin("123456");
        game.setStatus(GameStatus.QUIZ);

        given(gameService.changeToNextQuestion(Mockito.anyString(), Mockito.anyString())).willReturn(game);

        MockHttpServletRequestBuilder putRequest = put("/games/123456/quizQuestions")
                .contentType(MediaType.APPLICATION_JSON)
                .header("playerToken", "1");

        mockMvc.perform(putRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gamePin", is(game.getGamePin())))
                .andExpect(jsonPath("$.status", is(game.getStatus().toString())));
    }

    @Test
    public void requestNextQuizQuestion_notByHost() throws Exception {
        Game game = new Game();
        game.setGamePin("123456");
        game.setStatus(GameStatus.QUIZ);

        given(gameService.changeToNextQuestion(Mockito.anyString(), Mockito.anyString())).willThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        MockHttpServletRequestBuilder putRequest = put("/games/123456/quizQuestions")
                .contentType(MediaType.APPLICATION_JSON)
                .header("playerToken", "1");

        mockMvc.perform(putRequest)
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