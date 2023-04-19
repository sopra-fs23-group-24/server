package ch.uzh.ifi.hase.soprafs23.repository.prompt;

import ch.uzh.ifi.hase.soprafs23.constant.PromptType;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.Prompt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("promptRepository")
public interface PromptRepository extends JpaRepository<Prompt, Long> {
    Prompt findByPromptNr(Integer id);

    List<Prompt> findAllByPromptType(PromptType type);

}
