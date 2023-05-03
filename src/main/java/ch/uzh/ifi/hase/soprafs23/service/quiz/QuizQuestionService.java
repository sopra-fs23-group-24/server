package ch.uzh.ifi.hase.soprafs23.service.quiz;

import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.quiz.QuizQuestion;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.quiz.QuizQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * QuizQuestion Service
 * This class is the "worker" and responsible for all functionality related to
 * the QuizQuestions
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class QuizQuestionService {

    private final QuizQuestionRepository qqRepository;
    private final GameRepository gameRepository;


    @Autowired
    public QuizQuestionService(@Qualifier("quizQuestionRepository") QuizQuestionRepository qqRepository,
                               @Qualifier("gameRepository") GameRepository gameRepository) {
        this.qqRepository = qqRepository;
        this.gameRepository = gameRepository;
    }

    public List<QuizQuestion> getQuizQuestions() {
        return qqRepository.findAll();
    }

    public List<QuizQuestion> getQuizQuestionsOfGame(String gamePin) {
        Game gameByPin = gameRepository.findByGamePin(gamePin);
        if (gameByPin == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No game with this pin found.");
        }
        return qqRepository.findAllByAssociatedGamePin(gamePin);
    }

}
