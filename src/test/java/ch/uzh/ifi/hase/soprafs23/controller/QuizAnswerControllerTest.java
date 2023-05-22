package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.quiz.QuizAnswer;
import ch.uzh.ifi.hase.soprafs23.entity.quiz.QuizQuestion;
import ch.uzh.ifi.hase.soprafs23.rest.dto.quiz.QuizAnswerPostDTO;
import ch.uzh.ifi.hase.soprafs23.service.quiz.QuizAnswerService;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QuizAnswerController.class)
class QuizAnswerControllerTest {
    Player testPlayer = new Player();
    QuizAnswerPostDTO quizAnswerPostDTO;
    QuizAnswer quizAnswer;

    QuizQuestion quizQuestion;
    String playerToken = "TESTTOKEN";
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QuizAnswerService quizAnswerService;

    @BeforeEach
    public void setup() {
        testPlayer.setPlayerName("test");
        testPlayer.setAssociatedGamePin("123456");
        testPlayer.setHost(true);
        testPlayer.setToken("1");
        testPlayer.setPlayerId(1L);

        quizAnswerPostDTO = new QuizAnswerPostDTO();
        quizAnswerPostDTO.setPickedAnswerOptionId(1L);

        quizAnswer = new QuizAnswer();
        quizAnswer.setPickedAnswerOptionId(1L);
        quizAnswer.setQuizAnswerId(2L);
        quizAnswer.setAssociatedPlayer(testPlayer);

        quizQuestion = new QuizQuestion();
        quizQuestion.addReceivedAnswer(quizAnswer);

        given(quizAnswerService.findGameByPin(Mockito.anyString())).willReturn(new Game());
        given(quizAnswerService.findQuestionById(Mockito.anyLong())).willReturn(quizQuestion);
        given(quizAnswerService.addQuizAnswerToQuizQuestion(Mockito.any(), Mockito.any(), Mockito.anyString())).willReturn(quizAnswer);
        given(quizAnswerService.updateQuestionStatusIfAllAnswered(Mockito.any(), Mockito.any())).willReturn(quizQuestion);
        given(quizAnswerService.calculateAndAddScore(Mockito.any(), Mockito.any(), Mockito.any())).willReturn(10);
    }

    @Test
    public void postQuizAnswer_success() throws Exception {
        MockHttpServletRequestBuilder postRequest = post("/games/123456/quiz-questions/80/answers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quizAnswerPostDTO))
                .header("playerToken", playerToken);

        mockMvc.perform(postRequest)
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$", is(10)));
    }

    @Test
    public void postQuizAnswer_GameNotFound() throws Exception {
        given(quizAnswerService.findGameByPin(Mockito.anyString())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        MockHttpServletRequestBuilder postRequest = post("/games/123456/quiz-questions/80/answers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quizAnswerPostDTO))
                .header("playerToken", playerToken);

        mockMvc.perform(postRequest)
                .andExpect(status().isNotFound());
    }

    @Test
    public void postQuizAnswer_QuestionNotFound() throws Exception {
        given(quizAnswerService.findQuestionById(Mockito.anyLong())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        MockHttpServletRequestBuilder postRequest = post("/games/123456/quiz-questions/80/answers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quizAnswerPostDTO))
                .header("playerToken", playerToken);

        mockMvc.perform(postRequest)
                .andExpect(status().isNotFound());
    }

    @Test
    public void postQuizAnswer_PlayerNotFound() throws Exception {
        given(quizAnswerService.addQuizAnswerToQuizQuestion(Mockito.any(), Mockito.any(), Mockito.anyString())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        MockHttpServletRequestBuilder postRequest = post("/games/123456/quiz-questions/80/answers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quizAnswerPostDTO))
                .header("playerToken", playerToken);

        mockMvc.perform(postRequest)
                .andExpect(status().isNotFound());
    }

    @Test
    public void postQuizAnswer_PlayerAlreadyGaveAnswer() throws Exception {
        given(quizAnswerService.addQuizAnswerToQuizQuestion(Mockito.any(), Mockito.any(), Mockito.anyString())).willThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));

        MockHttpServletRequestBuilder postRequest = post("/games/123456/quiz-questions/80/answers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quizAnswerPostDTO))
                .header("playerToken", playerToken);

        mockMvc.perform(postRequest)
                .andExpect(status().isBadRequest());
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