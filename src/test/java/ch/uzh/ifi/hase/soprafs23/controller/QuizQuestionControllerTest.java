package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.CompletionStatus;
import ch.uzh.ifi.hase.soprafs23.entity.quiz.QuizQuestion;
import ch.uzh.ifi.hase.soprafs23.service.quiz.QuizQuestionService;
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

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QuizQuestionController.class)
class QuizQuestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QuizQuestionService quizQuestionService;

    private QuizQuestion exampleQuizQuestion;

    @BeforeEach
    public void setup() {
        exampleQuizQuestion = new QuizQuestion();
        exampleQuizQuestion.setQuestionStatus(CompletionStatus.NOT_FINISHED);
        exampleQuizQuestion.setQuizQuestionText("test question");
        exampleQuizQuestion.setAssociatedGamePin("123456");
    }

    @Test
    public void getAllQuizQuestions_returnAllQuizQuestions() throws Exception {
        List<QuizQuestion> allQuizQuestions = Collections.singletonList(exampleQuizQuestion);

        given(quizQuestionService.getQuizQuestions()).willReturn(allQuizQuestions);

        // when
        MockHttpServletRequestBuilder getRequest = get("/quizQuestions").contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].quizQuestionText", is(exampleQuizQuestion.getQuizQuestionText())))
                .andExpect(jsonPath("$[0].questionStatus", is(exampleQuizQuestion.getQuestionStatus().toString())));


    }

    @Test
    public void getAllQuiZQuestionsOfGame_returnsAllQuizQuestions() throws Exception {
        List<QuizQuestion> allQuizQuestions = Collections.singletonList(exampleQuizQuestion);

        given(quizQuestionService.getQuizQuestionsOfGame(Mockito.anyString())).willReturn(allQuizQuestions);

        // when
        MockHttpServletRequestBuilder getRequest = get("/games/123456/quizQuestions").contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].quizQuestionText", is(exampleQuizQuestion.getQuizQuestionText())))
                .andExpect(jsonPath("$[0].questionStatus", is(exampleQuizQuestion.getQuestionStatus().toString())));

    }

    @Test
    public void getAllQuiZQuestionsOfGame_invalidGamPin() throws Exception {
        given(quizQuestionService.getQuizQuestionsOfGame(Mockito.anyString())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        // when
        MockHttpServletRequestBuilder getRequest = get("/games/654321").contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isNotFound());
    }
}