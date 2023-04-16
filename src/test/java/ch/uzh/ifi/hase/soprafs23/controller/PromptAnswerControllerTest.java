package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.TextPromptAnswer;
import ch.uzh.ifi.hase.soprafs23.rest.dto.GamePutDTO;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.PromptAnswerService;
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