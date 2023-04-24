package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.quiz.QuizQuestion;
import ch.uzh.ifi.hase.soprafs23.rest.dto.quiz.QuizQuestionGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.quiz.QuizQuestionService;
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

    @GetMapping("/games/{pin}/quizQuestions")
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

    //currently only for testing, should be done automatically when all prompts are filled out
    @PostMapping("/games/{pin}/quizQuestions")
    @ResponseStatus(HttpStatus.CREATED)
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
