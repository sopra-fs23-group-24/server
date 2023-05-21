package ch.uzh.ifi.hase.soprafs23.repository.quiz;

import ch.uzh.ifi.hase.soprafs23.constant.CompletionStatus;
import ch.uzh.ifi.hase.soprafs23.constant.DisplayType;
import ch.uzh.ifi.hase.soprafs23.constant.PromptType;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.Prompt;
import ch.uzh.ifi.hase.soprafs23.entity.quiz.AnswerOption;
import ch.uzh.ifi.hase.soprafs23.entity.quiz.QuizQuestion;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

@DataJpaTest
public class QuizQuestionRepositoryIntegrationTest {
    QuizQuestion testQuizQuestion;

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setup() {
        AnswerOption testAnswerOption = new AnswerOption();
        testAnswerOption.setAnswerOptionText("answer option test");

        testAnswerOption = entityManager.merge(testAnswerOption);
        entityManager.flush();

        Prompt testPrompt = new Prompt();
        testPrompt.setPromptNr(999);
        testPrompt.setPromptText("Tell a story");
        testPrompt.setPromptType(PromptType.TRUEFALSE);

        testPrompt = entityManager.merge(testPrompt);
        entityManager.flush();

        testQuizQuestion = new QuizQuestion();
        testQuizQuestion.setQuestionId(999L);
        testQuizQuestion.setQuizQuestionText("test text");
        testQuizQuestion.setImageToDisplay("a story");
        testQuizQuestion.setStoryToDisplay("an image");
        testQuizQuestion.setAssociatedGamePin("123456");
        testQuizQuestion.setAssociatedPrompt(testPrompt);
        testQuizQuestion.setAnswerOptions(List.of(testAnswerOption));
        testQuizQuestion.setCorrectAnswer(testAnswerOption);
        testQuizQuestion.setAnswerDisplayType(DisplayType.IMAGE);

        testQuizQuestion = entityManager.merge(testQuizQuestion);
        entityManager.flush();
    }

    @AfterEach
    void emptyRepository() {
        quizQuestionRepository.deleteAll();
    }

    @Test
    public void findAllByAssociatedGamePin_success() {
        List<QuizQuestion> foundQuestions = quizQuestionRepository.findAllByAssociatedGamePin(testQuizQuestion.getAssociatedGamePin());

        Assertions.assertEquals(foundQuestions.size(), 1);
        Assertions.assertEquals(foundQuestions.get(0).getQuestionId(), testQuizQuestion.getQuestionId());
        Assertions.assertEquals(foundQuestions.get(0).getQuestionStatus(), CompletionStatus.NOT_FINISHED);
        Assertions.assertEquals(foundQuestions.get(0).getQuizQuestionText(), testQuizQuestion.getQuizQuestionText());
        Assertions.assertEquals(foundQuestions.get(0).getImageToDisplay(), testQuizQuestion.getImageToDisplay());
        Assertions.assertEquals(foundQuestions.get(0).getStoryToDisplay(), testQuizQuestion.getStoryToDisplay());
        Assertions.assertEquals(foundQuestions.get(0).getAssociatedGamePin(), testQuizQuestion.getAssociatedGamePin());
        Assertions.assertEquals(foundQuestions.get(0).getAssociatedPrompt(), testQuizQuestion.getAssociatedPrompt());
        Assertions.assertEquals(foundQuestions.get(0).getAnswerOptions(), testQuizQuestion.getAnswerOptions());
        Assertions.assertEquals(foundQuestions.get(0).getAnswerDisplayType(), testQuizQuestion.getAnswerDisplayType());
        Assertions.assertEquals(foundQuestions.get(0).getCorrectAnswer(), testQuizQuestion.getCorrectAnswer());
        Assertions.assertTrue(foundQuestions.get(0).getReceivedAnswers().isEmpty());
    }

    @Test
    public void deleteAllByAssociatedGamePin_success() {
        List<QuizQuestion> foundQuestions = quizQuestionRepository.findAllByAssociatedGamePin(testQuizQuestion.getAssociatedGamePin());
        Assertions.assertEquals(foundQuestions.size(), 1);

        quizQuestionRepository.deleteAllByAssociatedGamePin(testQuizQuestion.getAssociatedGamePin());

        foundQuestions = quizQuestionRepository.findAllByAssociatedGamePin(testQuizQuestion.getAssociatedGamePin());
        Assertions.assertEquals(foundQuestions.size(), 0);
    }


}