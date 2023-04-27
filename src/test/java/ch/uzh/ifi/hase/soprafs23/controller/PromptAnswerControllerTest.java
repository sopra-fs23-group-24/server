package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.prompt.DrawingPromptAnswer;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.TextPromptAnswer;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.TrueFalsePromptAnswer;
import ch.uzh.ifi.hase.soprafs23.rest.dto.prompt.DrawingPromptAnswerPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.prompt.TextPromptAnswerPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.prompt.TrueFalsePromptAnswerPostDTO;
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

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PromptAnswerController.class)
class PromptAnswerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PromptAnswerService promptAnswerService;


    @Test
    void postTextPromptAnswer_success() throws Exception {

        // create DTO
        TextPromptAnswerPostDTO testDto = new TextPromptAnswerPostDTO();
        testDto.setAssociatedPromptNr(0);
        testDto.setAnswer("Test");

        // the desired answer which service should create
        TextPromptAnswer textPromptAnswer = new TextPromptAnswer();
        textPromptAnswer.setAssociatedPromptNr(0);
        textPromptAnswer.setAnswer("Test");

        textPromptAnswer.setAssociatedPlayerId(2L);
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
                .andExpect(jsonPath("$.associatedPromptNr", is(textPromptAnswer.getAssociatedPromptNr())))
                .andExpect(jsonPath("$.answer", is(testDto.getAnswer())));
    }


    // test canChangeToQUiz
    @Test
    void testPostTrueFalsePromptAnswer_success() throws Exception {
        // create DTO
        TrueFalsePromptAnswerPostDTO testDto = new TrueFalsePromptAnswerPostDTO();
        testDto.setAssociatedPromptNr(0);
        testDto.setAnswerBoolean(true);
        testDto.setAnswerText("Test");

        // the desired answer which service should create
        TrueFalsePromptAnswer trueFalsePromptAnswer = new TrueFalsePromptAnswer();
        trueFalsePromptAnswer.setAssociatedPromptNr(0);
        trueFalsePromptAnswer.setAnswerBoolean(true);
        trueFalsePromptAnswer.setAnswerText("Test");
        // is this necessary, as it will not be tested here.
        trueFalsePromptAnswer.setAssociatedPlayerId(2L);
        trueFalsePromptAnswer.setAssociatedGamePin("123456");

        String playerToken = "TESTTOKEN";

        given(promptAnswerService.saveTrueFalsePromptAnswer(Mockito.any(), Mockito.anyString(), Mockito.anyString()))
                .willReturn(trueFalsePromptAnswer);

        // when
        MockHttpServletRequestBuilder postRequest = post("/games/123456/prompt-answers/tf")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(testDto))
                .header("playerToken", playerToken);

        // then
        // throws Exception
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.associatedPromptNr", is(trueFalsePromptAnswer.getAssociatedPromptNr())))
                .andExpect(jsonPath("$.answerBoolean", is(testDto.getAnswerBoolean())))
                .andExpect(jsonPath("$.answerText", is(testDto.getAnswerText())));
    }

    @Test
    void postDrawingPromptAnswer_success() throws Exception {
        // create DTO
        DrawingPromptAnswerPostDTO testDto = new DrawingPromptAnswerPostDTO();
        testDto.setAssociatedPromptNr(0);
        testDto.setAnswerDrawing("TestDrawing");

        // the desired answer which service should create
        DrawingPromptAnswer drawingPromptAnswer = new DrawingPromptAnswer();
        drawingPromptAnswer.setAssociatedPromptNr(0);
        drawingPromptAnswer.setAnswerDrawing("TestDrawing");

        drawingPromptAnswer.setAssociatedPlayerId(2L);
        drawingPromptAnswer.setAssociatedGamePin("123456");

        String playerToken = "TESTTOKEN";

        given(promptAnswerService.saveDrawingPromptAnswer(Mockito.any(), Mockito.anyString(), Mockito.anyString()))
                .willReturn(drawingPromptAnswer);

        // when
        MockHttpServletRequestBuilder postRequest = post("/games/123456/prompt-answers/drawing")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(testDto))
                .header("playerToken", playerToken);

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.associatedPromptNr", is(drawingPromptAnswer.getAssociatedPromptNr())))
                .andExpect(jsonPath("$.answerDrawing", is(testDto.getAnswerDrawing())));

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
