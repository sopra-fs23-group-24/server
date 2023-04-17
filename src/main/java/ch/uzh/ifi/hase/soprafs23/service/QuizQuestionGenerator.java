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
public class QuizQuestionGenerator {

    private final Logger log = LoggerFactory.getLogger(QuizQuestionGenerator.class);
    private final QuizQuestionRepository qqRepository;
    private final PotentialQuestionRepository pqRepository;
    private final TextPromptAnswerRepository textPromptAnswerRepository;
    private final TrueFalsePromptAnswerRepository trueFalsePromptAnswerRepository;
    private final DrawingPromptAnswerRepository drawingPromptAnswerRepository;

    private final AnswerOptionRepository answerOptionRepository;

    private GameService gameService;
    private PlayerService playerService;

    private final Random rand = SecureRandom.getInstanceStrong();

    @Autowired
    public QuizQuestionGenerator(@Qualifier("quizQuestionRepository") QuizQuestionRepository qqRepository,
                                 @Qualifier("potentialQuestionRepository") PotentialQuestionRepository pqRepository,
                                 @Qualifier("textPromptAnswerRepository") TextPromptAnswerRepository textPromptAnswerRepository,
                                 @Qualifier("trueFalsePromptAnswerRepository") TrueFalsePromptAnswerRepository trueFalsePromptAnswerRepository,
                                 @Qualifier("drawingPromptAnswerRepository") DrawingPromptAnswerRepository drawingPromptAnswerRepository,
                                 @Qualifier("answerOptionRepository") AnswerOptionRepository answerOptionRepository) throws NoSuchAlgorithmException {
        this.qqRepository = qqRepository;
        this.pqRepository = pqRepository;
        this.textPromptAnswerRepository = textPromptAnswerRepository;
        this.trueFalsePromptAnswerRepository = trueFalsePromptAnswerRepository;
        this.drawingPromptAnswerRepository = drawingPromptAnswerRepository;
        this.answerOptionRepository = answerOptionRepository;
    }

    @Autowired
    private void setGameService(GameService gameService) {
        this.gameService = gameService;
    }
    @Autowired
    private void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }


    public List<QuizQuestion> generateQuizQuestions(Prompt prompt, Game game){
        assert game != null;

        List<QuizQuestion> createdQuestions = new ArrayList<>();
        List<PotentialQuestion> potentialQuestions = pqRepository.findAllByAssociatedPrompt(prompt);
        PotentialQuestion selectedPotentialQuestion;
        QuizQuestion createdQuestion;

        //create one quiz question for a drawing prompt
        if(prompt.getPromptType() == PromptType.DRAWING){
            for(int i = 0; i < 2; i++) {
                selectedPotentialQuestion = potentialQuestions.get(rand.nextInt(potentialQuestions.size()));
                List<DrawingPromptAnswer> answersToPrompt = drawingPromptAnswerRepository.findAllByAssociatedGamePinAndAssociatedPromptNr(game.getGamePin(), prompt.getPromptNr());
                createdQuestion = transformPotentialQuestionDrawing(selectedPotentialQuestion, answersToPrompt);
                createdQuestion.setAssociatedGame(game);
                createdQuestion.setAssociatedPrompt(prompt);
                createdQuestions.add(createdQuestion);
            }
        }

        //create two quiz question for a text prompt
        if(prompt.getPromptType() == PromptType.TEXT){
            for(int i = 0; i < 2; i++){
                selectedPotentialQuestion = potentialQuestions.get(rand.nextInt(potentialQuestions.size()));
                List<TextPromptAnswer> answersToPrompt = textPromptAnswerRepository.findAllByAssociatedGamePinAndAssociatedPromptNr(game.getGamePin(), prompt.getPromptNr());
                createdQuestion = transformPotentialQuestionText(selectedPotentialQuestion, answersToPrompt);
                createdQuestion.setAssociatedGame(game);
                createdQuestion.setAssociatedPrompt(prompt);
                createdQuestions.add(createdQuestion);
            }
        }

        //create two quiz question for a tf prompt
        if(prompt.getPromptType() == PromptType.TRUEFALSE){
            for(int i = 0; i < 2; i++){
                selectedPotentialQuestion = potentialQuestions.get(rand.nextInt(potentialQuestions.size()));
                List<TrueFalsePromptAnswer> answersToPrompt = trueFalsePromptAnswerRepository.findAllByAssociatedGamePinAndAssociatedPromptNr(game.getGamePin(), prompt.getPromptNr());
                createdQuestion = transformPotentialQuestionTF(selectedPotentialQuestion, answersToPrompt);
                createdQuestion.setAssociatedGame(game);
                createdQuestion.setAssociatedPrompt(prompt);
                createdQuestions.add(createdQuestion);
            }
        }
        return createdQuestions;
    }

    private QuizQuestion transformPotentialQuestionDrawing(PotentialQuestion pq, List<DrawingPromptAnswer> allAnswers) {
        QuizQuestion newQuestion = new QuizQuestion();

        //picked pq will ask which player drawing is from
        if(pq.getQuestionType() == QuestionType.PLAYER){
            DrawingPromptAnswer selectedCorrectPromptAnswer = allAnswers.get(rand.nextInt(allAnswers.size()));
            Player correctAnswerPlayer = playerService.getById(selectedCorrectPromptAnswer.getAssociatedPlayerId());

            AnswerOption correctAnswer = new AnswerOption();
            correctAnswer.setAnswerOptionText(correctAnswerPlayer.getPlayerName());
            newQuestion.setCorrectAnswer(correctAnswer);
            newQuestion.addAnswerOption(correctAnswer);
            answerOptionRepository.save(correctAnswer);
            answerOptionRepository.flush();

            while(newQuestion.getAnswerOptions().size() < 4){

            }
        }

        //picked pq will ask which drawing is from a specific player
        else if(pq.getQuestionType() == QuestionType.PROMPTANSWER){}

        return null;
    }

    private QuizQuestion transformPotentialQuestionText(PotentialQuestion pq, List<TextPromptAnswer> allAnswers) {
        return null;
    }

    private QuizQuestion transformPotentialQuestionTF(PotentialQuestion pq, List<TrueFalsePromptAnswer> allAnswers) {
        return null;
    }

    /*
    StringBuilder outputString = new StringBuilder();
        if (pq.isRequiresTextInput()) {
            if (pq.getQuestionType() == QuestionType.PLAYER) {
                outputString.append(String.format(pq.getQuestionText(), "PROMPT INPUT"));
            }
            else {
                outputString.append(String.format(pq.getQuestionText(), "USERNAME"));
            }
        }
        else {
            outputString.append(pq.getQuestionText());
        }
        if (pq.getDisplayType() == AdditionalDisplayType.IMAGE) {
            outputString.append(" ---- SHOW IMAGE TO GUESS");
        }
        else if (pq.getDisplayType() == AdditionalDisplayType.TEXT) {
            outputString.append(" ---- SHOW A TEXT");
        }
        System.out.println(outputString);
     */
}
