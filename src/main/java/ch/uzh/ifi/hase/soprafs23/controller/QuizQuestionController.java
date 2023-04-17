package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.Prompt;
import ch.uzh.ifi.hase.soprafs23.entity.QuizQuestion;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PromptGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PromptPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.QuizQuestionGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.PromptService;
import ch.uzh.ifi.hase.soprafs23.service.QuizQuestionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class QuizQuestionController {
    //headers: @RequestHeader("playerToken") String loggedInToken, @PathVariable ("pin") String gamePin
    /*
      System.out.println("Received PlayerToken: " + loggedInToken);
      System.out.println("Received GamePin: " + gamePin);
    */
    private final QuizQuestionService quizQuestionService;

    QuizQuestionController(QuizQuestionService quizQuestionService) {
        this.quizQuestionService = quizQuestionService;
    }

    @GetMapping("/quizQuestions")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<QuizQuestionGetDTO> getAllQuizQuestions(){
        List<QuizQuestion> allQuizQuestions = quizQuestionService.getQuizQuestions();

        List<QuizQuestionGetDTO> quizQuestionGetDTOS = new ArrayList<>();

        for (QuizQuestion question : allQuizQuestions) {
            quizQuestionGetDTOS.add(DTOMapper.INSTANCE.convertToQuizQuestionGetDTO(question));
        }

        return quizQuestionGetDTOS;
    }

    @GetMapping("/games/{pin}/gameQuestions")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<QuizQuestionGetDTO> getAllQuizQuestions(@PathVariable("pin") String gamePin){
        List<QuizQuestion> allQuizQuestions = quizQuestionService.getQuizQuestionsOfGame(gamePin);

        List<QuizQuestionGetDTO> quizQuestionGetDTOS = new ArrayList<>();

        for (QuizQuestion question : allQuizQuestions) {
            quizQuestionGetDTOS.add(DTOMapper.INSTANCE.convertToQuizQuestionGetDTO(question));
        }

        return quizQuestionGetDTOS;
    }

    @PostMapping("/games/{pin}/gameQuestions")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<QuizQuestionGetDTO> createQuizQuestions(@PathVariable("pin") String gamePin){
        List<QuizQuestion> allQuizQuestions = quizQuestionService.createQuizQuestions(gamePin);

        List<QuizQuestionGetDTO> quizQuestionGetDTOS = new ArrayList<>();

        for (QuizQuestion question : allQuizQuestions) {
            quizQuestionGetDTOS.add(DTOMapper.INSTANCE.convertToQuizQuestionGetDTO(question));
        }

        return quizQuestionGetDTOS;
    }

}
