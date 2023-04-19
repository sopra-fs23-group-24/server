package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.prompt.TextPromptAnswer;
import ch.uzh.ifi.hase.soprafs23.service.prompt.PromptAnswerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(PromptAnswerController.class)
class PromptAnswerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PromptAnswerService promptAnswerService;


    @Test
    void testPostTextPromptAnswer() throws Exception {
        // given
        TextPromptAnswer textPromptAnswer = new TextPromptAnswer();

        textPromptAnswer.setAssociatedPromptNr(0);
        textPromptAnswer.setAssociatedPlayerId(1L);
        textPromptAnswer.setAnswer("Test");
        String gamePin = "123";
        textPromptAnswer.setAssociatedGamePin(gamePin);

        // maybe I need to add the gamePin to the textPromptAnswer as well...
        String playerToken = "TESTTOKEN-1";

        // does this get executed
        given(promptAnswerService.saveTextPromptAnswer(textPromptAnswer, playerToken, gamePin))
                .willReturn(textPromptAnswer);

        // when
        MockHttpServletRequestBuilder postRequest = post("/games/123/prompt-answers/text")
                .contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(postRequest);
            // does nothing yet

    }

    @Test
    void testPostTrueFalsePromptAnswer() {
    }

    @Test
    void postDrawingPromptAnswer() {
    }
}