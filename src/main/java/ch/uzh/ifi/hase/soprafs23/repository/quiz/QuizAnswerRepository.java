package ch.uzh.ifi.hase.soprafs23.repository.quiz;

import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.quiz.QuizAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("quizAnswerRepository")
public interface QuizAnswerRepository extends JpaRepository<QuizAnswer, Long> {

    QuizAnswer findByQuizAnswerId(Long id);

    QuizAnswer findByAssociatedPlayer(Player player);
}
