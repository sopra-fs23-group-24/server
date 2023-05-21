package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.quiz.QuizAnswer;
import ch.uzh.ifi.hase.soprafs23.entity.quiz.QuizQuestion;
import ch.uzh.ifi.hase.soprafs23.rest.dto.quiz.QuizAnswerPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.quiz.QuizAnswerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class QuizAnswerController {

    private final QuizAnswerService quizAnswerService;

    QuizAnswerController(QuizAnswerService quizAnswerService) {
        this.quizAnswerService = quizAnswerService;
    }


    @PostMapping("/games/{pin}/quiz-questions/{id}/answers")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ResponseBody
    public int postQuizAnswer(@RequestBody QuizAnswerPostDTO clientAnswer,
                              @RequestHeader("playerToken") String loggedInToken,
                              @PathVariable("pin") String gamePin,
                              @PathVariable("id") long questionId) {

        //convert from DTO to QuizAnswer
        QuizAnswer quizAnswer = DTOMapper.INSTANCE.convertFromQuizAnswerPostDTO(clientAnswer);

        Game gameByPin = quizAnswerService.findGameByPin(gamePin);
        QuizQuestion question = quizAnswerService.findQuestionById(questionId);

        // adds answer to question
        QuizAnswer addedQuizAnswer = quizAnswerService.addQuizAnswerToQuizQuestion(quizAnswer, question, loggedInToken);

        //check if all players have answered question
        QuizQuestion updatedQuestion = quizAnswerService.updateQuestionStatusIfAllAnswered(gameByPin, question);

        // checks if the answer is correct and if so (calculates) and adds the points to the player
        return quizAnswerService.calculateAndAddScore(addedQuizAnswer, updatedQuestion, gameByPin);
    }


}
