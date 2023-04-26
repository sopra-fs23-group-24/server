package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.prompt.TextPromptAnswer;
import ch.uzh.ifi.hase.soprafs23.rest.dto.prompt.TextPromptAnswerPostDTO;
import ch.uzh.ifi.hase.soprafs23.service.prompt.PromptAnswerService;
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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
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
    void testPostTextPromptAnswer_success() throws Exception {

        // create DTO
        TextPromptAnswerPostDTO testDto = new TextPromptAnswerPostDTO();
        // set...

        // the desired answer which service should create
        TextPromptAnswer textPromptAnswer = new TextPromptAnswer();
        textPromptAnswer.setAssociatedPromptNr(0);
        textPromptAnswer.setAssociatedPlayerId(2L);
        textPromptAnswer.setAnswer("Test");
        textPromptAnswer.setAssociatedGamePin("123456");

        String playerToken = "TESTTOKEN";

        given(promptAnswerService.saveTextPromptAnswer(Mockito.any(), Mockito.anyString(), Mockito.anyString()))
                .willReturn(textPromptAnswer);

        // when
        MockHttpServletRequestBuilder postRequest = post("/games/123456/prompt-answers/text")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(testDto))
                .header("playerToken", playerToken);

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.associatedPromptNr", is(textPromptAnswer.getAssociatedPromptNr())));
                //.andExpect(jsonPath("$.playerName", is(testPlayer.getPlayerName())))


    }


     // test canChangeToQUiz
    @Test
    void testPostTrueFalsePromptAnswer() {
    }

    @Test
    void postDrawingPromptAnswer() {
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
