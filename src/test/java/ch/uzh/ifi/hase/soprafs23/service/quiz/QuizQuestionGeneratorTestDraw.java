package ch.uzh.ifi.hase.soprafs23.service.quiz;

import ch.uzh.ifi.hase.soprafs23.constant.*;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.DrawingPromptAnswer;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.PotentialQuestion;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.Prompt;
import ch.uzh.ifi.hase.soprafs23.entity.quiz.AnswerOption;
import ch.uzh.ifi.hase.soprafs23.entity.quiz.QuizQuestion;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.prompt.PromptRepository;
import ch.uzh.ifi.hase.soprafs23.repository.quiz.AnswerOptionRepository;
import ch.uzh.ifi.hase.soprafs23.repository.quiz.QuizQuestionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

public class QuizQuestionGeneratorTestDraw {
    private final List<Player> testPlayers = new ArrayList<>();
    private final List<DrawingPromptAnswer> testDrawingAnswers = new ArrayList<>();
    private Prompt drawTestPrompt;
    private PotentialQuestion potentialDrawingQuestionPROMPTANSWER;
    private PotentialQuestion potentialDrawingQuestionPLAYER;
    private QuizQuestion testQuizQuestion;
    private Game testGame;
    @Mock
    private QuizQuestionRepository qqRepository;
    @Mock
    private PlayerRepository playerRepository;
    @Mock
    private PromptRepository promptRepository;
    @Mock
    private AnswerOptionRepository answerOptionRepository;

    @InjectMocks
    private QuizQuestionGenerator quizQuestionGenerator;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        testGame = new Game();
        testGame.setGamePin("123456");
        testGame.setStatus(GameStatus.PROMPT);
        setUpPlayers();
        testGame.setPlayerGroup(testPlayers);
        testGame.setHostId(testPlayers.get(0).getPlayerId());
        setUpPromptAndPotentialQuestion();
        testGame.setPromptSet(List.of(drawTestPrompt));

        setUpDrawingPromptAnswers();

