package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.Prompt;
import ch.uzh.ifi.hase.soprafs23.rest.dto.*;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.PlayerService;
import ch.uzh.ifi.hase.soprafs23.service.PromptService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to
 * the user.
 * The controller will receive the request and delegate the execution to the
 * UserService and finally return the result.
 */
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


}
