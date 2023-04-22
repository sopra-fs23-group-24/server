package ch.uzh.ifi.hase.soprafs23.service.quiz;

import ch.uzh.ifi.hase.soprafs23.entity.quiz.AnswerOption;
import ch.uzh.ifi.hase.soprafs23.entity.quiz.QuizAnswer;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.quiz.AnswerOptionRepository;
import ch.uzh.ifi.hase.soprafs23.repository.quiz.QuizQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class QuizAnswerService {
    private final QuizQuestionRepository qqRepository;
    private final AnswerOptionRepository answerOptionRepository;
    private final PlayerRepository playerRepository;
    @Autowired
    public QuizAnswerService(@Qualifier("quizQuestionRepository") QuizQuestionRepository qqRepository,
                             @Qualifier("answerOptionRepository") AnswerOptionRepository answerOptionRepository,
                             @Qualifier("playerRepository") PlayerRepository playerRepository)  {
        this.qqRepository = qqRepository;
        this.answerOptionRepository = answerOptionRepository;
        this.playerRepository = playerRepository;
    }



    // the notion of speed is not yet accounted for
    // TODO : Rename this
    public int calculateAndAddScore(String loggedInToken, QuizAnswer quizAnswer, long id) {
        AnswerOption chosenAnswer = answerOptionRepository.getAnswerOptionByAnswerOptionId(quizAnswer.getPickedAnswerOptionId());
        AnswerOption correctAnswer = qqRepository.getOne(id).getCorrectAnswer();

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


