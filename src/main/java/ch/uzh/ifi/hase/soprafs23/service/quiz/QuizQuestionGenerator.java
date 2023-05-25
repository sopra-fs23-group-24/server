package ch.uzh.ifi.hase.soprafs23.service.quiz;

import ch.uzh.ifi.hase.soprafs23.constant.CompletionStatus;
import ch.uzh.ifi.hase.soprafs23.constant.DisplayType;
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

    private static final int NR_OF_QUESTIONS_PER_PROMPT = 2;
    private List<QuizQuestion> createdQuestions = new ArrayList<>();

    @Autowired
    public QuizQuestionGenerator(@Qualifier("quizQuestionRepository") QuizQuestionRepository qqRepository,
                                 @Qualifier("potentialQuestionRepository") PotentialQuestionRepository pqRepository,
                                 @Qualifier("textPromptAnswerRepository") TextPromptAnswerRepository textPromptAnswerRepository,
                                 @Qualifier("trueFalsePromptAnswerRepository") TrueFalsePromptAnswerRepository trueFalsePromptAnswerRepository,
                                 @Qualifier("drawingPromptAnswerRepository") DrawingPromptAnswerRepository drawingPromptAnswerRepository,
                                 @Qualifier("answerOptionRepository") AnswerOptionRepository answerOptionRepository,
                                 @Qualifier("playerRepository") PlayerRepository playerRepository,
                                 @Qualifier("gameRepository") GameRepository gameRepository)
            throws NoSuchAlgorithmException { // this Exception is needed
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
        createdQuestions = new ArrayList<>();
        Game gameByPin = gameRepository.findByGamePin(gamePin);
        if (gameByPin == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No game with this pin found.");
        }

        List<Player> allPlayersInRandomOrder;
        List<Prompt> allPromptsInRandomOrder;
        allPromptsInRandomOrder = reshufflePrompts(gameByPin.getPromptSet());
        log.debug("Started generating quiz questions.");

        //generate at least the desired number of questions per prompt
        while (createdQuestions.size() < gameByPin.getPromptSet().size() * NR_OF_QUESTIONS_PER_PROMPT) {
            allPlayersInRandomOrder = new ArrayList<>(gameByPin.getPlayerGroup());
            Collections.shuffle(allPlayersInRandomOrder);
            log.debug("New question is being generated.");

            //make sure each players gets one question per iteration
            while (!allPlayersInRandomOrder.isEmpty()) {
                if (allPromptsInRandomOrder.isEmpty()) {
                    allPromptsInRandomOrder = reshufflePrompts(gameByPin.getPromptSet());
                }
                Prompt currentPrompt = allPromptsInRandomOrder.get(0);
                log.debug("Current prompt: {}", currentPrompt.getPromptText());
                Player pickedPlayer = allPlayersInRandomOrder.get(0);
                log.debug("Current player: {}", pickedPlayer.getPlayerName());
                QuizQuestion questionForPrompt = new QuizQuestion();
                switch (currentPrompt.getPromptType()) {
                    case DRAWING ->
                            questionForPrompt = generateQuestionForDrawingPrompt(currentPrompt, pickedPlayer, gameByPin);
                    case TEXT ->
                            questionForPrompt = generateQuestionForTextPrompt(currentPrompt, pickedPlayer, gameByPin);
                    case TRUEFALSE ->
                            questionForPrompt = generateQuestionForTrueFalsePrompt(currentPrompt, pickedPlayer, gameByPin);
                }
                //if question could successfully be generated, add question and remove player and prompt for remaining iteration
                if (questionForPrompt != null) {
                    log.debug("This question could be generated successfully.");
                    createdQuestions.add(questionForPrompt);
                    allPromptsInRandomOrder.remove(currentPrompt);
                    allPlayersInRandomOrder.remove(pickedPlayer);
                }
                //if question could not successfully be generated, try with next prompt and move the current one to end of list, if it's the last prompt reshuffle
                else {
                    log.debug("The question could not be generated successfully.");
                    if (allPromptsInRandomOrder.size() > 1) {
                        allPromptsInRandomOrder.remove(currentPrompt);
                        allPromptsInRandomOrder.add(currentPrompt);
                    }
                    else {
                        allPromptsInRandomOrder = reshufflePrompts(gameByPin.getPromptSet());
                    }
                }
                log.debug("end of question \n");
            }
        }
        log.debug("Generated {} quiz questions in total.", createdQuestions.size());
        Collections.shuffle(createdQuestions);

        gameByPin.addQuizQuestions(createdQuestions);
        gameByPin.nextQuestion();

        gameRepository.save(gameByPin);
        gameRepository.flush();

        return createdQuestions;
    }


    public QuizQuestion generateQuestionForDrawingPrompt(Prompt prompt, Player pickedPlayer, Game game) {

        QuizQuestion newDrawingQuestion;
        PotentialQuestion selectedPotentialQuestion = pickPotentialQuestionForPrompt(prompt);
        List<DrawingPromptAnswer> answersToPrompt = drawingPromptAnswerRepository.findAllByAssociatedGamePinAndAssociatedPromptNr(game.getGamePin(), prompt.getPromptNr());

        //transform the potential question into a quiz question
        if (selectedPotentialQuestion.getQuestionType() == QuestionType.PLAYER) {
            newDrawingQuestion = transformPotentialDrawingQuestionTypePlayer(selectedPotentialQuestion, answersToPrompt, pickedPlayer);
        }
        else {
            newDrawingQuestion = transformPotentialDrawingQuestionTypePromptAnswer(selectedPotentialQuestion, answersToPrompt, pickedPlayer);
        }

        //avoid duplicate questions
        newDrawingQuestion = setToNullIfDuplicate(newDrawingQuestion);
        if (newDrawingQuestion != null) {
            newDrawingQuestion = setRemainingValuesAndSaveQuizQuestion(newDrawingQuestion, game, prompt, selectedPotentialQuestion);
        }

        return newDrawingQuestion;
    }

    public QuizQuestion generateQuestionForTextPrompt(Prompt prompt, Player pickedPlayer, Game game) {

        QuizQuestion newTextQuestion;
        PotentialQuestion selectedPotentialQuestion = pickPotentialQuestionForPrompt(prompt);
        List<TextPromptAnswer> answersToPrompt = textPromptAnswerRepository.findAllByAssociatedGamePinAndAssociatedPromptNr(game.getGamePin(), prompt.getPromptNr());

        //transform the potential question into a quiz question
        if (selectedPotentialQuestion.getQuestionType() == QuestionType.PLAYER) {
            newTextQuestion = transformPotentialTextQuestionTypePlayer(selectedPotentialQuestion, answersToPrompt, pickedPlayer);
        }
        else {
            newTextQuestion = transformPotentialTextQuestionTypePromptAnswer(selectedPotentialQuestion, answersToPrompt, pickedPlayer);
        }

        //avoid duplicate questions
        newTextQuestion = setToNullIfDuplicate(newTextQuestion);
        if (newTextQuestion != null) {
            newTextQuestion = setRemainingValuesAndSaveQuizQuestion(newTextQuestion, game, prompt, selectedPotentialQuestion);
        }

        return newTextQuestion;
    }

    private QuizQuestion generateQuestionForTrueFalsePrompt(Prompt prompt, Player pickedPlayer, Game game) {

        QuizQuestion newTFQuestion;
        PotentialQuestion selectedPotentialQuestion = pickPotentialQuestionForPrompt(prompt);
        List<TrueFalsePromptAnswer> answersToPrompt = trueFalsePromptAnswerRepository.findAllByAssociatedGamePinAndAssociatedPromptNr(game.getGamePin(), prompt.getPromptNr());

        //transform the potential question into a quiz question
        if (selectedPotentialQuestion.getQuestionType() == QuestionType.PLAYER) {
            newTFQuestion = transformPotentialTFQuestionTypePlayer(selectedPotentialQuestion, answersToPrompt, pickedPlayer);
        }
        else {
            newTFQuestion = transformPotentialTFQuestionTypeBoolean(selectedPotentialQuestion, answersToPrompt, pickedPlayer);

            //if failed to create a player question (because selected player did not tell a true story), try generating a boolean question instead
            if (newTFQuestion == null) {
                selectedPotentialQuestion = pqRepository.findByAssociatedPromptAndQuestionType(prompt, QuestionType.PLAYER);
                newTFQuestion = transformPotentialTFQuestionTypePlayer(selectedPotentialQuestion, answersToPrompt, pickedPlayer);
            }
        }

        //avoid duplicate questions
        newTFQuestion = setToNullIfDuplicate(newTFQuestion);
        if (newTFQuestion != null) {
            newTFQuestion = setRemainingValuesAndSaveQuizQuestion(newTFQuestion, game, prompt, selectedPotentialQuestion);
        }

        return newTFQuestion;
    }

    public QuizQuestion transformPotentialDrawingQuestionTypePlayer(PotentialQuestion pq, List<DrawingPromptAnswer> allAnswers, Player pickedCorrectPlayer) {
        QuizQuestion newQuestion = new QuizQuestion();

        //picked pq will ask which player drawing is from
        log.debug("Drawing question - Player");

        DrawingPromptAnswer selectedCorrectPromptAnswer = drawingPromptAnswerRepository.findDrawingPromptAnswerByAssociatedPlayerIdAndAssociatedPromptNr(pickedCorrectPlayer.getPlayerId(), pq.getAssociatedPrompt().getPromptNr());
        if (selectedCorrectPromptAnswer.isUsedAsCorrectAnswer()) {
            log.debug("Selected prompt has already been used as correct answer, will not transform question.");
            return null;
        }
        selectedCorrectPromptAnswer.setUsedAsCorrectAnswer(true);
        drawingPromptAnswerRepository.saveAndFlush(selectedCorrectPromptAnswer);
        log.debug("for prompt of {} set to true", selectedCorrectPromptAnswer.getAssociatedPlayerId());

        newQuestion.setImageToDisplay(selectedCorrectPromptAnswer.getAnswerDrawing());

        newQuestion.setQuizQuestionText(generateQuizQuestionText(pq, selectedCorrectPromptAnswer.getAnswerDrawing()));

        log.debug("Set Question text to: %s with display: %s".formatted(newQuestion.getQuizQuestionText(), newQuestion.getImageToDisplay()));

        // set and save the correct answer option, and add this option to the question
        AnswerOption correctAnswer = saveAsAnswerOption(pickedCorrectPlayer.getPlayerName(), newQuestion);
        newQuestion.setCorrectAnswer(correctAnswer);

        // remove used answer
        allAnswers.remove(selectedCorrectPromptAnswer);
        log.debug("Correct answer:{}", newQuestion.getCorrectAnswer().getAnswerOptionText());

        while (newQuestion.getAnswerOptions().size() < 4) {
            // get a random answer and its according player from allAnswers
            DrawingPromptAnswer selectedPromptAnswer = allAnswers.get(rand.nextInt(allAnswers.size()));
            Player selectedPromptPlayer = playerRepository.findByPlayerId(selectedPromptAnswer.getAssociatedPlayerId());

            // creat and set a new answer option with that according player
            AnswerOption wrongAnswer = saveAsAnswerOption(selectedPromptPlayer.getPlayerName(), newQuestion);
            log.debug("Incorrect answer:{}", wrongAnswer.getAnswerOptionText());
            allAnswers.remove(selectedPromptAnswer);
        }

        log.debug("new question is: {}", newQuestion);
        return newQuestion;
    }

    public QuizQuestion transformPotentialDrawingQuestionTypePromptAnswer(PotentialQuestion pq, List<DrawingPromptAnswer> allAnswers, Player pickedCorrectPlayer) {
        QuizQuestion newQuestion = new QuizQuestion();

        //picked pq will ask which drawing is from a specific player
        log.debug("Drawing question - PromptAnswer");

        DrawingPromptAnswer selectedCorrectPromptAnswer = drawingPromptAnswerRepository.findDrawingPromptAnswerByAssociatedPlayerIdAndAssociatedPromptNr(pickedCorrectPlayer.getPlayerId(), pq.getAssociatedPrompt().getPromptNr());
        if (selectedCorrectPromptAnswer.isUsedAsCorrectAnswer()) {
            log.debug("Selected prompt has already been used as correct answer, will not transform question.");
            return null;
        }
        selectedCorrectPromptAnswer.setUsedAsCorrectAnswer(true);
        drawingPromptAnswerRepository.saveAndFlush(selectedCorrectPromptAnswer);
        log.debug("for prompt of {} set to true", CompletionStatus.NOT_FINISHED);

        newQuestion.setQuizQuestionText(generateQuizQuestionText(pq, pickedCorrectPlayer.getPlayerName()));

        log.debug("Set Question text to: %s".formatted(newQuestion.getQuizQuestionText()));

        AnswerOption correctAnswer = saveAsAnswerOption(selectedCorrectPromptAnswer.getAnswerDrawing(), newQuestion);
        newQuestion.setCorrectAnswer(correctAnswer);
        allAnswers.remove(selectedCorrectPromptAnswer);
        log.debug("Correct answer:{}", newQuestion.getCorrectAnswer().getAnswerOptionText());

        while (newQuestion.getAnswerOptions().size() < 4) {
            DrawingPromptAnswer selectedPromptAnswer = allAnswers.get(rand.nextInt(allAnswers.size()));

            AnswerOption wrongAnswer = saveAsAnswerOption(selectedPromptAnswer.getAnswerDrawing(), newQuestion);
            allAnswers.remove(selectedPromptAnswer);
            log.debug("Incorrect answer:{}", wrongAnswer.getAnswerOptionText());
        }

        log.debug("new question is: {}", newQuestion);
        return newQuestion;
    }

    public QuizQuestion transformPotentialTextQuestionTypePlayer(PotentialQuestion pq, List<TextPromptAnswer> allAnswers, Player pickedCorrectPlayer) {
        QuizQuestion newQuestion = new QuizQuestion();

        //picked pq will ask which player answer is from
        log.debug("Text question - Player");

        TextPromptAnswer selectedCorrectPromptAnswer = textPromptAnswerRepository.findTextPromptAnswerByAssociatedPlayerIdAndAssociatedPromptNr(pickedCorrectPlayer.getPlayerId(), pq.getAssociatedPrompt().getPromptNr());
        if (selectedCorrectPromptAnswer.isUsedAsCorrectAnswer()) {
            log.debug("Selected prompt has already been used as correct answer, will not transform question.");
            return null;
        }
        selectedCorrectPromptAnswer.setUsedAsCorrectAnswer(true);
        textPromptAnswerRepository.saveAndFlush(selectedCorrectPromptAnswer);

        newQuestion.setQuizQuestionText(generateQuizQuestionText(pq, selectedCorrectPromptAnswer.getAnswer()));

        log.debug("Set Question text to: %s".formatted(newQuestion.getQuizQuestionText()));

        AnswerOption correctAnswer = saveAsAnswerOption(pickedCorrectPlayer.getPlayerName(), newQuestion);
        newQuestion.setCorrectAnswer(correctAnswer);
        allAnswers.remove(selectedCorrectPromptAnswer);
        log.debug("Correct answer:{}", newQuestion.getCorrectAnswer().getAnswerOptionText());

        while (newQuestion.getAnswerOptions().size() < 4) {
            TextPromptAnswer selectedPromptAnswer = allAnswers.get(rand.nextInt(allAnswers.size()));
            Player selectedPromptPlayer = playerRepository.findByPlayerId(selectedPromptAnswer.getAssociatedPlayerId());

            AnswerOption wrongAnswer = saveAsAnswerOption(selectedPromptPlayer.getPlayerName(), newQuestion);
            allAnswers.remove(selectedPromptAnswer);
            log.debug("Incorrect answer:{}", wrongAnswer.getAnswerOptionText());
        }

        return newQuestion;
    }

    public QuizQuestion transformPotentialTextQuestionTypePromptAnswer(PotentialQuestion pq, List<TextPromptAnswer> allAnswers, Player pickedCorrectPlayer) {
        QuizQuestion newQuestion = new QuizQuestion();

        //picked pq will ask which answer is from a specific player
        log.debug("Text question - PromptAnswer");

        TextPromptAnswer selectedCorrectPromptAnswer = textPromptAnswerRepository.findTextPromptAnswerByAssociatedPlayerIdAndAssociatedPromptNr(pickedCorrectPlayer.getPlayerId(), pq.getAssociatedPrompt().getPromptNr());
        if (selectedCorrectPromptAnswer.isUsedAsCorrectAnswer()) {
            log.debug("Selected prompt has already been used as correct answer, will not transform question.");
            return null;
        }
        selectedCorrectPromptAnswer.setUsedAsCorrectAnswer(true);
        textPromptAnswerRepository.saveAndFlush(selectedCorrectPromptAnswer);

        newQuestion.setQuizQuestionText(generateQuizQuestionText(pq, pickedCorrectPlayer.getPlayerName()));

        log.debug("Set Question text to: %s".formatted(newQuestion.getQuizQuestionText()));

        AnswerOption correctAnswer = saveAsAnswerOption(selectedCorrectPromptAnswer.getAnswer(), newQuestion);
        newQuestion.setCorrectAnswer(correctAnswer);
        allAnswers.remove(selectedCorrectPromptAnswer);
        log.debug("Correct answer:{}", newQuestion.getCorrectAnswer().getAnswerOptionText());

        while (newQuestion.getAnswerOptions().size() < 4) {
            TextPromptAnswer selectedPromptAnswer = allAnswers.get(rand.nextInt(allAnswers.size()));

            AnswerOption wrongAnswer = saveAsAnswerOption(selectedPromptAnswer.getAnswer(), newQuestion);
            allAnswers.remove(selectedPromptAnswer);
            log.debug("Incorrect answer:{}", wrongAnswer.getAnswerOptionText());
        }

        return newQuestion;
    }

    public QuizQuestion transformPotentialTFQuestionTypePlayer(PotentialQuestion pq, List<TrueFalsePromptAnswer> allAnswers, Player pickedCorrectPlayer) {
        QuizQuestion newQuestion = new QuizQuestion();

        //picked pq will ask which player story is from
        log.debug("tf question - Player");

        //if the selected player did not tell a true story, then this type of QuizQuestion cannot be generated for this player
        TrueFalsePromptAnswer selectedCorrectPromptAnswer = trueFalsePromptAnswerRepository.findTrueFalsePromptAnswerByAssociatedPlayerIdAndAssociatedPromptNr(pickedCorrectPlayer.getPlayerId(), pq.getAssociatedPrompt().getPromptNr());
        if (Boolean.FALSE.equals(selectedCorrectPromptAnswer.getAnswerBoolean())) {
            log.debug("Selected prompt was an untrue story and hence does not fit requirement, will not transform question.");
            return null;
        }
        if (selectedCorrectPromptAnswer.isUsedAsCorrectAnswer()) {
            log.debug("Selected prompt has already been used as correct answer, will not transform question.");
            return null;
        }
        selectedCorrectPromptAnswer.setUsedAsCorrectAnswer(true);
        trueFalsePromptAnswerRepository.saveAndFlush(selectedCorrectPromptAnswer);

        newQuestion.setQuizQuestionText(generateQuizQuestionText(pq, selectedCorrectPromptAnswer.getAnswerText()));

        newQuestion.setStoryToDisplay(selectedCorrectPromptAnswer.getAnswerText());

        log.debug("Set Question text to: %s with display: %s".formatted(newQuestion.getQuizQuestionText(), newQuestion.getStoryToDisplay()));

        AnswerOption correctAnswer = saveAsAnswerOption(pickedCorrectPlayer.getPlayerName(), newQuestion);
        newQuestion.setCorrectAnswer(correctAnswer);
        allAnswers.remove(selectedCorrectPromptAnswer);
        log.debug("Correct answer:{}", newQuestion.getCorrectAnswer().getAnswerOptionText());

        while (newQuestion.getAnswerOptions().size() < 4) {
            TrueFalsePromptAnswer selectedPromptAnswer = allAnswers.get(rand.nextInt(allAnswers.size()));
            Player selectedPromptPlayer = playerRepository.findByPlayerId(selectedPromptAnswer.getAssociatedPlayerId());

            AnswerOption wrongAnswer = saveAsAnswerOption(selectedPromptPlayer.getPlayerName(), newQuestion);
            allAnswers.remove(selectedPromptAnswer);
            log.debug("Incorrect answer:{}", wrongAnswer.getAnswerOptionText());
        }

        return newQuestion;
    }

    public QuizQuestion transformPotentialTFQuestionTypeBoolean(PotentialQuestion pq, List<TrueFalsePromptAnswer> allAnswers, Player pickedCorrectPlayer) {
        QuizQuestion newQuestion = new QuizQuestion();

        //picked pq will ask whether story by specific user is true
        log.debug("Drawing question - Boolean");

        TrueFalsePromptAnswer selectedCorrectPromptAnswer = trueFalsePromptAnswerRepository.findTrueFalsePromptAnswerByAssociatedPlayerIdAndAssociatedPromptNr(pickedCorrectPlayer.getPlayerId(), pq.getAssociatedPrompt().getPromptNr());
        if (selectedCorrectPromptAnswer.isUsedAsCorrectAnswer()) {
            log.debug("Selected prompt has already been used as correct answer, will not transform question.");
            return null;
        }
        selectedCorrectPromptAnswer.setUsedAsCorrectAnswer(true);
        trueFalsePromptAnswerRepository.saveAndFlush(selectedCorrectPromptAnswer);

        newQuestion.setQuizQuestionText(generateQuizQuestionText(pq, pickedCorrectPlayer.getPlayerName()));

        newQuestion.setStoryToDisplay(selectedCorrectPromptAnswer.getAnswerText());

        log.debug("Set Question text to: %s with display: %s".formatted(newQuestion.getQuizQuestionText(), newQuestion.getStoryToDisplay()));

        AnswerOption correctAnswer = saveAsAnswerOption(selectedCorrectPromptAnswer.getAnswerBoolean().toString(), newQuestion);
        newQuestion.setCorrectAnswer(correctAnswer);
        allAnswers.remove(selectedCorrectPromptAnswer);
        log.debug("Correct answer:{}", newQuestion.getCorrectAnswer().getAnswerOptionText());

        if (Boolean.TRUE.equals(selectedCorrectPromptAnswer.getAnswerBoolean())) {
            AnswerOption wrongAnswer = saveAsAnswerOption("false", newQuestion);
            log.debug("Incorrect answer:{}", wrongAnswer.getAnswerOptionText());
        }
        else {
            AnswerOption wrongAnswer = saveAsAnswerOption("true", newQuestion);
            log.debug("Incorrect answer:{}", wrongAnswer.getAnswerOptionText());
        }

        return newQuestion;
    }


    private List<Prompt> reshufflePrompts(List<Prompt> promptsOfGame) {
        List<Prompt> allPromptsInRandomOrder = new ArrayList<>(promptsOfGame);
        Collections.shuffle(allPromptsInRandomOrder);
        return allPromptsInRandomOrder;
    }

    private PotentialQuestion pickPotentialQuestionForPrompt(Prompt prompt) {
        List<PotentialQuestion> potentialQuestions = pqRepository.findAllByAssociatedPrompt(prompt);
        return potentialQuestions.get(rand.nextInt(potentialQuestions.size()));
    }

    private AnswerOption saveAsAnswerOption(String answerText, QuizQuestion newQuestion) {
        AnswerOption answer = new AnswerOption();
        answer.setAnswerOptionText(answerText);
        newQuestion.addAnswerOption(answer);
        answerOptionRepository.save(answer);
        answerOptionRepository.flush();
        return answer;
    }

    private String generateQuizQuestionText(PotentialQuestion potentialQuestion, String textToAddIfNeeded) {
        String generatedText;
        if (potentialQuestion.isRequiresTextInput()) {
            generatedText = String.format(potentialQuestion.getQuestionText(), textToAddIfNeeded);
        }
        else {
            generatedText = potentialQuestion.getQuestionText();
        }
        return generatedText;
    }

    private QuizQuestion setToNullIfDuplicate(QuizQuestion newQuestion) {
        if (newQuestion == null) {
            log.debug("Question creation failed during transformation, returned null.");
            return null;
        }
        for (QuizQuestion q : createdQuestions) {
            //check if there is already a question with the same questionText and the same correctAnswer
            if (q.getQuizQuestionText().equals(newQuestion.getQuizQuestionText()) && q.getCorrectAnswer().getAnswerOptionText().equals(newQuestion.getCorrectAnswer().getAnswerOptionText())) {
                log.debug("Question is duplicate, will not be generated.");
                return null;
            }
        }
        return newQuestion;
    }

    private QuizQuestion setRemainingValuesAndSaveQuizQuestion(QuizQuestion newQuestion, Game associatedGame, Prompt associatedPrompt, PotentialQuestion potentialQuestion) {
        shuffleQuizAnswers(newQuestion);
        newQuestion.setAssociatedGamePin(associatedGame.getGamePin());
        newQuestion.setAssociatedPrompt(associatedPrompt);
        if (associatedPrompt.getPromptType() == PromptType.DRAWING && potentialQuestion.getQuestionType() == QuestionType.PROMPTANSWER) {
            newQuestion.setAnswerDisplayType(DisplayType.IMAGE);
        }
        else {
            newQuestion.setAnswerDisplayType(DisplayType.TEXT);
        }

        newQuestion = qqRepository.save(newQuestion);
        qqRepository.flush();
        return newQuestion;
    }

    private void shuffleQuizAnswers(QuizQuestion quizQuestionToShuffle) {
        Collections.shuffle(quizQuestionToShuffle.getAnswerOptions());
    }
}
