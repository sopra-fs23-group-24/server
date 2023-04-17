package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.Prompt;
import ch.uzh.ifi.hase.soprafs23.rest.dto.*;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.PromptService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class PromptController {
    //headers: @RequestHeader("playerToken") String loggedInToken, @PathVariable ("pin") String gamePin
    /*
      System.out.println("Received PlayerToken: " + loggedInToken);
      System.out.println("Received GamePin: " + gamePin);
    */
    private final PromptService promptService;

    PromptController(PromptService promptService) {
        this.promptService = promptService;
    }

    @GetMapping("/prompts")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<PromptGetDTO> getAllPrompts(){
        List<Prompt> allPrompts = promptService.getPrompts();

        List<PromptGetDTO> promptsGetDTOs = new ArrayList<>();

        for (Prompt prompt : allPrompts) {
            promptsGetDTOs.add(DTOMapper.INSTANCE.convertToPromptGetDTO(prompt));
        }

        return promptsGetDTOs;
    }

    @GetMapping("/games/{pin}/prompts")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<PromptGetDTO> getPromptsOfGame(@PathVariable("pin") String gamePin) {
        List<Prompt> allPromptsOfGame = promptService.getPromptsOfGame(gamePin);

        List<PromptGetDTO> promptsGetDTOs = new ArrayList<>();

        for (Prompt prompt : allPromptsOfGame) {
            promptsGetDTOs.add(DTOMapper.INSTANCE.convertToPromptGetDTO(prompt));
        }

        return promptsGetDTOs;
    }

    @PostMapping("/games/{pin}/prompts")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public List<PromptGetDTO> setPromptsForGame(@RequestBody PromptPostDTO promptPostDTO, @PathVariable ("pin") String gamePin){

        List<Prompt> allPrompts = promptService.pickPrompts(promptPostDTO, gamePin);

        List<PromptGetDTO> promptsGetDTOs = new ArrayList<>();

        for (Prompt prompt : allPrompts) {
            promptsGetDTOs.add(DTOMapper.INSTANCE.convertToPromptGetDTO(prompt));
        }

        return promptsGetDTOs;
    }


}
