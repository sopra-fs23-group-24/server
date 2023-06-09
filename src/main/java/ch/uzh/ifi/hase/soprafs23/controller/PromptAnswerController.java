package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.prompt.DrawingPromptAnswer;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.TextPromptAnswer;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.TrueFalsePromptAnswer;
import ch.uzh.ifi.hase.soprafs23.rest.dto.prompt.DrawingPromptAnswerPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.prompt.TextPromptAnswerPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.prompt.TrueFalsePromptAnswerPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.prompt.PromptAnswerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
public class PromptAnswerController {

    private final PromptAnswerService promptAnswerService;

    PromptAnswerController(PromptAnswerService promptAnswerService) {
        this.promptAnswerService = promptAnswerService;
    }

    @PostMapping("/games/{pin}/prompt-answers/text")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public TextPromptAnswer postTextPromptAnswer(@RequestBody TextPromptAnswerPostDTO clientAnswer,
                                                 @RequestHeader("playerToken") String loggedInToken,
                                                 @PathVariable("pin") String gamePin) {
        // convert
        TextPromptAnswer answer = DTOMapper.INSTANCE.convertFromTextPromptAnswerPostDTO(clientAnswer);
        // save
        TextPromptAnswer successfulSave = promptAnswerService.saveTextPromptAnswer(answer, loggedInToken, gamePin);
        // "game controller" method
        promptAnswerService.changeFromPromptAnsweringToQuizStage(gamePin);

        return successfulSave;
    }

    @PostMapping("/games/{pin}/prompt-answers/tf")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public TrueFalsePromptAnswer postTrueFalsePromptAnswer(@RequestBody TrueFalsePromptAnswerPostDTO clientAnswer,
                                                           @RequestHeader("playerToken") String loggedInToken,
                                                           @PathVariable("pin") String gamePin) {
        // convert
        TrueFalsePromptAnswer answer = DTOMapper.INSTANCE.convertFromTrueFalsePromptAnswerPostDTO(clientAnswer);
        // save
        TrueFalsePromptAnswer successfulSave = promptAnswerService.saveTrueFalsePromptAnswer(answer, loggedInToken, gamePin);
        // "game controller" method
        promptAnswerService.changeFromPromptAnsweringToQuizStage(gamePin);
        return successfulSave;
    }


    @PostMapping("/games/{pin}/prompt-answers/drawing")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public DrawingPromptAnswer postDrawingPromptAnswer(@RequestBody DrawingPromptAnswerPostDTO clientAnswer,
                                                       @RequestHeader("playerToken") String loggedInToken,
                                                       @PathVariable("pin") String gamePin) {
        // convert
        DrawingPromptAnswer answer = DTOMapper.INSTANCE.convertFromDrawingPromptAnswerPostDTO(clientAnswer);
        // save
        DrawingPromptAnswer successfulSave = promptAnswerService.saveDrawingPromptAnswer(answer, loggedInToken, gamePin);
        // "game controller" method
        promptAnswerService.changeFromPromptAnsweringToQuizStage(gamePin);

        return successfulSave;
    }

}
