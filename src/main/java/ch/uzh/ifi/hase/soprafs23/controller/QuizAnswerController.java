package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.quiz.QuizAnswer;
import ch.uzh.ifi.hase.soprafs23.rest.dto.quiz.QuizAnswerPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.quiz.QuizAnswerService;
import ch.uzh.ifi.hase.soprafs23.service.quiz.QuizQuestionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class QuizAnswerController {

    private final QuizAnswerService quizAnswerService;

    QuizAnswerController(QuizAnswerService quizAnswerService) {
            this.quizAnswerService = quizAnswerService;
    }


    @PostMapping("/games/{pin}/gameQuestions/{id}/answers")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ResponseBody
    public int postQuizAnswer(@RequestBody QuizAnswerPostDTO clientAnswer,
                              @RequestHeader("playerToken") String loggedInToken,
                              @PathVariable("pin") String gamePin,
                              @PathVariable("id") long questionId) {

        //convert from DTO to QuizAnswer
        QuizAnswer quizAnswer = DTOMapper.INSTANCE.convertFromQuizAnswerPostDTO(clientAnswer);

        // checks if the answer is correct and if so (calculates) and adds the points to the player
        int score = quizAnswerService.calculateAndAddScore(loggedInToken, quizAnswer, questionId);

        // TODO: move it out to different methods in the service.
        //  not calling all of them here - but one call to e.g. processQuizAnswer
        //  e.g. with calls to: calculateScore, addQuizAnswerToQuizQuestion, isAnswered?

        // adds answer to question and check if Question is answered by everyone.
        quizAnswerService.addQuizAnswerToQuizQuestion(quizAnswer, questionId, gamePin);

        // what should it return?
        return score;
    }

    // more mappings?

}
