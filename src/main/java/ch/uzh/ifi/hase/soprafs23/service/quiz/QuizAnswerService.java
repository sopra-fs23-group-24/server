package ch.uzh.ifi.hase.soprafs23.service.quiz;

import ch.uzh.ifi.hase.soprafs23.constant.CompletionStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.quiz.AnswerOption;
import ch.uzh.ifi.hase.soprafs23.entity.quiz.QuizAnswer;
import ch.uzh.ifi.hase.soprafs23.entity.quiz.QuizQuestion;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.quiz.AnswerOptionRepository;
import ch.uzh.ifi.hase.soprafs23.repository.quiz.QuizAnswerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.quiz.QuizQuestionRepository;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuizAnswerService {
    private final QuizQuestionRepository qqRepository;
    private final AnswerOptionRepository answerOptionRepository;
    private final PlayerRepository playerRepository;

    private final GameRepository gameRepository;
    private final QuizAnswerRepository quizAnswerRepository;
    @Autowired
    public QuizAnswerService(@Qualifier("gameRepository") GameRepository gameRepository,
                             @Qualifier("quizQuestionRepository") QuizQuestionRepository qqRepository,
                             @Qualifier("answerOptionRepository") AnswerOptionRepository answerOptionRepository,
                             @Qualifier("playerRepository") PlayerRepository playerRepository,
                             @Qualifier("quizAnswerRepository") QuizAnswerRepository quizAnswerRepository)  {
        this.gameRepository = gameRepository;
        this.qqRepository = qqRepository;
        this.answerOptionRepository = answerOptionRepository;
        this.playerRepository = playerRepository;
        this.quizAnswerRepository = quizAnswerRepository;
    }


    // return value is never used
    public QuizAnswer addQuizAnswerToQuizQuestion(QuizAnswer newQuizAnswer, long quizQuestionId, String gamePin, String loggedInToken){
        Game gameByPin = gameRepository.findByGamePin(gamePin);
        if (gameByPin == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No game with this pin found.");
        }

        // set the player
        Player player = playerRepository.findByToken(loggedInToken);
        if (player == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No player with this token found.");
        }
        newQuizAnswer.setAssociatedPlayer(player);

        // check if already answered
        QuizQuestion questionById = qqRepository.findByQuestionId(quizQuestionId);
        for (QuizAnswer answer : questionById.getReceivedAnswers()) {
            if (answer.getAssociatedPlayer() == newQuizAnswer.getAssociatedPlayer()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This player already answered this question.");
            }
        }
        // add and save answer
        newQuizAnswer = quizAnswerRepository.save(newQuizAnswer);
        quizAnswerRepository.flush();

        questionById.addReceivedAnswer(newQuizAnswer);
        qqRepository.save(questionById);
        qqRepository.flush();

        // check if all players have answered a question

        List<Player> allPlayersOfGame = new ArrayList<>(gameByPin.getPlayerGroup());
        for (QuizAnswer answer : questionById.getReceivedAnswers()) {
            allPlayersOfGame.remove(answer.getAssociatedPlayer());
        }
        if (allPlayersOfGame.isEmpty()) {
            questionById.setQuestionStatus(CompletionStatus.FINISHED);
            qqRepository.save(questionById);
            qqRepository.flush();
        }

        return newQuizAnswer;
    }


    public int calculateAndAddScore(QuizAnswer quizAnswer, long questionId, String gamePin) {
        int score = 0;
        int scoreToAddInCaseOfNoTimer = 10;
        // get the chosen and the correct answer
        long pickedId = quizAnswer.getPickedAnswerOptionId();
        AnswerOption chosenAnswer = answerOptionRepository.getAnswerOptionByAnswerOptionId(pickedId);
        AnswerOption correctAnswer = qqRepository.findByQuestionId(questionId).getCorrectAnswer();


        if (chosenAnswer.equals(correctAnswer)) {
            int gameTimer = gameRepository.findByGamePin(gamePin).getTimer();
            if (gameTimer == -1) {
                score = scoreToAddInCaseOfNoTimer;
            } else {
                // sets the reminding time as score (that is our calculation of score.)
                score = quizAnswer.getTimer(); // rename get timer to reminding time
            }
        }
        // add points to player and set the latest score, is 0 if incorrect answer
        Player player = quizAnswer.getAssociatedPlayer();
        player.addPoints(score);
        player.setLatestScore(score);

        playerRepository.save(quizAnswer.getAssociatedPlayer());
        playerRepository.flush();
        return score;

    }

}


