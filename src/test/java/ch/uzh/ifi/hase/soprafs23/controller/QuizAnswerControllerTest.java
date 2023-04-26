package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.quiz.QuizAnswer;
import ch.uzh.ifi.hase.soprafs23.service.quiz.QuizAnswerService;
import ch.uzh.ifi.hase.soprafs23.service.quiz.QuizQuestionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(QuizAnswerController.class)
class QuizAnswerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QuizAnswerService quizAnswerService;

    @Test
    public void postQuizAnswer_success(){
        //TODO: test
    }

    @Test
    public void postQuizAnswer_failureOfSomeKind(){
        //TODO: test
    }

}