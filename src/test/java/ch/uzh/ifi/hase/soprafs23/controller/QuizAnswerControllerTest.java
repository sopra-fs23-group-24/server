package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.quiz.QuizAnswer;
import ch.uzh.ifi.hase.soprafs23.entity.quiz.QuizQuestion;
import ch.uzh.ifi.hase.soprafs23.rest.dto.quiz.QuizAnswerPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.quiz.QuizAnswerService;
import ch.uzh.ifi.hase.soprafs23.service.quiz.QuizQuestionService;
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
    }
    @Test
    public void postQuizAnswer_success() throws Exception {
        // dto
        QuizAnswerPostDTO quizAnswerPostDTO = new QuizAnswerPostDTO();
        quizAnswerPostDTO.setPickedAnswerOptionId(1L);

        // desired QuizAnswer
        QuizAnswer quizAnswer = new QuizAnswer();
        quizAnswer.setPickedAnswerOptionId(1L);
        // not really needed here, could be in QuizAnswerTest...
        quizAnswer.setQuizAnswerId(2L);
        quizAnswer.setAssociatedPlayer(testPlayer);
        //set
        String playerToken = "TESTTOKEN";

        // quizQuestion we want to get
        QuizQuestion quizQuestion = new QuizQuestion();
        // set quizAnswer
        quizQuestion.addReceivedAnswer(quizAnswer);

        //
        given(quizAnswerService.addQuizAnswerToQuizQuestion(Mockito.any(), Mockito.anyLong(),
                Mockito.anyString(), Mockito.anyString()))
                .willReturn(quizQuestion);

        // when
        MockHttpServletRequestBuilder postRequest = post("/games/123456/quiz-questions/80/answers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quizAnswerPostDTO))
                .header("playerToken", playerToken);
        // TODO: what about the PathVariables?

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$", is(0)));

    }

    @Test
    public void postQuizAnswer_failureOfSomeKind(){
        //TODO: test
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