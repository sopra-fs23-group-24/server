package ch.uzh.ifi.hase.soprafs23.service.quiz;

import ch.uzh.ifi.hase.soprafs23.constant.*;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.PotentialQuestion;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.Prompt;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.TextPromptAnswer;
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

public class QuizQuestionGeneratorTestText {
    private final List<Player> testPlayers = new ArrayList<>();
    private final List<TextPromptAnswer> testTextAnswers = new ArrayList<>();
    private Prompt textTestPrompt;
    private PotentialQuestion potentialTextQuestionPROMPTANSWER;
    private PotentialQuestion potentialTextQuestionPLAYER;
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
        testGame.setPromptSet(List.of(textTestPrompt));

        setUpTextPromptAnswers();

        Mockito.when(qqRepository.save(Mockito.any())).thenReturn(testQuizQuestion);

    }

    @Test
    public void transformPotentialQuestionText_PLAYER() {

        QuizQuestion generatedQuestion = quizQuestionGenerator.transformPotentialQuestionText(potentialTextQuestionPLAYER, new ArrayList<>(testTextAnswers));

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

        Assertions.assertNotNull(generatedQuestion.getCorrectAnswer());
        Assertions.assertTrue(generatedQuestion.getAnswerOptions().contains(generatedQuestion.getCorrectAnswer()));

        Assertions.assertTrue(generatedQuestion.getReceivedAnswers().isEmpty());

        Assertions.assertNull(generatedQuestion.getImageToDisplay());
        Assertions.assertNull(generatedQuestion.getStoryToDisplay());
    }

    @Test
    public void transformPotentialQuestionText_PROMPTANSWER() {

        QuizQuestion generatedQuestion = quizQuestionGenerator.transformPotentialQuestionText(potentialTextQuestionPROMPTANSWER, new ArrayList<>(testTextAnswers));

        Assertions.assertEquals(generatedQuestion.getQuestionStatus(), CompletionStatus.NOT_FINISHED);

        Assertions.assertEquals(generatedQuestion.getAnswerOptions().size(), 4);
        for (AnswerOption ao : generatedQuestion.getAnswerOptions()) {
            assert (ao.getAnswerOptionText().equals(testTextAnswers.get(0).getAnswer())
                    || ao.getAnswerOptionText().equals(testTextAnswers.get(1).getAnswer())
                    || ao.getAnswerOptionText().equals(testTextAnswers.get(2).getAnswer())
                    || ao.getAnswerOptionText().equals(testTextAnswers.get(3).getAnswer())
            );
        }
        Assertions.assertNotNull(generatedQuestion.getQuizQuestionText());

        Assertions.assertNotNull(generatedQuestion.getCorrectAnswer());
        Assertions.assertTrue(generatedQuestion.getAnswerOptions().contains(generatedQuestion.getCorrectAnswer()));

        Assertions.assertTrue(generatedQuestion.getReceivedAnswers().isEmpty());

        Assertions.assertNotNull(generatedQuestion.getQuizQuestionText());
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
        textTestPrompt = new Prompt();
        textTestPrompt.setPromptNr(999);
        textTestPrompt.setPromptText("Answer a question.");
        textTestPrompt.setPromptType(PromptType.TEXT);
        Mockito.when(promptRepository.findByPromptNr(998)).thenReturn(textTestPrompt);

        potentialTextQuestionPROMPTANSWER = new PotentialQuestion();
        potentialTextQuestionPROMPTANSWER.setQuestionText("What is the favourite food of %s?");
        potentialTextQuestionPROMPTANSWER.setQuestionType(QuestionType.PROMPTANSWER);
        potentialTextQuestionPROMPTANSWER.setAssociatedPrompt(promptRepository.findByPromptNr(998));
        potentialTextQuestionPROMPTANSWER.setRequiresTextInput(true);
        potentialTextQuestionPROMPTANSWER.setDisplayType(AdditionalDisplayType.NONE);

        potentialTextQuestionPLAYER = new PotentialQuestion();
        potentialTextQuestionPLAYER.setQuestionText("Whose favourite food is %s?");
        potentialTextQuestionPLAYER.setQuestionType(QuestionType.PLAYER);
        potentialTextQuestionPLAYER.setAssociatedPrompt(promptRepository.findByPromptNr(998));
        potentialTextQuestionPLAYER.setRequiresTextInput(true);
        potentialTextQuestionPLAYER.setDisplayType(AdditionalDisplayType.NONE);
    }

    private void setUpTextPromptAnswers() {
        for (Player player : testPlayers) {
            TextPromptAnswer textAnswer = new TextPromptAnswer();
            textAnswer.setAnswer("some answer from: " + player.getPlayerName());
            textAnswer.setAssociatedPromptNr(textTestPrompt.getPromptNr());
            textAnswer.setAssociatedGamePin(testGame.getGamePin());
            textAnswer.setAssociatedPlayerId(player.getPlayerId());
            testTextAnswers.add(textAnswer);
        }
    }

}