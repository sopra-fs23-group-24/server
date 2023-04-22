package ch.uzh.ifi.hase.soprafs23.repository.quiz;

import ch.uzh.ifi.hase.soprafs23.entity.quiz.AnswerOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("answerOptionRepository")
public interface AnswerOptionRepository extends JpaRepository<AnswerOption, Long> {

    AnswerOption getAnswerOptionByAnswerOptionId(long id);
}
