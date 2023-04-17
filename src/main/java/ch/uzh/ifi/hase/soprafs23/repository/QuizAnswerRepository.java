package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.AnswerOption;
import ch.uzh.ifi.hase.soprafs23.entity.QuizAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("quizAnswerRepository")
public interface QuizAnswerRepository extends JpaRepository<QuizAnswer, Long> {

}
