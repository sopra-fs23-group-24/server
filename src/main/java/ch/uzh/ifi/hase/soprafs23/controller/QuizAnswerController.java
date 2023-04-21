package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.quiz.QuizAnswer;
import ch.uzh.ifi.hase.soprafs23.rest.dto.quiz.QuizAnswerPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.quiz.QuizQuestionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class QuizAnswerController {

    private final QuizQuestionService quizQuestionService;

    // using a QuizQuestionService in the QuizAnswerController, bc there is no QuizAnswerService...
    QuizAnswerController(QuizQuestionService quizQuestionService) {
        this.quizQuestionService = quizQuestionService;
    }


    @PostMapping("/games/{pin}/gameQuestions/{id}/answers")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ResponseBody
    public int postQuizAnswer(@RequestBody QuizAnswerPostDTO clientAnswer,
                                            @PathVariable("pin") String gamePin,
                                            @PathVariable("id") long id) {
        // what do I need the gamePin for? maybe to get the player...

        //convert from DTO to QuizAnswer
        QuizAnswer quizAnswer = DTOMapper.INSTANCE.convertFromQuizAnswerPostDTO(clientAnswer);
        int score = quizQuestionService.calculateAndAddScore(quizAnswer, id);

        // what should it return?
        return score;
    }

}
