package ch.uzh.ifi.hase.soprafs23.service.prompt;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.constant.PromptType;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.Prompt;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.prompt.PotentialQuestionRepository;
import ch.uzh.ifi.hase.soprafs23.repository.prompt.PromptRepository;
import ch.uzh.ifi.hase.soprafs23.rest.dto.prompt.PromptPostDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PromptServiceTest {
    private Prompt tfTestPrompt;
    private Prompt textTestPrompt;
    private Prompt drawTestPrompt;

    private Game testGame;

    @Mock
    private PromptRepository promptRepository;

    @Mock
    private GameRepository gameRepository;

    //no usage but needs to be instantiated for promptService to run
    @Mock
    private PotentialQuestionRepository potentialQuestionRepository;

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

    @Test
    public void getPromptsOfGame_success() {
        List<Prompt> listOfPrompts = List.of(tfTestPrompt, textTestPrompt, drawTestPrompt);
        testGame.setPromptSet(listOfPrompts);

        Mockito.when(gameRepository.findByGamePin(testGame.getGamePin())).thenReturn(testGame);

        List<Prompt> foundPrompts = promptService.getPromptsOfGame(testGame.getGamePin());

        assertEquals(foundPrompts, listOfPrompts);
    }

    @Test
    public void getPromptsOfGame_invalidPin() {
        Mockito.when(gameRepository.findByGamePin(testGame.getGamePin())).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> promptService.getPromptsOfGame(testGame.getGamePin()));
    }

    @Test
    public void pickPrompts_addPromptsToGame_success() {
        PromptPostDTO testDTO = new PromptPostDTO();
        testDTO.setDrawingNr(1);
        testDTO.setTextNr(1);
        testDTO.setTrueFalseNr(1);
        testDTO.setTimer(40);

        testGame.setStatus(GameStatus.SELECTION);
        testGame.emptyPromptSet();
        Mockito.when(gameRepository.findByGamePin(Mockito.anyString())).thenReturn(testGame);

        assertTrue(gameRepository.findByGamePin(testGame.getGamePin()).getPromptSet().isEmpty());
        assertEquals(gameRepository.findByGamePin(testGame.getGamePin()).getStatus(), GameStatus.SELECTION);

        List<Prompt> listOfPrompts = List.of(textTestPrompt, tfTestPrompt, drawTestPrompt);

        List<Prompt> createdPrompts = promptService.pickPrompts(testDTO, "123456");

        Mockito.verify(gameRepository, Mockito.times(1)).save(Mockito.any());

        assertEquals(createdPrompts, listOfPrompts);
        assertEquals(gameRepository.findByGamePin(testGame.getGamePin()).getPromptSet(), listOfPrompts);
        assertEquals(gameRepository.findByGamePin(testGame.getGamePin()).getStatus(), GameStatus.PROMPT);
    }

    @Test
    public void pickPrompts_addPromptsToGame_invalidGamePin() {
        PromptPostDTO testDTO = new PromptPostDTO();
        testDTO.setDrawingNr(1);
        testDTO.setTextNr(1);
        testDTO.setTrueFalseNr(1);
        testDTO.setTimer(40);

        Mockito.when(gameRepository.findByGamePin(testGame.getGamePin())).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> promptService.pickPrompts(testDTO, testGame.getGamePin()));
    }

    @Test
    public void pickPrompts_addPromptsToGame_gameNotInSelectionStage() {
        PromptPostDTO testDTO = new PromptPostDTO();
        testDTO.setDrawingNr(1);
        testDTO.setTextNr(1);
        testDTO.setTrueFalseNr(1);
        testDTO.setTimer(40);

        testGame.setStatus(GameStatus.PROMPT);
        Mockito.when(gameRepository.findByGamePin(testGame.getGamePin())).thenReturn(testGame);

        assertThrows(ResponseStatusException.class, () -> promptService.pickPrompts(testDTO, testGame.getGamePin()));
    }

    @Test
    public void pickPrompts_addPromptsToGame_alreadyHasPrompts() {
        PromptPostDTO testDTO = new PromptPostDTO();
        testDTO.setDrawingNr(1);
        testDTO.setTextNr(1);
        testDTO.setTrueFalseNr(1);
        testDTO.setTimer(40);

        testGame.setStatus(GameStatus.SELECTION);
        testGame.setPromptSet(List.of(textTestPrompt, tfTestPrompt, drawTestPrompt));
        Mockito.when(gameRepository.findByGamePin(testGame.getGamePin())).thenReturn(testGame);

        assertThrows(ResponseStatusException.class, () -> promptService.pickPrompts(testDTO, testGame.getGamePin()));
    }

    @Test
    public void addTimerToGame() {
        PromptPostDTO testDTO = new PromptPostDTO();
        testDTO.setDrawingNr(1);
        testDTO.setTextNr(1);
        testDTO.setTrueFalseNr(1);
        testDTO.setTimer(40);

        testGame.setStatus(GameStatus.PROMPT);
        Mockito.when(gameRepository.findByGamePin(Mockito.anyString())).thenReturn(testGame);
        Mockito.when(gameRepository.save(Mockito.any())).thenReturn(testGame);

        promptService.addTimerToGame(testDTO, "123456");

        Mockito.verify(gameRepository, Mockito.times(1)).save(Mockito.any());

        assertEquals(gameRepository.findByGamePin(testGame.getGamePin()).getTimer(), testDTO.getTimer());
    }

}