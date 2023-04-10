package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.constant.PromptType;
import ch.uzh.ifi.hase.soprafs23.constant.QuestionType;
import ch.uzh.ifi.hase.soprafs23.entity.PotentialQuestion;
import ch.uzh.ifi.hase.soprafs23.entity.Prompt;
import org.aspectj.weaver.patterns.TypePatternQuestions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("potentialQuestionRepository")
public interface PotentialQuestionsRepository extends JpaRepository<PotentialQuestion, Long> {
    PotentialQuestion getBypotentialQuestionId(Long id);
    List<PotentialQuestion> findAllByAssociatedPrompt(Prompt prompt);
    List<PotentialQuestion> findAllByQuestionType(QuestionType type);
}
