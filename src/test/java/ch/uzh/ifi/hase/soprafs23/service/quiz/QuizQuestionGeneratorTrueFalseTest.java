package ch.uzh.ifi.hase.soprafs23.service.quiz;

import ch.uzh.ifi.hase.soprafs23.constant.CompletionStatus;
import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.constant.PromptType;
import ch.uzh.ifi.hase.soprafs23.constant.QuestionType;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.PotentialQuestion;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.Prompt;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.TrueFalsePromptAnswer;
import ch.uzh.ifi.hase.soprafs23.entity.quiz.AnswerOption;
import ch.uzh.ifi.hase.soprafs23.entity.quiz.QuizQuestion;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.prompt.PromptRepository;
import ch.uzh.ifi.hase.soprafs23.repository.prompt.TrueFalsePromptAnswerRepository;
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

public class QuizQuestionGeneratorTrueFalseTest {
    private final List<Player> testPlayers = new ArrayList<>();
    private Prompt tfPrompt;
    private PotentialQuestion potentialTFQuestionBOOLEAN;
    private PotentialQuestion potentialTFQuestionPLAYER;

    // is needed to run
    private QuizQuestion testQuizQuestion;
    private List<TrueFalsePromptAnswer> testTrueFalseAnswers = new ArrayList<>();
    private Game testGame;
    @Mock
    private QuizQuestionRepository qqRepository;
    @Mock
    private PlayerRepository playerRepository;
    @Mock
    private PromptRepository promptRepository;
    @Mock
    private TrueFalsePromptAnswerRepository trueFalsePromptAnswerRepository;

    // is needed to run
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
        testGame.setPromptSet(List.of(tfPrompt));

        setUpTFPromptAnswers();

