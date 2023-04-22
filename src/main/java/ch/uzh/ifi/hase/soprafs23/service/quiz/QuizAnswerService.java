package ch.uzh.ifi.hase.soprafs23.service.quiz;

import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.quiz.AnswerOption;
import ch.uzh.ifi.hase.soprafs23.entity.quiz.QuizAnswer;
import ch.uzh.ifi.hase.soprafs23.entity.quiz.QuizQuestion;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.quiz.AnswerOptionRepository;
import ch.uzh.ifi.hase.soprafs23.repository.quiz.QuizQuestionRepository;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class QuizAnswerService {
    private final QuizQuestionRepository qqRepository;
    private final AnswerOptionRepository answerOptionRepository;
    private final PlayerRepository playerRepository;
    private GameService gameService;
    @Autowired
    public QuizAnswerService(@Qualifier("quizQuestionRepository") QuizQuestionRepository qqRepository,
                             @Qualifier("answerOptionRepository") AnswerOptionRepository answerOptionRepository,
                             @Qualifier("playerRepository") PlayerRepository playerRepository)  {
        this.qqRepository = qqRepository;
        this.answerOptionRepository = answerOptionRepository;
        this.playerRepository = playerRepository;
    }
    @Autowired
    private void setGameService(GameService gameService) {
        this.gameService = gameService;
    }


    // return value is never used
    public QuizQuestion addQuizAnswerToQuizQuestion(QuizAnswer quizAnswer, long quizQuestionId, String gamePin){
        QuizQuestion questionById = qqRepository.getOne(quizQuestionId);
        questionById.addReceivedAnswer(quizAnswer);

        Game gameByPin = gameService.getGameByPin(gamePin);
        //TODO: check if all players answered question, change question status - Do this in the QuizQuestionService

        return questionById;
    }


    // the notion of speed is not yet accounted for
    // TODO : Rename this
    public int calculateAndAddScore(String loggedInToken, QuizAnswer quizAnswer, long questionId) {
        // get the picked and the correct answer
        long pickedId = quizAnswer.getPickedAnswerOptionId();
        AnswerOption chosenAnswer = answerOptionRepository.getAnswerOptionByAnswerOptionId(pickedId);
        AnswerOption correctAnswer = qqRepository.getOne(questionId).getCorrectAnswer();
        // set the pl
        quizAnswer.setAssociatedPlayer(playerRepository.findByToken(loggedInToken));

        int score = 0;
        if (chosenAnswer.equals(correctAnswer)) {
            score = 10;
            // add points to player
            quizAnswer.getAssociatedPlayer().addPoints(score);
        }
        return score; // either 0 or 10

    }

}


