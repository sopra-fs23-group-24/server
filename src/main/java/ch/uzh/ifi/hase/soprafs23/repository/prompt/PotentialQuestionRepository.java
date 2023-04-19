package ch.uzh.ifi.hase.soprafs23.repository.prompt;

import ch.uzh.ifi.hase.soprafs23.constant.QuestionType;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.PotentialQuestion;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.Prompt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("potentialQuestionRepository")
public interface PotentialQuestionRepository extends JpaRepository<PotentialQuestion, Long> {
    List<PotentialQuestion> findAllByAssociatedPrompt(Prompt prompt);

    List<PotentialQuestion> findAllByQuestionType(QuestionType type);
}
