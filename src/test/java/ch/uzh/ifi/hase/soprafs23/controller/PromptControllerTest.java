package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.PromptType;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.Prompt;
import ch.uzh.ifi.hase.soprafs23.rest.dto.prompt.PromptPostDTO;
import ch.uzh.ifi.hase.soprafs23.service.prompt.PromptService;
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

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PromptController.class)
class PromptControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PromptService promptService;

    private Prompt textPrompt;
    private Prompt tfPrompt;
    private Prompt drawPrompt;

    @BeforeEach
    void setup() {
        textPrompt = new Prompt();
        textPrompt.setPromptNr(900);
        textPrompt.setPromptType(PromptType.TEXT);
        textPrompt.setPromptText("Example text 1");

        tfPrompt = new Prompt();
        tfPrompt.setPromptNr(901);
        tfPrompt.setPromptType(PromptType.TRUEFALSE);
        tfPrompt.setPromptText("Example text 2");

        drawPrompt = new Prompt();
        drawPrompt.setPromptNr(902);
        drawPrompt.setPromptType(PromptType.DRAWING);
        drawPrompt.setPromptText("Example text 3");
    }

    @Test
    void setPromptsForGame_returnsPromptDTOs() throws Exception {
        PromptPostDTO promptPostDTO = new PromptPostDTO();
        promptPostDTO.setDrawingNr(1);
        promptPostDTO.setTextNr(1);
        promptPostDTO.setTrueFalseNr(1);

        List<Prompt> pickedList = List.of(textPrompt, tfPrompt, drawPrompt);

        given(promptService.pickPrompts(Mockito.any(), Mockito.anyString())).willReturn(pickedList);

        MockHttpServletRequestBuilder postRequest = post("/games/123456/prompts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(promptPostDTO));

        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].promptNr", is(textPrompt.getPromptNr())))
                .andExpect(jsonPath("$[1].promptNr", is(tfPrompt.getPromptNr())))
                .andExpect(jsonPath("$[2].promptNr", is(drawPrompt.getPromptNr())))
                .andExpect(jsonPath("$[0].promptType", is(textPrompt.getPromptType().toString())))
                .andExpect(jsonPath("$[1].promptType", is(tfPrompt.getPromptType().toString())))
                .andExpect(jsonPath("$[2].promptType", is(drawPrompt.getPromptType().toString())))
                .andExpect(jsonPath("$[0].promptText", is(textPrompt.getPromptText())))
                .andExpect(jsonPath("$[1].promptText", is(tfPrompt.getPromptText())))
                .andExpect(jsonPath("$[2].promptText", is(drawPrompt.getPromptText())));
    }

    @Test
    void setPrompts_exceedAvailableNumberOrWrongState() throws Exception {
        PromptPostDTO promptPostDTO = new PromptPostDTO();
        promptPostDTO.setDrawingNr(10);
        promptPostDTO.setTextNr(1);
        promptPostDTO.setTrueFalseNr(1);

        given(promptService.pickPrompts(Mockito.any(), Mockito.anyString())).willThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));

        MockHttpServletRequestBuilder postRequest = post("/games/123456/prompts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(promptPostDTO));

        mockMvc.perform(postRequest)
                .andExpect(status().isBadRequest());
    }

    @Test
    void setPrompts_invalidGamePin() throws Exception {
        PromptPostDTO promptPostDTO = new PromptPostDTO();
        promptPostDTO.setDrawingNr(10);
        promptPostDTO.setTextNr(1);
        promptPostDTO.setTrueFalseNr(1);

        given(promptService.pickPrompts(Mockito.any(), Mockito.anyString())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        MockHttpServletRequestBuilder postRequest = post("/games/123456/prompts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(promptPostDTO));

        mockMvc.perform(postRequest)
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllPrompts_returnsPromptDTOs() throws Exception {
        PromptPostDTO promptPostDTO = new PromptPostDTO();
        promptPostDTO.setDrawingNr(1);
        promptPostDTO.setTextNr(1);
        promptPostDTO.setTrueFalseNr(1);

        List<Prompt> pickedList = List.of(textPrompt, tfPrompt, drawPrompt);

        given(promptService.getPrompts()).willReturn(pickedList);

        MockHttpServletRequestBuilder getRequest = get("/prompts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(promptPostDTO));

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].promptNr", is(textPrompt.getPromptNr())))
                .andExpect(jsonPath("$[1].promptNr", is(tfPrompt.getPromptNr())))
                .andExpect(jsonPath("$[2].promptNr", is(drawPrompt.getPromptNr())))
                .andExpect(jsonPath("$[0].promptType", is(textPrompt.getPromptType().toString())))
                .andExpect(jsonPath("$[1].promptType", is(tfPrompt.getPromptType().toString())))
                .andExpect(jsonPath("$[2].promptType", is(drawPrompt.getPromptType().toString())))
                .andExpect(jsonPath("$[0].promptText", is(textPrompt.getPromptText())))
                .andExpect(jsonPath("$[1].promptText", is(tfPrompt.getPromptText())))
                .andExpect(jsonPath("$[2].promptText", is(drawPrompt.getPromptText())));
    }

    @Test
    void getPromptsOfGame_returnsPromptsOfGame() throws Exception {
        Prompt prompt = new Prompt();
        prompt.setPromptNr(999);
        prompt.setPromptType(PromptType.TEXT);
        prompt.setPromptText("example prompt");
        prompt.setPromptId(999L);


        List<Prompt> allPrompts = Collections.singletonList(prompt);

        given(promptService.getPromptsOfGame(Mockito.anyString())).willReturn(allPrompts);

        MockHttpServletRequestBuilder getRequest = get("/games/123456/prompts").contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].promptNr", is(prompt.getPromptNr())))
                .andExpect(jsonPath("$[0].promptType", is(prompt.getPromptType().toString())))
                .andExpect(jsonPath("$[0].promptText", is(prompt.getPromptText())));
    }

    @Test
    void getPromptsOfGame_invalidGamePin() throws Exception {

        given(promptService.getPromptsOfGame(Mockito.anyString())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        MockHttpServletRequestBuilder getRequest = get("/games/123456/prompts").contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest)
                .andExpect(status().isNotFound());
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