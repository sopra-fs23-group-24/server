package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.constant.PromptType;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.Prompt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("promptRepository")
public interface PromptRepository extends JpaRepository<Prompt, Long> {

  Prompt findByPromptId(Long id);
  Prompt findByPromptType(PromptType type);

}
