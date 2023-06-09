package ch.uzh.ifi.hase.soprafs23.service.prompt;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.constant.PromptType;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.Prompt;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.prompt.PromptRepository;
import ch.uzh.ifi.hase.soprafs23.rest.dto.prompt.PromptPostDTO;
import ch.uzh.ifi.hase.soprafs23.service.PlayerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@WebAppConfiguration
@SpringBootTest
class PromptServiceIntegrationTest {
    Game testGame;
    @Qualifier("gameRepository")
    @Autowired
    private GameRepository gameRepository;

    @Qualifier("promptRepository")
    @Autowired
    private PromptRepository promptRepository;

    @Autowired
    private PromptService promptService;

    @BeforeEach
    void setup() {
        gameRepository.deleteAll();

        testGame = new Game();
        testGame.setStatus(GameStatus.SELECTION);
        testGame.setGamePin("123456");
        gameRepository.save(testGame);
        gameRepository.flush();
    }

    @Test
    void getPrompts() {
        Assertions.assertTrue(promptRepository.findAll().size() > 0);
    }

    @Test
    void pickPrompts_success() {
        Game foundGame = gameRepository.findByGamePin(testGame.getGamePin());
        Assertions.assertNotNull(foundGame);
        Assertions.assertTrue(foundGame.getPromptSet().isEmpty());

        PromptPostDTO testDTO = new PromptPostDTO();
        testDTO.setDrawingNr(1);
        testDTO.setTextNr(1);
        testDTO.setTrueFalseNr(1);
        testDTO.setTimer(40);

        List<Prompt> pickedPrompts = promptService.pickPrompts(testDTO, testGame.getGamePin());
        Assertions.assertTrue(pickedPrompts.size() > 0);

        foundGame = gameRepository.findByGamePin(testGame.getGamePin());
        Assertions.assertEquals(GameStatus.PROMPT, foundGame.getStatus());
        Assertions.assertEquals(3, foundGame.getPromptSet().size());
    }

    @Test
    void getPromptsOfGame_success() {
        Prompt tfTestPrompt = new Prompt();
        tfTestPrompt.setPromptNr(999);
        tfTestPrompt.setPromptText("Tell a story");
        tfTestPrompt.setPromptType(PromptType.TRUEFALSE);
        promptRepository.save(tfTestPrompt);

        Prompt textTestPrompt = new Prompt();
        textTestPrompt.setPromptNr(998);
        textTestPrompt.setPromptText("Answer a question");
        textTestPrompt.setPromptType(PromptType.TEXT);
        promptRepository.save(textTestPrompt);

        Prompt drawTestPrompt = new Prompt();
        drawTestPrompt.setPromptNr(997);
        drawTestPrompt.setPromptText("Draw something");
        drawTestPrompt.setPromptType(PromptType.DRAWING);
        promptRepository.save(drawTestPrompt);

        promptRepository.flush();

        List<Prompt> testPrompts = new ArrayList<>();
        testPrompts.add(tfTestPrompt);
        testPrompts.add(textTestPrompt);
        testPrompts.add(drawTestPrompt);

        testGame.setPromptSet(testPrompts);
        gameRepository.save(testGame);
        gameRepository.flush();

        List<Prompt> foundPrompts = promptService.getPromptsOfGame(testGame.getGamePin());
        Assertions.assertEquals(3, foundPrompts.size());

        for (Prompt p : foundPrompts) {
            assert (Objects.equals(p.getPromptText(), tfTestPrompt.getPromptText()) || Objects.equals(p.getPromptText(), drawTestPrompt.getPromptText()) || Objects.equals(p.getPromptText(), textTestPrompt.getPromptText()));
        }
    }

    @Test
    void addTimerToGame_success(){
        Game foundGame = gameRepository.findByGamePin(testGame.getGamePin());
        Assertions.assertNotNull(foundGame);
        Assertions.assertNull(foundGame.getTimer());

        PromptPostDTO testDTO = new PromptPostDTO();
        testDTO.setDrawingNr(1);
        testDTO.setTextNr(1);
        testDTO.setTrueFalseNr(1);
        testDTO.setTimer(40);

        promptService.addTimerToGame(testDTO, testGame.getGamePin());

        foundGame = gameRepository.findByGamePin(testGame.getGamePin());
        Assertions.assertEquals(40, foundGame.getTimer());
    }

}