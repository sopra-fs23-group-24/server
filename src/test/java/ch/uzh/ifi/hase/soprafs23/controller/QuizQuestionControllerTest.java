package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.service.quiz.QuizQuestionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(QuizQuestionController.class)
class QuizQuestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QuizQuestionService quizQuestionService;

    @Test
    public void getAllQuizQuestions_returnAllQuizQuestions(){
        //todo: test

    }

    @Test
    public void getAllQuiZQuestionsOfGame_returnsAllQuizQuestions(){
        //todo: test
    }

    @Test
    public void getAllQuiZQuestionsOfGame_invalidGamPin(){
        //todo: test
    }
}