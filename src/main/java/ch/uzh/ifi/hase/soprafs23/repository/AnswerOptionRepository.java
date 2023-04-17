package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.AnswerOption;
import ch.uzh.ifi.hase.soprafs23.entity.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("answerOptionRepository")
public interface AnswerOptionRepository extends JpaRepository<AnswerOption, Long> {

}
