package ch.uzh.ifi.hase.soprafs23.service.quiz;

import ch.uzh.ifi.hase.soprafs23.constant.AdditionalDisplayType;
import ch.uzh.ifi.hase.soprafs23.constant.PromptType;
import ch.uzh.ifi.hase.soprafs23.constant.QuestionType;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.*;
import ch.uzh.ifi.hase.soprafs23.entity.quiz.AnswerOption;
import ch.uzh.ifi.hase.soprafs23.entity.quiz.QuizQuestion;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.prompt.DrawingPromptAnswerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.prompt.PotentialQuestionRepository;
import ch.uzh.ifi.hase.soprafs23.repository.prompt.TextPromptAnswerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.prompt.TrueFalsePromptAnswerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.quiz.AnswerOptionRepository;
import ch.uzh.ifi.hase.soprafs23.repository.quiz.QuizQuestionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
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

    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;

    private final Random rand = SecureRandom.getInstanceStrong();

    @Autowired
    public QuizQuestionGenerator(@Qualifier("quizQuestionRepository") QuizQuestionRepository qqRepository,
                                 @Qualifier("potentialQuestionRepository") PotentialQuestionRepository pqRepository,
                                 @Qualifier("textPromptAnswerRepository") TextPromptAnswerRepository textPromptAnswerRepository,
                                 @Qualifier("trueFalsePromptAnswerRepository") TrueFalsePromptAnswerRepository trueFalsePromptAnswerRepository,
                                 @Qualifier("drawingPromptAnswerRepository") DrawingPromptAnswerRepository drawingPromptAnswerRepository,
                                 @Qualifier("answerOptionRepository") AnswerOptionRepository answerOptionRepository,
                                 @Qualifier("playerRepository") PlayerRepository playerRepository,
                                 @Qualifier("gameRepository") GameRepository gameRepository)
            throws NoSuchAlgorithmException {
        this.qqRepository = qqRepository;
        this.pqRepository = pqRepository;
        this.textPromptAnswerRepository = textPromptAnswerRepository;
        this.trueFalsePromptAnswerRepository = trueFalsePromptAnswerRepository;
        this.drawingPromptAnswerRepository = drawingPromptAnswerRepository;
        this.answerOptionRepository = answerOptionRepository;
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
    }

    public List<QuizQuestion> createQuizQuestions(String gamePin) {
        // to make testing in postman possible
        // sets an answer for every prompt for every player
        //promptAnswerService.mockPromptAnswersForGame(gamePin); //TODO: remove

        Game gameByPin = gameRepository.findByGamePin(gamePin);

        List<QuizQuestion> createdQuestions = new ArrayList<>();

        for (Prompt prompt : gameByPin.getPromptSet()) {
            createdQuestions.addAll(generateQuizQuestions(prompt, gameByPin));
        }
        Collections.shuffle(createdQuestions);

        gameByPin.addQuizQuestions(createdQuestions);
        gameByPin.nextQuestion();
        // gameByPin.setStatus(GameStatus.QUIZ);

        return createdQuestions;
    }

    public List<QuizQuestion> generateQuizQuestions(Prompt prompt, Game game) {
        assert game != null;

        System.out.println("started generating questions");

        List<QuizQuestion> createdQuestions = new ArrayList<>();
        List<PotentialQuestion> potentialQuestions = pqRepository.findAllByAssociatedPrompt(prompt);
        PotentialQuestion selectedPotentialQuestion;
        QuizQuestion createdQuestion;
        selectedPotentialQuestion = potentialQuestions.get(rand.nextInt(potentialQuestions.size()));

        while (createdQuestions.size() < 2) {
            if (prompt.getPromptType() == PromptType.DRAWING) {
                List<DrawingPromptAnswer> answersToPrompt = drawingPromptAnswerRepository.findAllByAssociatedGamePinAndAssociatedPromptNr(game.getGamePin(), prompt.getPromptNr());
                createdQuestion = transformPotentialQuestionDrawing(selectedPotentialQuestion, answersToPrompt);
            }
            else if (prompt.getPromptType() == PromptType.TEXT) {
                List<TextPromptAnswer> answersToPrompt = textPromptAnswerRepository.findAllByAssociatedGamePinAndAssociatedPromptNr(game.getGamePin(), prompt.getPromptNr());
                createdQuestion = transformPotentialQuestionText(selectedPotentialQuestion, answersToPrompt);
            }
            else {
                List<TrueFalsePromptAnswer> answersToPrompt = trueFalsePromptAnswerRepository.findAllByAssociatedGamePinAndAssociatedPromptNr(game.getGamePin(), prompt.getPromptNr());
                createdQuestion = transformPotentialQuestionTF(selectedPotentialQuestion, answersToPrompt);
            }
            if (createdQuestion == null) {
                continue;
            }
            createdQuestion.setAssociatedGamePin(game.getGamePin());
            createdQuestion.setAssociatedPrompt(prompt);
            createdQuestions.add(createdQuestion);
            // does not reach this point
            System.out.println("Created a question: " + createdQuestion.getQuestionId());
        }

        System.out.println("returning generated questions");

        return createdQuestions;
    }

    private QuizQuestion transformPotentialQuestionDrawing(PotentialQuestion pq, List<DrawingPromptAnswer> allAnswers) {
        QuizQuestion newQuestion = new QuizQuestion();

        //picked pq will ask which player drawing is from
        if (pq.getQuestionType() == QuestionType.PLAYER) {
            System.out.println("Drawing question - Player");
            newQuestion.setQuizQuestionText(pq.getQuestionText());

            DrawingPromptAnswer selectedCorrectPromptAnswer = allAnswers.get(rand.nextInt(allAnswers.size()));
            Player correctAnswerPlayer = playerRepository.findByPlayerId(selectedCorrectPromptAnswer.getAssociatedPlayerId());

            if (pq.getDisplayType() == AdditionalDisplayType.IMAGE) {
                newQuestion.setImageToDisplay(selectedCorrectPromptAnswer.getAnswerDrawing());
            }
            System.out.println("Set Question text to: " + newQuestion.getQuizQuestionText() + " with display: " + newQuestion.getImageToDisplay());

            // set and save the correct answer option, and add this option to the question
            AnswerOption correctAnswer = new AnswerOption();
            correctAnswer.setAnswerOptionText(correctAnswerPlayer.getPlayerName()); // bc the answer is a player
            newQuestion.setCorrectAnswer(correctAnswer);
            newQuestion.addAnswerOption(correctAnswer);
            answerOptionRepository.save(correctAnswer);
            answerOptionRepository.flush();
            // remove used answer
            allAnswers.remove(selectedCorrectPromptAnswer);

            while (newQuestion.getAnswerOptions().size() < 4) {
                // get a random answer and its according player from allAnswers
                DrawingPromptAnswer selectedPromptAnswer = allAnswers.get(rand.nextInt(allAnswers.size()));
                Player selectedPromptPlayer = playerRepository.findByPlayerId(selectedPromptAnswer.getAssociatedPlayerId());

                // creat and set a new answer option with that according player
                AnswerOption newAnswerOption = new AnswerOption();
                newAnswerOption.setAnswerOptionText(selectedPromptPlayer.getPlayerName());

                // "if the players name is already used - choose another one" - I think that is the mistake,
                // because I sometimes create players with the same name...
                newQuestion.addAnswerOption(newAnswerOption);
                answerOptionRepository.save(newAnswerOption);
                answerOptionRepository.flush();
                allAnswers.remove(selectedPromptAnswer);
            }
        }

        //picked pq will ask which drawing is from a specific player
        else if (pq.getQuestionType() == QuestionType.PROMPTANSWER) {
            System.out.println("Drawing question - Promptanswer");
            DrawingPromptAnswer selectedCorrectPromptAnswer = allAnswers.get(rand.nextInt(allAnswers.size()));
            Player correctAnswerPlayer = playerRepository.findByPlayerId(selectedCorrectPromptAnswer.getAssociatedPlayerId());

            if (pq.isRequiresTextInput()) {
                newQuestion.setQuizQuestionText(String.format(pq.getQuestionText(), correctAnswerPlayer.getPlayerName()));
            }
            else {
                newQuestion.setQuizQuestionText(pq.getQuestionText());
            }
            System.out.println("Set Question text to: " + newQuestion.getQuizQuestionText());

            AnswerOption correctAnswer = new AnswerOption();
            correctAnswer.setAnswerOptionText(selectedCorrectPromptAnswer.getAnswerDrawing());
            newQuestion.setCorrectAnswer(correctAnswer);
            newQuestion.addAnswerOption(correctAnswer);
            answerOptionRepository.save(correctAnswer);
            answerOptionRepository.flush();
            allAnswers.remove(selectedCorrectPromptAnswer);

            while (newQuestion.getAnswerOptions().size() < 4) {
                DrawingPromptAnswer selectedPromptAnswer = allAnswers.get(rand.nextInt(allAnswers.size()));

                AnswerOption newAnswerOption = new AnswerOption();
                newAnswerOption.setAnswerOptionText(selectedPromptAnswer.getAnswerDrawing());
                newQuestion.addAnswerOption(newAnswerOption);
                answerOptionRepository.save(newAnswerOption);
                answerOptionRepository.flush();
                allAnswers.remove(selectedPromptAnswer);
            }
        }

        qqRepository.save(newQuestion);
        qqRepository.flush();
        return newQuestion;
    }

    private QuizQuestion transformPotentialQuestionText(PotentialQuestion pq, List<TextPromptAnswer> allAnswers) {
        QuizQuestion newQuestion = new QuizQuestion();

        //picked pq will ask which player answer is from
        if (pq.getQuestionType() == QuestionType.PLAYER) {

            System.out.println("Text question - Player");
            TextPromptAnswer selectedCorrectPromptAnswer = allAnswers.get(rand.nextInt(allAnswers.size()));
            Player correctAnswerPlayer = playerRepository.findByPlayerId(selectedCorrectPromptAnswer.getAssociatedPlayerId());

            if (pq.isRequiresTextInput()) {
                newQuestion.setQuizQuestionText(String.format(pq.getQuestionText(), selectedCorrectPromptAnswer.getAnswer()));
            }
            else {
                newQuestion.setQuizQuestionText(pq.getQuestionText());
            }
            System.out.println("Set Question text to: " + newQuestion.getQuizQuestionText());

            AnswerOption correctAnswer = new AnswerOption();
            correctAnswer.setAnswerOptionText(correctAnswerPlayer.getPlayerName());
            newQuestion.setCorrectAnswer(correctAnswer);
            newQuestion.addAnswerOption(correctAnswer);
            answerOptionRepository.save(correctAnswer);
            answerOptionRepository.flush();
            allAnswers.remove(selectedCorrectPromptAnswer);

            while (newQuestion.getAnswerOptions().size() < 4) {

                TextPromptAnswer selectedPromptAnswer = allAnswers.get(rand.nextInt(allAnswers.size()));
                Player selectedPromptPlayer = playerRepository.findByPlayerId(selectedPromptAnswer.getAssociatedPlayerId());

                AnswerOption newAnswerOption = new AnswerOption();
                newAnswerOption.setAnswerOptionText(selectedPromptPlayer.getPlayerName());
                newQuestion.addAnswerOption(newAnswerOption);
                answerOptionRepository.save(newAnswerOption);
                answerOptionRepository.flush();
                allAnswers.remove(selectedPromptAnswer);
            }
        }

        //picked pq will ask which answer is from a specific player
        else if (pq.getQuestionType() == QuestionType.PROMPTANSWER) {
            System.out.println("Text question - Promptanswer");
            TextPromptAnswer selectedCorrectPromptAnswer = allAnswers.get(rand.nextInt(allAnswers.size()));
            Player correctAnswerPlayer = playerRepository.findByPlayerId(selectedCorrectPromptAnswer.getAssociatedPlayerId());

            if (pq.isRequiresTextInput()) {
                newQuestion.setQuizQuestionText(String.format(pq.getQuestionText(), correctAnswerPlayer.getPlayerName()));
            }
            else {
                newQuestion.setQuizQuestionText(pq.getQuestionText());
            }
            System.out.println("Set Question text to: " + newQuestion.getQuizQuestionText());

            AnswerOption correctAnswer = new AnswerOption();
            correctAnswer.setAnswerOptionText(selectedCorrectPromptAnswer.getAnswer());
            newQuestion.setCorrectAnswer(correctAnswer);
            newQuestion.addAnswerOption(correctAnswer);
            answerOptionRepository.save(correctAnswer);
            answerOptionRepository.flush();
            allAnswers.remove(selectedCorrectPromptAnswer);

            while (newQuestion.getAnswerOptions().size() < 4) {
                TextPromptAnswer selectedPromptAnswer = allAnswers.get(rand.nextInt(allAnswers.size()));

                AnswerOption newAnswerOption = new AnswerOption();
                newAnswerOption.setAnswerOptionText(selectedPromptAnswer.getAnswer());
                newQuestion.addAnswerOption(newAnswerOption);
                answerOptionRepository.save(newAnswerOption);
                answerOptionRepository.flush();
                allAnswers.remove(selectedPromptAnswer);
            }
        }

        qqRepository.save(newQuestion);
        qqRepository.flush();
        return newQuestion;
    }

    private QuizQuestion transformPotentialQuestionTF(PotentialQuestion pq, List<TrueFalsePromptAnswer> allAnswers) {
        QuizQuestion newQuestion = new QuizQuestion();

        //picked pq will ask which player story is from
        if (pq.getQuestionType() == QuestionType.PLAYER) {
            System.out.println("tf question - Player");

            List<TrueFalsePromptAnswer> allAnswersCopy = new ArrayList<>(allAnswers);
            TrueFalsePromptAnswer selectedCorrectPromptAnswer = null;
            while (selectedCorrectPromptAnswer == null) {
                selectedCorrectPromptAnswer = allAnswersCopy.get(rand.nextInt(allAnswersCopy.size()));
                if (!selectedCorrectPromptAnswer.getAnswerBoolean()) {
                    allAnswersCopy.remove(selectedCorrectPromptAnswer);
                    selectedCorrectPromptAnswer = null;
                }
                if (allAnswersCopy.isEmpty()) {
                    return null;
                }
            }

            newQuestion.setQuizQuestionText(pq.getQuestionText());

            Player correctAnswerPlayer = playerRepository.findByPlayerId(selectedCorrectPromptAnswer.getAssociatedPlayerId());

            if (pq.getDisplayType() == AdditionalDisplayType.TEXT) {
                newQuestion.setStoryToDisplay(selectedCorrectPromptAnswer.getAnswerText());
            }

            System.out.println("Set Question text to: " + newQuestion.getQuizQuestionText() + " with display: " + newQuestion.getStoryToDisplay());

            AnswerOption correctAnswer = new AnswerOption();
            correctAnswer.setAnswerOptionText(correctAnswerPlayer.getPlayerName());
            newQuestion.setCorrectAnswer(correctAnswer);
            newQuestion.addAnswerOption(correctAnswer);
            answerOptionRepository.save(correctAnswer);
            answerOptionRepository.flush();
            allAnswers.remove(selectedCorrectPromptAnswer);

            while (newQuestion.getAnswerOptions().size() < 4) {
                TrueFalsePromptAnswer selectedPromptAnswer = allAnswers.get(rand.nextInt(allAnswers.size()));
                Player selectedPromptPlayer = playerRepository.findByPlayerId(selectedPromptAnswer.getAssociatedPlayerId());

                AnswerOption newAnswerOption = new AnswerOption();
                newAnswerOption.setAnswerOptionText(selectedPromptPlayer.getPlayerName());
                newQuestion.addAnswerOption(newAnswerOption);
                answerOptionRepository.save(newAnswerOption);
                answerOptionRepository.flush();
                allAnswers.remove(selectedPromptAnswer);
            }
        }

        //picked pq will ask whether story by specific user is true
        else if (pq.getQuestionType() == QuestionType.BOOLEAN) {
            System.out.println("Drawing question - Boolean");

            TrueFalsePromptAnswer selectedCorrectPromptAnswer = allAnswers.get(rand.nextInt(allAnswers.size()));
            Player correctAnswerPlayer = playerRepository.findByPlayerId(selectedCorrectPromptAnswer.getAssociatedPlayerId());

            if (pq.isRequiresTextInput()) {
                newQuestion.setQuizQuestionText(String.format(pq.getQuestionText(), correctAnswerPlayer.getPlayerName()));
            }
            else {
                newQuestion.setQuizQuestionText(pq.getQuestionText());
            }


            if (pq.getDisplayType() == AdditionalDisplayType.TEXT) {
                newQuestion.setStoryToDisplay(selectedCorrectPromptAnswer.getAnswerText());
            }

            System.out.println("Set Question text to: " + newQuestion.getQuizQuestionText() + " with display: " + newQuestion.getStoryToDisplay());

            AnswerOption correctAnswer = new AnswerOption();
            correctAnswer.setAnswerOptionText(selectedCorrectPromptAnswer.getAnswerBoolean().toString());
            newQuestion.setCorrectAnswer(correctAnswer);
            newQuestion.addAnswerOption(correctAnswer);
            answerOptionRepository.save(correctAnswer);
            answerOptionRepository.flush();
            allAnswers.remove(selectedCorrectPromptAnswer);

            AnswerOption newAnswerOption = new AnswerOption();
            if (selectedCorrectPromptAnswer.getAnswerBoolean()) {
                newAnswerOption.setAnswerOptionText("false");
            }
            else {
                newAnswerOption.setAnswerOptionText("true");
            }

            answerOptionRepository.save(newAnswerOption);
            answerOptionRepository.flush();
        }

        qqRepository.save(newQuestion);
        qqRepository.flush();
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
