package ch.uzh.ifi.hase.soprafs23.service.quiz;

import ch.uzh.ifi.hase.soprafs23.constant.CompletionStatus;
import ch.uzh.ifi.hase.soprafs23.constant.PromptType;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.Prompt;
import ch.uzh.ifi.hase.soprafs23.entity.quiz.AnswerOption;
import ch.uzh.ifi.hase.soprafs23.entity.quiz.QuizQuestion;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.quiz.QuizQuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class QuizQuestionServiceTest {
    QuizQuestion testQuestion;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private QuizQuestionRepository qqRepository;

    @InjectMocks
    private QuizQuestionService quizQuestionService;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        Prompt testPrompt = new Prompt();
        testPrompt.setPromptNr(999);
        testPrompt.setPromptText("Tell a story");
        testPrompt.setPromptType(PromptType.TRUEFALSE);

        AnswerOption answerOption1 = new AnswerOption();
        answerOption1.setAnswerOptionText("option 1");
        AnswerOption answerOption2 = new AnswerOption();
        answerOption2.setAnswerOptionText("option 2");
        AnswerOption answerOption3 = new AnswerOption();
        answerOption3.setAnswerOptionText("option 3");
        AnswerOption answerOption4 = new AnswerOption();
        answerOption4.setAnswerOptionText("option 4");

        List<AnswerOption> answerOptionList = List.of(answerOption1, answerOption2, answerOption3, answerOption4);

        testQuestion = new QuizQuestion();
        testQuestion.setAnswerOptions(answerOptionList);
        testQuestion.setCorrectAnswer(answerOption1);
        testQuestion.setQuizQuestionText("test text");
        testQuestion.setQuestionStatus(CompletionStatus.NOT_FINISHED);
        testQuestion.setAssociatedGamePin("123456");
        testQuestion.setAssociatedPrompt(testPrompt);

        Mockito.when(gameRepository.findByGamePin("123456")).thenReturn(new Game());
        Mockito.when(gameRepository.findByGamePin("invalidPin")).thenReturn(null);
        Mockito.when(qqRepository.findAll()).thenReturn(List.of(testQuestion));
        Mockito.when(qqRepository.findAllByAssociatedGamePin(Mockito.any())).thenReturn(List.of(testQuestion));
    }

    @Test
    public void getQuizQuestions_success() {
        List<QuizQuestion> foundQQ = quizQuestionService.getQuizQuestions();
        assertEquals(foundQQ, List.of(testQuestion));

    }

    @Test
    public void getQuizQuestionsOfGame_success() {
        List<QuizQuestion> foundQQ = quizQuestionService.getQuizQuestionsOfGame("123456");
        assertEquals(foundQQ, List.of(testQuestion));
    }

    @Test
    public void getQuizQuestions_invalidGamePin() {
        assertThrows(ResponseStatusException.class, () -> quizQuestionService.getQuizQuestionsOfGame("invalidPin"));
    }


}