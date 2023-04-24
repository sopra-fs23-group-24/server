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

import java.util.List;


@RestController
public class PromptAnswerController {

    private final PromptAnswerService promptAnswerService;

    PromptAnswerController(PromptAnswerService promptAnswerService) {
        this.promptAnswerService = promptAnswerService;
    }


    @GetMapping("prompt-answers/text")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<TextPromptAnswer> getAllTextPromptAnswers(){

        List<TextPromptAnswer> allTextPromptAnswers = promptAnswerService.getAllTextPromptAnswers();

        return allTextPromptAnswers;
    }

    @GetMapping("/games/{pin}/prompt-answers/text")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<TextPromptAnswer> getAllTextPromptAnswersOfGame(@PathVariable("pin") String gamePin){
        List<TextPromptAnswer> allTextPromptAnswers = promptAnswerService.getAllTextPromptAnswersByGamePin(gamePin);

        return allTextPromptAnswers;
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
        // convert
        TextPromptAnswer answer = DTOMapper.INSTANCE.convertFromTextPromptAnswerPostDTO(clientAnswer);
        // save
        TextPromptAnswer successfulSave = promptAnswerService.saveTextPromptAnswer(answer, loggedInToken, gamePin);
        System.out.print("TextPromptAnswer saved");
        // "game controller" method
        Boolean canChangeToQuizStage = promptAnswerService.changeFromPromptAnsweringToQuizStage(gamePin);
        System.out.printf("Game is changing to Quiz stage: %s", canChangeToQuizStage);

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
        System.out.println("answer: " + answer);
        // save
        TrueFalsePromptAnswer successfulSave = promptAnswerService.saveTrueFalsePromptAnswer(answer, loggedInToken, gamePin);
        System.out.print("TrueFalseAnswer saved");
        // "game controller" method
        Boolean canChangeToQuizStage = promptAnswerService.changeFromPromptAnsweringToQuizStage(gamePin);
        System.out.printf("Game is changing to Quiz stage: %s", canChangeToQuizStage);

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
        System.out.print("DrawingPromptAnswer saved");
        // "game controller" method
        Boolean canChangeToQuizStage = promptAnswerService.changeFromPromptAnsweringToQuizStage(gamePin);
        System.out.printf("Game is changing to Quiz stage: %s", canChangeToQuizStage);

        return successfulSave;
    }

    // other endpoints? - (maybe for our testing purposes, but not for the game...)

}