        Mockito.when(qqRepository.save(Mockito.any())).thenReturn(testQuizQuestion);

    }

    @Test
    public void transformPotentialQuestionTF_PLAYER() {
        Mockito.when(trueFalsePromptAnswerRepository.findTrueFalsePromptAnswerByAssociatedPlayerIdAndAssociatedPromptNr(Mockito.anyLong(), Mockito.anyInt())).thenReturn(testTrueFalseAnswers.get(0));
        QuizQuestion generatedQuestion = quizQuestionGenerator.transformPotentialTFQuestionTypePlayer(potentialTFQuestionPLAYER,
                new ArrayList<>(testTrueFalseAnswers), testPlayers.get(0));

        Assertions.assertEquals(generatedQuestion.getQuestionStatus(), CompletionStatus.NOT_FINISHED);

        Assertions.assertEquals(generatedQuestion.getAnswerOptions().size(), 4);
        for (AnswerOption ao : generatedQuestion.getAnswerOptions()) {
            assert (ao.getAnswerOptionText().equals(testPlayers.get(0).getPlayerName())
                    || ao.getAnswerOptionText().equals(testPlayers.get(1).getPlayerName())
                    || ao.getAnswerOptionText().equals(testPlayers.get(2).getPlayerName())
                    || ao.getAnswerOptionText().equals(testPlayers.get(3).getPlayerName())
            );
        }

        Assertions.assertEquals(generatedQuestion.getCorrectAnswer().getAnswerOptionText(), testPlayers.get(0).getPlayerName());
        Assertions.assertTrue(generatedQuestion.getAnswerOptions().contains(generatedQuestion.getCorrectAnswer()));

        Assertions.assertTrue(generatedQuestion.getReceivedAnswers().isEmpty());

        Assertions.assertNotNull(generatedQuestion.getQuizQuestionText());

        Assertions.assertNull(generatedQuestion.getImageToDisplay());
        Assertions.assertNotNull(generatedQuestion.getStoryToDisplay());
    }

    @Test
    public void transformPotentialQuestionTF_PLAYER_notATrueStory() {
        setUpTFPromptAnswers_allFalse();
        QuizQuestion generatedQuestion = quizQuestionGenerator.transformPotentialTFQuestionTypePlayer(potentialTFQuestionPLAYER,
                new ArrayList<>(testTrueFalseAnswers), testPlayers.get(0));
        Assertions.assertNull(generatedQuestion);
    }

    @Test
    public void transformPotentialQuestionTF_BOOLEAN() {
        Mockito.when(trueFalsePromptAnswerRepository.findTrueFalsePromptAnswerByAssociatedPlayerIdAndAssociatedPromptNr(Mockito.anyLong(), Mockito.anyInt())).thenReturn(testTrueFalseAnswers.get(0));
        QuizQuestion generatedQuestion = quizQuestionGenerator.transformPotentialTFQuestionTypeBoolean(potentialTFQuestionBOOLEAN,
                new ArrayList<>(testTrueFalseAnswers), testPlayers.get(0));

        Assertions.assertEquals(generatedQuestion.getQuestionStatus(), CompletionStatus.NOT_FINISHED);

        Assertions.assertEquals(generatedQuestion.getAnswerOptions().size(), 2);
        for (AnswerOption ao : generatedQuestion.getAnswerOptions()) {
            assert (ao.getAnswerOptionText().equals("true")
                    || ao.getAnswerOptionText().equals("false")
            );
        }

        Assertions.assertEquals(generatedQuestion.getCorrectAnswer().getAnswerOptionText(), "true");
        Assertions.assertTrue(generatedQuestion.getAnswerOptions().contains(generatedQuestion.getCorrectAnswer()));

        Assertions.assertTrue(generatedQuestion.getReceivedAnswers().isEmpty());

        Assertions.assertNotNull(generatedQuestion.getQuizQuestionText());

        Assertions.assertNull(generatedQuestion.getImageToDisplay());
        Assertions.assertEquals(generatedQuestion.getStoryToDisplay(), "some story from: " + testPlayers.get(0).getPlayerName());
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
        tfPrompt = new Prompt();
        tfPrompt.setPromptNr(999);
        tfPrompt.setPromptText("Tell a story.");
        tfPrompt.setPromptType(PromptType.TRUEFALSE);
        Mockito.when(promptRepository.findByPromptNr(999)).thenReturn(tfPrompt);

        potentialTFQuestionBOOLEAN = new PotentialQuestion();
        potentialTFQuestionBOOLEAN.setQuestionText("Is the following story by %s about their last holiday true or false?");
        potentialTFQuestionBOOLEAN.setQuestionType(QuestionType.BOOLEAN);
        potentialTFQuestionBOOLEAN.setAssociatedPrompt(promptRepository.findByPromptNr(999));
        potentialTFQuestionBOOLEAN.setRequiresTextInput(true);

        potentialTFQuestionPLAYER = new PotentialQuestion();
        potentialTFQuestionPLAYER.setQuestionText("Which player told this true story about their last holiday?");
        potentialTFQuestionPLAYER.setQuestionType(QuestionType.PLAYER);
        potentialTFQuestionPLAYER.setAssociatedPrompt(promptRepository.findByPromptNr(999));
        potentialTFQuestionPLAYER.setRequiresTextInput(false);
    }

    private void setUpTFPromptAnswers() {
        for (Player player : testPlayers) {
            TrueFalsePromptAnswer tfAnswer = new TrueFalsePromptAnswer();
            tfAnswer.setAnswerText("some story from: " + player.getPlayerName());
            tfAnswer.setAnswerBoolean(true);
            tfAnswer.setAssociatedPromptNr(tfPrompt.getPromptNr());
            tfAnswer.setAssociatedGamePin(testGame.getGamePin());
            tfAnswer.setAssociatedPlayerId(player.getPlayerId());
            testTrueFalseAnswers.add(tfAnswer);
        }
    }

    private void setUpTFPromptAnswers_allFalse() {
        testTrueFalseAnswers = new ArrayList<>();
        for (Player player : testPlayers) {
            TrueFalsePromptAnswer tfAnswer = new TrueFalsePromptAnswer();
            tfAnswer.setAnswerText("some story from: " + player.getPlayerName());
            tfAnswer.setAnswerBoolean(false);
            tfAnswer.setAssociatedPromptNr(tfPrompt.getPromptNr());
            tfAnswer.setAssociatedGamePin(testGame.getGamePin());
            tfAnswer.setAssociatedPlayerId(player.getPlayerId());
            testTrueFalseAnswers.add(tfAnswer);
        }
        Mockito.when(trueFalsePromptAnswerRepository.findTrueFalsePromptAnswerByAssociatedPlayerIdAndAssociatedPromptNr(Mockito.anyLong(), Mockito.anyInt())).thenReturn(testTrueFalseAnswers.get(0));
    }


}