package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.prompt.Prompt;
import ch.uzh.ifi.hase.soprafs23.rest.dto.prompt.PromptGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.prompt.PromptPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.prompt.PromptService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class PromptController {
    private final PromptService promptService;

    PromptController(PromptService promptService) {
        this.promptService = promptService;
    }

    @GetMapping("/prompts")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<PromptGetDTO> getAllPrompts() {
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
    public List<PromptGetDTO> setPromptsAndTimerForGame(@RequestBody PromptPostDTO promptPostDTO, @PathVariable("pin") String gamePin) {

        // add prompts to game
        List<Prompt> allPrompts = promptService.pickPrompts(promptPostDTO, gamePin);
        // add timer to game
        promptService.addTimerToGame(promptPostDTO, gamePin);

        List<PromptGetDTO> promptsGetDTOs = new ArrayList<>();

        for (Prompt prompt : allPrompts) {
            promptsGetDTOs.add(DTOMapper.INSTANCE.convertToPromptGetDTO(prompt));
        }

        return promptsGetDTOs;
    }


}
