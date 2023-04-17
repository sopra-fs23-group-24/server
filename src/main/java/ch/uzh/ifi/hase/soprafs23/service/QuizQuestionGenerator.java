package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.AdditionalDisplayType;
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
            newQuestion.setQuizQuestionText(pq.getQuestionText());

            DrawingPromptAnswer selectedCorrectPromptAnswer = allAnswers.get(rand.nextInt(allAnswers.size()));
            Player correctAnswerPlayer = playerService.getById(selectedCorrectPromptAnswer.getAssociatedPlayerId());
            if(pq.getDisplayType() == AdditionalDisplayType.IMAGE){
                newQuestion.setImageToDisplay(selectedCorrectPromptAnswer.getAnswerDrawing());
            }

            AnswerOption correctAnswer = new AnswerOption();
            correctAnswer.setAnswerOptionText(correctAnswerPlayer.getPlayerName());
            newQuestion.setCorrectAnswer(correctAnswer);
            newQuestion.addAnswerOption(correctAnswer);
            answerOptionRepository.save(correctAnswer);
            answerOptionRepository.flush();

            while(newQuestion.getAnswerOptions().size() < 4){
                DrawingPromptAnswer selectedPromptAnswer = allAnswers.get(rand.nextInt(allAnswers.size()));
                Player selectedPromptPlayer = playerService.getById(selectedPromptAnswer.getAssociatedPlayerId());

                AnswerOption newAnswerOption = new AnswerOption();
                newAnswerOption.setAnswerOptionText(selectedPromptPlayer.getPlayerName());
                if(!newQuestion.getAnswerOptionStrings().contains(newAnswerOption.getAnswerOptionText())){
                    newQuestion.addAnswerOption(correctAnswer);
                    answerOptionRepository.save(correctAnswer);
                    answerOptionRepository.flush();
                }
            }
        }

        //picked pq will ask which drawing is from a specific player
        else if(pq.getQuestionType() == QuestionType.PROMPTANSWER){
            DrawingPromptAnswer selectedCorrectPromptAnswer = allAnswers.get(rand.nextInt(allAnswers.size()));
            Player correctAnswerPlayer = playerService.getById(selectedCorrectPromptAnswer.getAssociatedPlayerId());

            if(pq.isRequiresTextInput()){
                newQuestion.setQuizQuestionText(String.format(pq.getQuestionText(), correctAnswerPlayer.getPlayerName()));
            }else{
                newQuestion.setQuizQuestionText(pq.getQuestionText());
            }

            AnswerOption correctAnswer = new AnswerOption();
            correctAnswer.setAnswerOptionText(selectedCorrectPromptAnswer.getAnswerDrawing());
            newQuestion.setCorrectAnswer(correctAnswer);
            newQuestion.addAnswerOption(correctAnswer);
            answerOptionRepository.save(correctAnswer);
            answerOptionRepository.flush();

            while(newQuestion.getAnswerOptions().size() < 4){
                DrawingPromptAnswer selectedPromptAnswer = allAnswers.get(rand.nextInt(allAnswers.size()));
                Player selectedPromptPlayer = playerService.getById(selectedPromptAnswer.getAssociatedPlayerId());

                AnswerOption newAnswerOption = new AnswerOption();
                newAnswerOption.setAnswerOptionText(selectedCorrectPromptAnswer.getAnswerDrawing());
                if(!newQuestion.getAnswerOptionStrings().contains(newAnswerOption.getAnswerOptionText())){
                    newQuestion.addAnswerOption(correctAnswer);
                    answerOptionRepository.save(correctAnswer);
                    answerOptionRepository.flush();
                }
            }
        }

        return newQuestion;
    }

    private QuizQuestion transformPotentialQuestionText(PotentialQuestion pq, List<TextPromptAnswer> allAnswers) {
        QuizQuestion newQuestion = new QuizQuestion();

        //picked pq will ask which player answer is from
        if(pq.getQuestionType() == QuestionType.PLAYER){
            TextPromptAnswer selectedCorrectPromptAnswer = allAnswers.get(rand.nextInt(allAnswers.size()));
            Player correctAnswerPlayer = playerService.getById(selectedCorrectPromptAnswer.getAssociatedPlayerId());

            if(pq.isRequiresTextInput()){
                newQuestion.setQuizQuestionText(String.format(pq.getQuestionText(), selectedCorrectPromptAnswer.getAnswer()));
            }else{
                newQuestion.setQuizQuestionText(pq.getQuestionText());
            }

            AnswerOption correctAnswer = new AnswerOption();
            correctAnswer.setAnswerOptionText(correctAnswerPlayer.getPlayerName());
            newQuestion.setCorrectAnswer(correctAnswer);
            newQuestion.addAnswerOption(correctAnswer);
            answerOptionRepository.save(correctAnswer);
            answerOptionRepository.flush();

            while(newQuestion.getAnswerOptions().size() < 4){
                TextPromptAnswer selectedPromptAnswer = allAnswers.get(rand.nextInt(allAnswers.size()));
                Player selectedPromptPlayer = playerService.getById(selectedPromptAnswer.getAssociatedPlayerId());

                AnswerOption newAnswerOption = new AnswerOption();
                newAnswerOption.setAnswerOptionText(selectedPromptPlayer.getPlayerName());
                if(!newQuestion.getAnswerOptionStrings().contains(newAnswerOption.getAnswerOptionText())){
                    newQuestion.addAnswerOption(correctAnswer);
                    answerOptionRepository.save(correctAnswer);
                    answerOptionRepository.flush();
                }
            }
        }

        //picked pq will ask which answer is from a specific player
        else if(pq.getQuestionType() == QuestionType.PROMPTANSWER){
            TextPromptAnswer selectedCorrectPromptAnswer = allAnswers.get(rand.nextInt(allAnswers.size()));
            Player correctAnswerPlayer = playerService.getById(selectedCorrectPromptAnswer.getAssociatedPlayerId());

            if(pq.isRequiresTextInput()){
                newQuestion.setQuizQuestionText(String.format(pq.getQuestionText(), correctAnswerPlayer.getPlayerName()));
            }else{
                newQuestion.setQuizQuestionText(pq.getQuestionText());
            }

            AnswerOption correctAnswer = new AnswerOption();
            correctAnswer.setAnswerOptionText(selectedCorrectPromptAnswer.getAnswer());
            newQuestion.setCorrectAnswer(correctAnswer);
            newQuestion.addAnswerOption(correctAnswer);
            answerOptionRepository.save(correctAnswer);
            answerOptionRepository.flush();

            while(newQuestion.getAnswerOptions().size() < 4){
                TextPromptAnswer selectedPromptAnswer = allAnswers.get(rand.nextInt(allAnswers.size()));

                AnswerOption newAnswerOption = new AnswerOption();
                newAnswerOption.setAnswerOptionText(selectedCorrectPromptAnswer.getAnswer());
                if(!newQuestion.getAnswerOptionStrings().contains(newAnswerOption.getAnswerOptionText())){
                    newQuestion.addAnswerOption(correctAnswer);
                    answerOptionRepository.save(correctAnswer);
                    answerOptionRepository.flush();
                }
            }
        }

        return newQuestion;
    }

    private QuizQuestion transformPotentialQuestionTF(PotentialQuestion pq, List<TrueFalsePromptAnswer> allAnswers) {
        QuizQuestion newQuestion = new QuizQuestion();

        //picked pq will ask which player story is from
        if(pq.getQuestionType() == QuestionType.PLAYER){
            newQuestion.setQuizQuestionText(pq.getQuestionText());

            TrueFalsePromptAnswer selectedCorrectPromptAnswer = null;
            while(selectedCorrectPromptAnswer == null){
                selectedCorrectPromptAnswer = allAnswers.get(rand.nextInt(allAnswers.size()));
                if(!selectedCorrectPromptAnswer.getAnswerBoolean()){
                    selectedCorrectPromptAnswer = null;
                }
            }


            Player correctAnswerPlayer = playerService.getById(selectedCorrectPromptAnswer.getAssociatedPlayerId());

            if(pq.getDisplayType() == AdditionalDisplayType.TEXT){
                newQuestion.setStoryToDisplay(selectedCorrectPromptAnswer.getAnswerText());
            }

            AnswerOption correctAnswer = new AnswerOption();
            correctAnswer.setAnswerOptionText(correctAnswerPlayer.getPlayerName());
            newQuestion.setCorrectAnswer(correctAnswer);
            newQuestion.addAnswerOption(correctAnswer);
            answerOptionRepository.save(correctAnswer);
            answerOptionRepository.flush();

            while(newQuestion.getAnswerOptions().size() < 4){
                TrueFalsePromptAnswer selectedPromptAnswer = allAnswers.get(rand.nextInt(allAnswers.size()));
                Player selectedPromptPlayer = playerService.getById(selectedPromptAnswer.getAssociatedPlayerId());

                AnswerOption newAnswerOption = new AnswerOption();
                newAnswerOption.setAnswerOptionText(selectedPromptPlayer.getPlayerName());
                if(!newQuestion.getAnswerOptionStrings().contains(newAnswerOption.getAnswerOptionText())){
                    newQuestion.addAnswerOption(correctAnswer);
                    answerOptionRepository.save(correctAnswer);
                    answerOptionRepository.flush();
                }
            }
        }

        //picked pq will ask whether story by specific user is true
        else if(pq.getQuestionType() == QuestionType.PROMPTANSWER){
            TrueFalsePromptAnswer selectedCorrectPromptAnswer = allAnswers.get(rand.nextInt(allAnswers.size()));
            Player correctAnswerPlayer = playerService.getById(selectedCorrectPromptAnswer.getAssociatedPlayerId());

            if(pq.isRequiresTextInput()){
                newQuestion.setQuizQuestionText(String.format(pq.getQuestionText(), correctAnswerPlayer.getPlayerName()));
            }else{
                newQuestion.setQuizQuestionText(pq.getQuestionText());
            }

            if(pq.getDisplayType() == AdditionalDisplayType.TEXT){
                newQuestion.setStoryToDisplay(selectedCorrectPromptAnswer.getAnswerText());
            }

            AnswerOption correctAnswer = new AnswerOption();
            correctAnswer.setAnswerOptionText(selectedCorrectPromptAnswer.getAnswerBoolean().toString());
            newQuestion.setCorrectAnswer(correctAnswer);
            newQuestion.addAnswerOption(correctAnswer);
            answerOptionRepository.save(correctAnswer);
            answerOptionRepository.flush();

            AnswerOption newAnswerOption = new AnswerOption();
            if(selectedCorrectPromptAnswer.getAnswerBoolean()){
                newAnswerOption.setAnswerOptionText("false");
            }else{
                newAnswerOption.setAnswerOptionText("true");
            }

            answerOptionRepository.save(correctAnswer);
            answerOptionRepository.flush();
        }

        return newQuestion;
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
