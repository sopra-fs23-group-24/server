package ch.uzh.ifi.hase.soprafs23.service.prompt;

import ch.uzh.ifi.hase.soprafs23.constant.AdditionalDisplayType;
import ch.uzh.ifi.hase.soprafs23.constant.PromptType;
import ch.uzh.ifi.hase.soprafs23.constant.QuestionType;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.PotentialQuestion;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.Prompt;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.prompt.PotentialQuestionRepository;
import ch.uzh.ifi.hase.soprafs23.repository.prompt.PromptRepository;
import ch.uzh.ifi.hase.soprafs23.service.prompt.PromptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PromptServiceTest {
    private Prompt tfTestPrompt;
    private Prompt textTestPrompt;
    private Prompt drawTestPrompt;

    private Game testGame;

    @Mock
    private PromptRepository promptRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private PotentialQuestionRepository pqRepository;

    @InjectMocks
    private PromptService promptService;


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

        PotentialQuestion testPQ = new PotentialQuestion();
        testPQ.setQuestionType(QuestionType.PLAYER);
        testPQ.setQuestionText("Test question about a story of %s?");
        testPQ.setRequiresTextInput(true);
        testPQ.setDisplayType(AdditionalDisplayType.TEXT);
        testPQ.setAssociatedPrompt(tfTestPrompt);

        Mockito.when(promptRepository.findAll()).thenReturn(List.of(tfTestPrompt, textTestPrompt, drawTestPrompt));
        Mockito.when(promptRepository.findAllByPromptType(PromptType.TRUEFALSE)).thenReturn(new ArrayList<>(List.of(tfTestPrompt)));
        Mockito.when(promptRepository.findAllByPromptType(PromptType.TEXT)).thenReturn(new ArrayList<>(List.of(textTestPrompt)));
        Mockito.when(promptRepository.findAllByPromptType(PromptType.DRAWING)).thenReturn(new ArrayList<>(List.of(drawTestPrompt)));

        testGame = new Game();
        testGame.setGameId(1L);
        testGame.setGamePin("123456");
    }

    @Test
    public void getPrompts_success() {
        List<Prompt> allTestPrompts = List.of(tfTestPrompt, textTestPrompt, drawTestPrompt);

        List<Prompt> allFound = promptService.getPrompts();

        assertEquals(allFound, allTestPrompts);
    }

    /*@Test
    public void getPromptsOfGame_success() {
        Prompt testPrompt = new Prompt();
        testPrompt.setPromptNr(999);
        testPrompt.setPromptText("Tell a story");
        testPrompt.setPromptType(PromptType.TRUEFALSE);

        List<Prompt> listOfPrompts = List.of(testPrompt);
        testGame.setPromptSet(listOfPrompts);

        Mockito.when(gameRepository.findByGamePin(testGame.getGamePin())).thenReturn(testGame);

        List<Prompt> foundPrompts = promptService.getPromptsOfGame(testGame.getGamePin());

        assertEquals(foundPrompts, listOfPrompts);
    }

    @Test
    public void getPromptsOfGame_invalidPin() {
        Prompt testPrompt = new Prompt();
        testPrompt.setPromptNr(999);
        testPrompt.setPromptText("Tell a story");
        testPrompt.setPromptType(PromptType.TRUEFALSE);

        List<Prompt> listOfPrompts = List.of(testPrompt);
        testGame.setPromptSet(listOfPrompts);

        Mockito.when(gameRepository.findByGamePin(testGame.getGamePin())).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        assertThrows(ResponseStatusException.class, () -> promptService.getPromptsOfGame(testGame.getGamePin()));
    }*/

    /**
     * Setup functions tests
     */
    //all private


    /**
     * Helper functions tests
     */
    //all private
}