package ch.uzh.ifi.hase.soprafs23.repository.quiz;

import ch.uzh.ifi.hase.soprafs23.constant.CompletionStatus;
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
        entityManager.persist(testAnswerOption);
        entityManager.merge(testAnswerOption);
        entityManager.flush();

        Prompt testPrompt = new Prompt();
        testPrompt.setPromptNr(999);
        testPrompt.setPromptText("Tell a story");
        testPrompt.setPromptType(PromptType.TRUEFALSE);

        entityManager.persist(testPrompt);
        entityManager.flush();

        testQuizQuestion = new QuizQuestion();
        testQuizQuestion.setAssociatedGamePin("123456");
        testQuizQuestion.setQuizQuestionText("test text");
        testQuizQuestion.setQuestionStatus(CompletionStatus.NOT_FINISHED);
        testQuizQuestion.setAssociatedPrompt(testPrompt);
        testQuizQuestion.setAnswerOptions(List.of(testAnswerOption));

        entityManager.merge(testQuizQuestion);
        entityManager.persist(testAnswerOption);
        entityManager.flush();
    }

    @AfterEach
    void emptyRepository() {
        quizQuestionRepository.deleteAll();
    }

    @Test
    public void findAllByAssociatedGamePin_success(){
        List<QuizQuestion> foundQuestions = quizQuestionRepository.findAllByAssociatedGamePin(testQuizQuestion.getAssociatedGamePin());

        Assertions.assertEquals(foundQuestions.size(), 1);
        Assertions.assertEquals(foundQuestions.get(0).getQuizQuestionText(), testQuizQuestion.getQuizQuestionText());
    }

    @Test
    public void deleteAllByAssociatedGamePin_success(){
        List<QuizQuestion> foundQuestions = quizQuestionRepository.findAllByAssociatedGamePin(testQuizQuestion.getAssociatedGamePin());

        Assertions.assertEquals(foundQuestions.size(), 1);

        quizQuestionRepository.deleteAllByAssociatedGamePin(testQuizQuestion.getAssociatedGamePin());

        foundQuestions = quizQuestionRepository.findAllByAssociatedGamePin(testQuizQuestion.getAssociatedGamePin());

        Assertions.assertEquals(foundQuestions.size(), 0);
    }



}