package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.TextPromptAnswer;
import org.springframework.data.annotation.Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository("textPromptAnswerRepository")
public interface TextPromptAnswerRepository extends JpaRepository<TextPromptAnswer, Long> {
    TextPromptAnswer findByTextPromptAnswerId(long id);
    void deleteByTextPromptAnswerId(long id);

    //list of all TextPromptAnswers findByAssociatedPromptNr, does this even work... or do we need to adjust the repo?

}