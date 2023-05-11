package ch.uzh.ifi.hase.soprafs23.service.quiz;

import ch.uzh.ifi.hase.soprafs23.constant.DisplayType;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * This class is responsible for creating all QuizQuestions for the game
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

    private final int nrOfQuestionPerPrompt = 2;

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
        Game gameByPin = gameRepository.findByGamePin(gamePin);
        if (gameByPin == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No game with this pin found.");
        }

        List<QuizQuestion> createdQuestions = new ArrayList<>();

        //TODO: potentially distribute questions evenly among players?
        for (Prompt prompt : gameByPin.getPromptSet()) {
            List<QuizQuestion> questionsForPrompt = new ArrayList<>();
            switch (prompt.getPromptType()) {
                case DRAWING -> questionsForPrompt = generateQuestionsForDrawingPrompt(prompt, gameByPin);
                case TEXT -> questionsForPrompt = generateQuestionsForTextPrompt(prompt, gameByPin);
                case TRUEFALSE -> questionsForPrompt = generateQuestionsForTrueFalsePrompt(prompt, gameByPin);
            }
            createdQuestions.addAll(questionsForPrompt);
        }
        Collections.shuffle(createdQuestions);

        gameByPin.addQuizQuestions(createdQuestions);
        gameByPin.nextQuestion();

        gameRepository.save(gameByPin);
        gameRepository.flush();

        return createdQuestions;
    }

    private List<QuizQuestion> generateQuestionsForDrawingPrompt(Prompt prompt, Game game) {
        List<QuizQuestion> createdQuestionsForDrawingPrompt = new ArrayList<>();

        //create desired number of quizQuestions for a prompt
        while (createdQuestionsForDrawingPrompt.size() < nrOfQuestionPerPrompt) {
            QuizQuestion newDrawingQuestion;
            PotentialQuestion selectedPotentialQuestion = pickPotentialQuestionForPrompt(prompt);
            List<DrawingPromptAnswer> answersToPrompt = drawingPromptAnswerRepository.findAllByAssociatedGamePinAndAssociatedPromptNr(game.getGamePin(), prompt.getPromptNr());

            //transform the potential question into a quiz question
            if (selectedPotentialQuestion.getQuestionType() == QuestionType.PLAYER) {
                newDrawingQuestion = transformPotentialDrawingQuestionTypePlayer(selectedPotentialQuestion, answersToPrompt);
                newDrawingQuestion.setAnswerDisplayType(DisplayType.TEXT);
            }
            else {
                newDrawingQuestion = transformPotentialDrawingQuestionTypePromptAnswer(selectedPotentialQuestion, answersToPrompt);
                newDrawingQuestion.setAnswerDisplayType(DisplayType.IMAGE);
            }

            //avoid duplicate questions
            newDrawingQuestion = setToNullIfDuplicate(createdQuestionsForDrawingPrompt, newDrawingQuestion);
            if (newDrawingQuestion == null) {
                continue;
            }

            newDrawingQuestion = setRemainingValuesAndSaveQuizQuestion(newDrawingQuestion, game, prompt);
            createdQuestionsForDrawingPrompt.add(newDrawingQuestion);
        }

        return createdQuestionsForDrawingPrompt;
    }

    private List<QuizQuestion> generateQuestionsForTextPrompt(Prompt prompt, Game game) {
        List<QuizQuestion> createdQuestionsForTextPrompt = new ArrayList<>();

        while (createdQuestionsForTextPrompt.size() < nrOfQuestionPerPrompt) {
            QuizQuestion newTextQuestion;
            PotentialQuestion selectedPotentialQuestion = pickPotentialQuestionForPrompt(prompt);
            List<TextPromptAnswer> answersToPrompt = textPromptAnswerRepository.findAllByAssociatedGamePinAndAssociatedPromptNr(game.getGamePin(), prompt.getPromptNr());

            //transform the potential question into a quiz question
            if (selectedPotentialQuestion.getQuestionType() == QuestionType.PLAYER) {
                newTextQuestion = transformPotentialTextQuestionTypePlayer(selectedPotentialQuestion, answersToPrompt);
            }
            else {
                newTextQuestion = transformPotentialTextQuestionTypePromptAnswer(selectedPotentialQuestion, answersToPrompt);
            }
            newTextQuestion.setAnswerDisplayType(DisplayType.TEXT);

            //avoid duplicate questions
            newTextQuestion = setToNullIfDuplicate(createdQuestionsForTextPrompt, newTextQuestion);
            if (newTextQuestion == null) {
                continue;
            }
            newTextQuestion = setRemainingValuesAndSaveQuizQuestion(newTextQuestion, game, prompt);
            createdQuestionsForTextPrompt.add(newTextQuestion);
        }

        return createdQuestionsForTextPrompt;
    }

    private List<QuizQuestion> generateQuestionsForTrueFalsePrompt(Prompt prompt, Game game) {
        List<QuizQuestion> createdQuestionsForTFPrompt = new ArrayList<>();

        while (createdQuestionsForTFPrompt.size() < nrOfQuestionPerPrompt) {
            QuizQuestion newTFQuestion;
            PotentialQuestion selectedPotentialQuestion = pickPotentialQuestionForPrompt(prompt);
            List<TrueFalsePromptAnswer> answersToPrompt = trueFalsePromptAnswerRepository.findAllByAssociatedGamePinAndAssociatedPromptNr(game.getGamePin(), prompt.getPromptNr());

            //transform the potential question into a quiz question
            if (selectedPotentialQuestion.getQuestionType() == QuestionType.PLAYER) {
                newTFQuestion = transformPotentialTFQuestionTypePlayer(selectedPotentialQuestion, answersToPrompt);
            }
            else {
                newTFQuestion = transformPotentialTFQuestionTypeBoolean(selectedPotentialQuestion, answersToPrompt);
            }
            newTFQuestion.setAnswerDisplayType(DisplayType.TEXT);

            //avoid duplicate questions
            newTFQuestion = setToNullIfDuplicate(createdQuestionsForTFPrompt, newTFQuestion);
            if (newTFQuestion == null) {
                continue;
            }

            newTFQuestion = setRemainingValuesAndSaveQuizQuestion(newTFQuestion, game, prompt);
            createdQuestionsForTFPrompt.add(newTFQuestion);
        }

        return createdQuestionsForTFPrompt;
    }

    public QuizQuestion transformPotentialDrawingQuestionTypePlayer(PotentialQuestion pq, List<DrawingPromptAnswer> allAnswers) {
        QuizQuestion newQuestion = new QuizQuestion();

        //picked pq will ask which player drawing is from
        log.debug("Drawing question - Player");

        DrawingPromptAnswer selectedCorrectPromptAnswer = allAnswers.get(rand.nextInt(allAnswers.size()));
        Player correctAnswerPlayer = playerRepository.findByPlayerId(selectedCorrectPromptAnswer.getAssociatedPlayerId());

        newQuestion.setImageToDisplay(selectedCorrectPromptAnswer.getAnswerDrawing());

        newQuestion.setQuizQuestionText(pq.getQuestionText());

        log.debug("Set Question text to: %s with display: %s".formatted(newQuestion.getQuizQuestionText(), newQuestion.getImageToDisplay()));

        // set and save the correct answer option, and add this option to the question
        AnswerOption correctAnswer = saveAsAnswerOption(correctAnswerPlayer.getPlayerName(), newQuestion);
        newQuestion.setCorrectAnswer(correctAnswer);
        // remove used answer
        allAnswers.remove(selectedCorrectPromptAnswer);

        while (newQuestion.getAnswerOptions().size() < 4) {
            // get a random answer and its according player from allAnswers
            DrawingPromptAnswer selectedPromptAnswer = allAnswers.get(rand.nextInt(allAnswers.size()));
            Player selectedPromptPlayer = playerRepository.findByPlayerId(selectedPromptAnswer.getAssociatedPlayerId());

            // creat and set a new answer option with that according player
            saveAsAnswerOption(selectedPromptPlayer.getPlayerName(), newQuestion);
            allAnswers.remove(selectedPromptAnswer);
        }

        return newQuestion;
    }

    public QuizQuestion transformPotentialDrawingQuestionTypePromptAnswer(PotentialQuestion pq, List<DrawingPromptAnswer> allAnswers) {
        QuizQuestion newQuestion = new QuizQuestion();

        //picked pq will ask which drawing is from a specific player
        log.debug("Drawing question - PromptAnswer");
        DrawingPromptAnswer selectedCorrectPromptAnswer = allAnswers.get(rand.nextInt(allAnswers.size()));
        Player correctAnswerPlayer = playerRepository.findByPlayerId(selectedCorrectPromptAnswer.getAssociatedPlayerId());

        if (pq.isRequiresTextInput()) {
            newQuestion.setQuizQuestionText(String.format(pq.getQuestionText(), correctAnswerPlayer.getPlayerName()));
        }
        else {
            newQuestion.setQuizQuestionText(pq.getQuestionText());
        }
        log.debug("Set Question text to: %s".formatted(newQuestion.getQuizQuestionText()));

        AnswerOption correctAnswer = saveAsAnswerOption(selectedCorrectPromptAnswer.getAnswerDrawing(), newQuestion);
        newQuestion.setCorrectAnswer(correctAnswer);
        allAnswers.remove(selectedCorrectPromptAnswer);

        while (newQuestion.getAnswerOptions().size() < 4) {
            DrawingPromptAnswer selectedPromptAnswer = allAnswers.get(rand.nextInt(allAnswers.size()));

            saveAsAnswerOption(selectedPromptAnswer.getAnswerDrawing(), newQuestion);
            allAnswers.remove(selectedPromptAnswer);
        }

        return newQuestion;
    }

    public QuizQuestion transformPotentialTextQuestionTypePlayer(PotentialQuestion pq, List<TextPromptAnswer> allAnswers) {
        QuizQuestion newQuestion = new QuizQuestion();

        //picked pq will ask which player answer is from

        log.debug("Text question - Player");
        TextPromptAnswer selectedCorrectPromptAnswer = allAnswers.get(rand.nextInt(allAnswers.size()));
        Player correctAnswerPlayer = playerRepository.findByPlayerId(selectedCorrectPromptAnswer.getAssociatedPlayerId());

        if (pq.isRequiresTextInput()) {
            newQuestion.setQuizQuestionText(String.format(pq.getQuestionText(), selectedCorrectPromptAnswer.getAnswer()));
        }
        else {
            newQuestion.setQuizQuestionText(pq.getQuestionText());
        }
        log.debug("Set Question text to: %s".formatted(newQuestion.getQuizQuestionText()));

        AnswerOption correctAnswer = saveAsAnswerOption(correctAnswerPlayer.getPlayerName(), newQuestion);
        newQuestion.setCorrectAnswer(correctAnswer);
        allAnswers.remove(selectedCorrectPromptAnswer);

        while (newQuestion.getAnswerOptions().size() < 4) {
            TextPromptAnswer selectedPromptAnswer = allAnswers.get(rand.nextInt(allAnswers.size()));
            Player selectedPromptPlayer = playerRepository.findByPlayerId(selectedPromptAnswer.getAssociatedPlayerId());

            saveAsAnswerOption(selectedPromptPlayer.getPlayerName(), newQuestion);
            allAnswers.remove(selectedPromptAnswer);
        }

        return newQuestion;
    }

    public QuizQuestion transformPotentialTextQuestionTypePromptAnswer(PotentialQuestion pq, List<TextPromptAnswer> allAnswers) {
        QuizQuestion newQuestion = new QuizQuestion();

        //picked pq will ask which answer is from a specific player
        log.debug("Text question - PromptAnswer");
        TextPromptAnswer selectedCorrectPromptAnswer = allAnswers.get(rand.nextInt(allAnswers.size()));
        Player correctAnswerPlayer = playerRepository.findByPlayerId(selectedCorrectPromptAnswer.getAssociatedPlayerId());

        if (pq.isRequiresTextInput()) {
            newQuestion.setQuizQuestionText(String.format(pq.getQuestionText(), correctAnswerPlayer.getPlayerName()));
        }
        else {
            newQuestion.setQuizQuestionText(pq.getQuestionText());
        }
        log.debug("Set Question text to: %s".formatted(newQuestion.getQuizQuestionText()));

        AnswerOption correctAnswer = saveAsAnswerOption(selectedCorrectPromptAnswer.getAnswer(), newQuestion);
        newQuestion.setCorrectAnswer(correctAnswer);
        allAnswers.remove(selectedCorrectPromptAnswer);

        while (newQuestion.getAnswerOptions().size() < 4) {
            TextPromptAnswer selectedPromptAnswer = allAnswers.get(rand.nextInt(allAnswers.size()));

            saveAsAnswerOption(selectedPromptAnswer.getAnswer(), newQuestion);
            allAnswers.remove(selectedPromptAnswer);
        }

        return newQuestion;
    }

    public QuizQuestion transformPotentialTFQuestionTypePlayer(PotentialQuestion pq, List<TrueFalsePromptAnswer> allAnswers) {
        QuizQuestion newQuestion = new QuizQuestion();

        //picked pq will ask which player story is from

        log.debug("tf question - Player");

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

        newQuestion.setStoryToDisplay(selectedCorrectPromptAnswer.getAnswerText());

        log.debug("Set Question text to: %s with display: %s".formatted(newQuestion.getQuizQuestionText(), newQuestion.getStoryToDisplay()));

        AnswerOption correctAnswer = saveAsAnswerOption(correctAnswerPlayer.getPlayerName(), newQuestion);
        newQuestion.setCorrectAnswer(correctAnswer);
        allAnswers.remove(selectedCorrectPromptAnswer);

        while (newQuestion.getAnswerOptions().size() < 4) {
            TrueFalsePromptAnswer selectedPromptAnswer = allAnswers.get(rand.nextInt(allAnswers.size()));
            Player selectedPromptPlayer = playerRepository.findByPlayerId(selectedPromptAnswer.getAssociatedPlayerId());

            saveAsAnswerOption(selectedPromptPlayer.getPlayerName(), newQuestion);
            allAnswers.remove(selectedPromptAnswer);
        }

        return newQuestion;
    }

    public QuizQuestion transformPotentialTFQuestionTypeBoolean(PotentialQuestion pq, List<TrueFalsePromptAnswer> allAnswers) {
        QuizQuestion newQuestion = new QuizQuestion();

        //picked pq will ask whether story by specific user is true

        log.debug("Drawing question - Boolean");

        TrueFalsePromptAnswer selectedCorrectPromptAnswer = allAnswers.get(rand.nextInt(allAnswers.size()));
        Player correctAnswerPlayer = playerRepository.findByPlayerId(selectedCorrectPromptAnswer.getAssociatedPlayerId());

        if (pq.isRequiresTextInput()) {
            newQuestion.setQuizQuestionText(String.format(pq.getQuestionText(), correctAnswerPlayer.getPlayerName()));
        }
        else {
            newQuestion.setQuizQuestionText(pq.getQuestionText());
        }

        newQuestion.setStoryToDisplay(selectedCorrectPromptAnswer.getAnswerText());

        log.debug("Set Question text to: %s with display: %s".formatted(newQuestion.getQuizQuestionText(), newQuestion.getStoryToDisplay()));

        AnswerOption correctAnswer = saveAsAnswerOption(selectedCorrectPromptAnswer.getAnswerBoolean().toString(), newQuestion);
        newQuestion.setCorrectAnswer(correctAnswer);
        allAnswers.remove(selectedCorrectPromptAnswer);

        if (selectedCorrectPromptAnswer.getAnswerBoolean()) {
            saveAsAnswerOption("false", newQuestion);
        }
        else {
            saveAsAnswerOption("true", newQuestion);
        }

        return newQuestion;
    }


    private PotentialQuestion pickPotentialQuestionForPrompt(Prompt prompt) {
        List<PotentialQuestion> potentialQuestions = pqRepository.findAllByAssociatedPrompt(prompt);
        return potentialQuestions.get(rand.nextInt(potentialQuestions.size()));
    }

    private AnswerOption saveAsAnswerOption(String answerText, QuizQuestion newQuestion) {
        AnswerOption answer = new AnswerOption();
        answer.setAnswerOptionText(answerText);
        newQuestion.setCorrectAnswer(answer);
        newQuestion.addAnswerOption(answer);
        answerOptionRepository.save(answer);
        answerOptionRepository.flush();
        return answer;
    }

    private QuizQuestion setToNullIfDuplicate(List<QuizQuestion> alreadyCreatedQuestions, QuizQuestion newQuestion) {
        if (newQuestion == null) {
            return null;
        }
        for (QuizQuestion q : alreadyCreatedQuestions) {
            //check if there is already a question with the same questionText and the same correctAnswer
            //TODO: better checks for using the same prompt as correct in different ways?
            if (q.getQuizQuestionText().equals(newQuestion.getQuizQuestionText()) && q.getCorrectAnswer().getAnswerOptionText().equals(newQuestion.getCorrectAnswer().getAnswerOptionText())) {
                return null;
            }
        }
        return newQuestion;
    }

    private QuizQuestion setRemainingValuesAndSaveQuizQuestion(QuizQuestion newQuestion, Game associatedGame, Prompt associatedPrompt) {
        shuffleQuizAnswers(newQuestion);
        newQuestion.setAssociatedGamePin(associatedGame.getGamePin());
        newQuestion.setAssociatedPrompt(associatedPrompt);
        newQuestion = qqRepository.save(newQuestion);
        qqRepository.flush();
        return newQuestion;
    }

    private void shuffleQuizAnswers(QuizQuestion quizQuestionToShuffle) {
        Collections.shuffle(quizQuestionToShuffle.getAnswerOptions());
    }
}
