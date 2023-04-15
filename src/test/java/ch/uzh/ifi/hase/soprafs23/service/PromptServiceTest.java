package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.AdditionalDisplayType;
import ch.uzh.ifi.hase.soprafs23.constant.PromptType;
import ch.uzh.ifi.hase.soprafs23.constant.QuestionType;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.PotentialQuestion;
import ch.uzh.ifi.hase.soprafs23.entity.Prompt;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.PotentialQuestionsRepository;
import ch.uzh.ifi.hase.soprafs23.repository.PromptRepository;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PromptPostDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class PromptServiceTest {
    private Prompt tfTestPrompt;
    private Prompt textTestPrompt;
    private Prompt drawTestPrompt;

    private PotentialQuestion testPQ;

    @Mock
    private PromptRepository promptRepository;

    @InjectMocks
    private PromptService promptService;

    @Mock
    private PotentialQuestionsRepository pqRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        tfTestPrompt = new Prompt();
        tfTestPrompt.setPromptNr(999);
        tfTestPrompt.setPromptText("Tell a story");
        tfTestPrompt.setPromptType(PromptType.TRUEFALSE);

        textTestPrompt = new Prompt();
        textTestPrompt.setPromptNr(998);
        textTestPrompt.setPromptText("Answer a question");
        textTestPrompt.setPromptType(PromptType.TEXT);

        drawTestPrompt = new Prompt();
        drawTestPrompt.setPromptNr(997);
        drawTestPrompt.setPromptText("Draw something.");
        drawTestPrompt.setPromptType(PromptType.DRAWING);

        testPQ = new PotentialQuestion();
        testPQ.setQuestionType(QuestionType.PLAYER);
        testPQ.setQuestionText("Test question about a story of %s?");
        testPQ.setRequiresTextInput(true);
        testPQ.setDisplayType(AdditionalDisplayType.TEXT);
        testPQ.setAssociatedPrompt(tfTestPrompt);

        Mockito.when(promptRepository.findAll()).thenReturn(List.of(tfTestPrompt, textTestPrompt, drawTestPrompt));
        Mockito.when(promptRepository.findAllByPromptType(PromptType.TRUEFALSE)).thenReturn(new ArrayList<Prompt>(List.of(tfTestPrompt)));
        Mockito.when(promptRepository.findAllByPromptType(PromptType.TEXT)).thenReturn(new ArrayList<Prompt>(List.of(textTestPrompt)));
        Mockito.when(promptRepository.findAllByPromptType(PromptType.DRAWING)).thenReturn(new ArrayList<Prompt>(List.of(drawTestPrompt)));
    }

    @Test
    public void getPrompts_success() throws Exception{
        List<Prompt> allTestPrompts = List.of(tfTestPrompt, textTestPrompt, drawTestPrompt);

        List<Prompt> allFound = promptService.getPrompts();

        assertEquals(allFound, allTestPrompts);
    }


    /**
     * Setup functions tests
     */
    //all private


    /**
     * Helper functions tests
     */
    //all private
}