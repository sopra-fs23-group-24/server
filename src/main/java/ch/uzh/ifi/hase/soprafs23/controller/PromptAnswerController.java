package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.TextPromptAnswer;
import ch.uzh.ifi.hase.soprafs23.entity.TrueFalsePromptAnswer;
import ch.uzh.ifi.hase.soprafs23.repository.TextPromptAnswerRepository;
import ch.uzh.ifi.hase.soprafs23.rest.dto.TextPromptAnswerPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.TrueFalsePromptAnswerPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.PromptAnswerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;


@RestController
public class PromptAnswerController {

    private final PromptAnswerService promptAnswerService;

    PromptAnswerController(PromptAnswerService promptAnswerService) {
        this.promptAnswerService = promptAnswerService;
    }




    // currently the playerToken header is not needed, / used, would only be needed for authentication I think
    // I added the playerToken as an HTTP Header, but I don't know where...
    // the game pin is needed for the {pin} part as far as I know
    @PostMapping("/games/{pin}/prompt-answers/text")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public TextPromptAnswer postTextPromptAnswer(@RequestBody TextPromptAnswerPostDTO clientAnswer,
                                                 @RequestHeader("playerToken") String loggedInToken,
                                                 @PathVariable("pin") String gamePin) {

        // do we need checks before the conversion?
        TextPromptAnswer answer = DTOMapper.INSTANCE.convertFromTextPromptAnswerPostDTO(clientAnswer);

        // any further checks should probably be in the service
        // this could be left out, if we change the Service method to return type void...
        Boolean successfulSave = promptAnswerService.saveTextPromptAnswer(answer);
        System.out.printf("TextPromptAnswer saved = %s", successfulSave);

        // does it need to return sth...? - at the moment it returns the converted answer itself...
        return answer;
    }



    @PostMapping("/games/{pin}/prompt-answers/tf")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public TrueFalsePromptAnswer postTrueFalsePromptAnswer(@RequestBody TrueFalsePromptAnswerPostDTO clientAnswer,
                                                           @RequestHeader("playerToken") String loggedInToken,
                                                           @PathVariable("pin") String gamePin) {

        // do we need checks before the conversion?
        TrueFalsePromptAnswer answer = DTOMapper.INSTANCE.convertFromTrueFalsePromptAnswerPostDTO(clientAnswer);

        // any further checks should probably be in the service
        // this could be left out, if we change the Service method to return type void...
        Boolean successfulSave = promptAnswerService.saveTrueFalsePromptAnswer(answer);
        System.out.printf("TrueFalseAnswer saved = %s", successfulSave);

        // does it need to return sth...? - at the moment it returns the converted answer itself...
        return answer;
    }


/*
        @PostMapping("/games/{pin}/prompt-answers/drawing")
        @ResponseStatus(HttpStatus.CREATED)
        @ResponseBody

 */


}