        Mockito.when(qqRepository.save(Mockito.any())).thenReturn(testQuizQuestion);

    }

    @Test
    public void transformPotentialQuestionDrawing_PLAYER() {

        QuizQuestion generatedQuestion = quizQuestionGenerator.transformPotentialQuestionDrawing(potentialDrawingQuestionPLAYER, new ArrayList<>(testDrawingAnswers));

        Assertions.assertEquals(generatedQuestion.getQuestionStatus(), CompletionStatus.NOT_FINISHED);

        Assertions.assertEquals(generatedQuestion.getAnswerOptions().size(), 4);
        for (AnswerOption ao : generatedQuestion.getAnswerOptions()) {
            assert (ao.getAnswerOptionText().equals(testPlayers.get(0).getPlayerName())
                    || ao.getAnswerOptionText().equals(testPlayers.get(1).getPlayerName())
                    || ao.getAnswerOptionText().equals(testPlayers.get(2).getPlayerName())
                    || ao.getAnswerOptionText().equals(testPlayers.get(3).getPlayerName())
            );
        }
        Assertions.assertNotNull(generatedQuestion.getQuizQuestionText());
        Assertions.assertEquals(generatedQuestion.getQuizQuestionText(), potentialDrawingQuestionPLAYER.getQuestionText());

        Assertions.assertNotNull(generatedQuestion.getCorrectAnswer());
        Assertions.assertTrue(generatedQuestion.getAnswerOptions().contains(generatedQuestion.getCorrectAnswer()));

        Assertions.assertTrue(generatedQuestion.getReceivedAnswers().isEmpty());

        Assertions.assertNotNull(generatedQuestion.getQuizQuestionText());
        Assertions.assertNotNull(generatedQuestion.getImageToDisplay());
        Assertions.assertNull(generatedQuestion.getStoryToDisplay());
    }

    @Test
    public void transformPotentialQuestionDrawing_PROMPANSWER() {

        QuizQuestion generatedQuestion = quizQuestionGenerator.transformPotentialQuestionDrawing(potentialDrawingQuestionPROMPTANSWER, new ArrayList<>(testDrawingAnswers));

        Assertions.assertEquals(generatedQuestion.getQuestionStatus(), CompletionStatus.NOT_FINISHED);

        Assertions.assertEquals(generatedQuestion.getAnswerOptions().size(), 4);
        for (AnswerOption ao : generatedQuestion.getAnswerOptions()) {
            assert (ao.getAnswerOptionText().equals(testDrawingAnswers.get(0).getAnswerDrawing())
                    || ao.getAnswerOptionText().equals(testDrawingAnswers.get(1).getAnswerDrawing())
                    || ao.getAnswerOptionText().equals(testDrawingAnswers.get(2).getAnswerDrawing())
                    || ao.getAnswerOptionText().equals(testDrawingAnswers.get(3).getAnswerDrawing())
            );
        }

        Assertions.assertNotNull(generatedQuestion.getQuizQuestionText());

        Assertions.assertNotNull(generatedQuestion.getCorrectAnswer());
        Assertions.assertTrue(generatedQuestion.getAnswerOptions().contains(generatedQuestion.getCorrectAnswer()));

        Assertions.assertTrue(generatedQuestion.getReceivedAnswers().isEmpty());

        Assertions.assertNull(generatedQuestion.getImageToDisplay());
        Assertions.assertNull(generatedQuestion.getStoryToDisplay());
    }


    /**
     * Set Up Methods, called in setup
     */
    private void setUpPlayers() {
        Player host = new Player();
        host.setPlayerName("host");
        host.setAssociatedGamePin("123456");
        host.setPlayerId(1L);
        host.setHost(true);
        Mockito.when(playerRepository.findByPlayerId(1L)).thenReturn(host);


        Player player2 = new Player();
        player2.setPlayerName("player2");
        player2.setAssociatedGamePin("123456");
        player2.setPlayerId(2L);
        player2.setHost(false);
        Mockito.when(playerRepository.findByPlayerId(2L)).thenReturn(player2);


        Player player3 = new Player();
        player3.setPlayerName("player3");
        player3.setAssociatedGamePin("123456");
        player3.setPlayerId(3L);
        player3.setHost(false);
        Mockito.when(playerRepository.findByPlayerId(3L)).thenReturn(player3);


        Player player4 = new Player();
        player4.setPlayerName("player4");
        player4.setAssociatedGamePin("123456");
        player4.setPlayerId(4L);
        player4.setHost(false);
        Mockito.when(playerRepository.findByPlayerId(4L)).thenReturn(player4);

        testPlayers.add(host);
        testPlayers.add(player2);
        testPlayers.add(player3);
        testPlayers.add(player4);
    }

    private void setUpPromptAndPotentialQuestion() {
        drawTestPrompt = new Prompt();
        drawTestPrompt.setPromptNr(997);
        drawTestPrompt.setPromptText("Draw something.");
        drawTestPrompt.setPromptType(PromptType.DRAWING);
        Mockito.when(promptRepository.findByPromptNr(997)).thenReturn(drawTestPrompt);

        potentialDrawingQuestionPROMPTANSWER = new PotentialQuestion();
        potentialDrawingQuestionPROMPTANSWER.setQuestionText("Which rat was drawn by %s?");
        potentialDrawingQuestionPROMPTANSWER.setQuestionType(QuestionType.PROMPTANSWER);
        potentialDrawingQuestionPROMPTANSWER.setAssociatedPrompt(promptRepository.findByPromptNr(997));
        potentialDrawingQuestionPROMPTANSWER.setRequiresTextInput(true);
        potentialDrawingQuestionPROMPTANSWER.setDisplayType(AdditionalDisplayType.NONE);

        potentialDrawingQuestionPLAYER = new PotentialQuestion();
        potentialDrawingQuestionPLAYER.setQuestionText("Which player drew this rat?");
        potentialDrawingQuestionPLAYER.setQuestionType(QuestionType.PLAYER);
        potentialDrawingQuestionPLAYER.setAssociatedPrompt(promptRepository.findByPromptNr(997));
        potentialDrawingQuestionPLAYER.setRequiresTextInput(false);
        potentialDrawingQuestionPLAYER.setDisplayType(AdditionalDisplayType.IMAGE);
    }

    private void setUpDrawingPromptAnswers() {
        for (Player player : testPlayers) {
            DrawingPromptAnswer drawAnswer = new DrawingPromptAnswer();
            drawAnswer.setAnswerDrawing("some drawing from: " + player.getPlayerName());
            drawAnswer.setAssociatedPromptNr(drawTestPrompt.getPromptNr());
            drawAnswer.setAssociatedGamePin(testGame.getGamePin());
            drawAnswer.setAssociatedPlayerId(player.getPlayerId());
            testDrawingAnswers.add(drawAnswer);
        }
    }

}