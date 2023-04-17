package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.PromptType;
import ch.uzh.ifi.hase.soprafs23.constant.QuestionType;
import ch.uzh.ifi.hase.soprafs23.entity.*;
import ch.uzh.ifi.hase.soprafs23.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class QuizQuestionService {

    private final Logger log = LoggerFactory.getLogger(QuizQuestionService.class);
    private final QuizQuestionRepository qqRepository;

    private GameService gameService;

    private PromptAnswerService promptAnswerService ;
    private QuizQuestionGenerator quizQuestionGenerator;

    private final Random rand = SecureRandom.getInstanceStrong();

    @Autowired
    public QuizQuestionService(@Qualifier("quizQuestionRepository") QuizQuestionRepository qqRepository) throws NoSuchAlgorithmException {
        this.qqRepository = qqRepository;
    }

    @Autowired
    private void setGameService(GameService gameService) {
        this.gameService = gameService;
    }
    @Autowired
    private void setQuizQuestionGenerator(QuizQuestionGenerator quizQuestionGenerator) {
        this.quizQuestionGenerator = quizQuestionGenerator;
    }

    @Autowired
    private void setPromptAnswerService(PromptAnswerService promptAnswerService) {
        this.promptAnswerService = promptAnswerService;
    }

    public List<QuizQuestion> getQuizQuestions() {
        return qqRepository.findAll();
    }

    public List<QuizQuestion> getQuizQuestionsOfGame(String gamePin) {
        Game gameByPin = gameService.getGameByPin(gamePin);
        return gameByPin.getQuizQuestionSet();
    }


    public List<QuizQuestion> createQuizQuestions(String gamePin) {
        promptAnswerService.mockPromptAnswersForGame(gamePin);

        Game gameByPin = gameService.getGameByPin(gamePin);

        List<QuizQuestion> createdQuestions = new ArrayList<>();

        for (Prompt prompt : gameByPin.getPromptSet()) {
            createdQuestions.addAll(quizQuestionGenerator.generateQuizQuestions(prompt, gameByPin));
        }

        gameService.addQuizQuestionsToGame(createdQuestions, gamePin);

        return createdQuestions;
    }
}
