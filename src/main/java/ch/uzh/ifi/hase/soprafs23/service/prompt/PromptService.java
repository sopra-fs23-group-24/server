package ch.uzh.ifi.hase.soprafs23.service.prompt;

import ch.uzh.ifi.hase.soprafs23.constant.DisplayType;
import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.constant.PromptType;
import ch.uzh.ifi.hase.soprafs23.constant.QuestionType;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.PotentialQuestion;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.Prompt;
import ch.uzh.ifi.hase.soprafs23.exceptions.PromptSetupException;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.prompt.PotentialQuestionRepository;
import ch.uzh.ifi.hase.soprafs23.repository.prompt.PromptRepository;
import ch.uzh.ifi.hase.soprafs23.rest.dto.prompt.PromptPostDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class PromptService {

    private final Logger log = LoggerFactory.getLogger(PromptService.class);

    private final PromptRepository promptRepository;

    private final PotentialQuestionRepository potentialQuestionsRepository;

    private final GameRepository gameRepository;

    private final Random rand = SecureRandom.getInstanceStrong();


    @Autowired
    public PromptService(@Qualifier("gameRepository") GameRepository gameRepository,
                         @Qualifier("promptRepository") PromptRepository promptRepository, @Qualifier("potentialQuestionRepository") PotentialQuestionRepository potentialQuestionsRepository) throws PromptSetupException, NoSuchAlgorithmException, IOException {
        this.promptRepository = promptRepository;
        this.potentialQuestionsRepository = potentialQuestionsRepository;
        this.gameRepository = gameRepository;
        initialisePromptRepository();
        initialisePotentialQuestionRepository();
    }

    //TODO: test Integration?
    public List<Prompt> getPrompts() {
        return promptRepository.findAll();
    }


    //TODO: test Integration?
    public List<Prompt> getPromptsOfGame(String gamePin) {
        Game gameByPin = gameRepository.findByGamePin(gamePin);
        if (gameByPin == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No game with this pin found.");
        }
        return gameByPin.getPromptSet();
    }

    //TODO: test Integration?
    public List<Prompt> pickPrompts(PromptPostDTO userRequest, String gamePin) {
        int wantedTextPrompts = 4;
        int wantedTrueFalsePrompts = 2;
        int wantedDrawingPrompts = 3;

        if (userRequest.getTextNr() != null) {
            wantedTextPrompts = userRequest.getTextNr();
        }
        if (userRequest.getTruefalseNr() != null) {
            wantedTrueFalsePrompts = userRequest.getTruefalseNr();
        }
        if (userRequest.getDrawingNr() != null) {
            wantedDrawingPrompts = userRequest.getDrawingNr();
        }

        List<Prompt> allTextPrompts = promptRepository.findAllByPromptType(PromptType.TEXT);
        List<Prompt> allTrueFalsePrompts = promptRepository.findAllByPromptType(PromptType.TRUEFALSE);
        List<Prompt> allDrawingPrompts = promptRepository.findAllByPromptType(PromptType.DRAWING);

        List<Prompt> promptsForGame = new ArrayList<>();

        promptsForGame.addAll(selectNrOfPromptsFromList(allTextPrompts, wantedTextPrompts));
        promptsForGame.addAll(selectNrOfPromptsFromList(allTrueFalsePrompts, wantedTrueFalsePrompts));
        promptsForGame.addAll(selectNrOfPromptsFromList(allDrawingPrompts, wantedDrawingPrompts));
        addPromptsToGame(promptsForGame, gamePin);
        return promptsForGame;
    }

    public void addTimerToGame(PromptPostDTO promptPostDTO, String gamePin) {
        Game gameByPin = gameRepository.findByGamePin(gamePin);
        gameByPin.setTimer(promptPostDTO.getTimer());

        gameRepository.save(gameByPin);
        gameRepository.flush();
    }


    //TODO: test Integration?
    private Game addPromptsToGame(List<Prompt> promptsForGame, String gamePin) {
        Game gameByPin = gameRepository.findByGamePin(gamePin);
        if (gameByPin == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No game with this pin found.");
        }
        if (gameByPin.getStatus() != GameStatus.SELECTION) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Game is in the wrong state to take prompts.");
        }
        if (!gameByPin.getPromptSet().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This game already has prompts selected.");
        }

        gameByPin.setPromptSet(promptsForGame);

        gameByPin.setStatus(GameStatus.PROMPT);

        gameRepository.save(gameByPin);
        gameRepository.flush();

        return gameByPin;
    }

    /**
     * set up functions
     */

    private void initialisePromptRepository() throws PromptSetupException, IOException {
        try (BufferedReader input = new BufferedReader(new FileReader("src/main/resources/prompts.txt"))){
            String line;
            while ((line = input.readLine()) != null) {
                if (line.startsWith("\\")) {
                    continue;
                }
                String[] promptInfo = line.split(": ");
                Prompt newPrompt = parsePrompt(promptInfo);
                if (newPrompt == null) {
                    System.out.println("Could not properly parse prompt input: " + line);
                    continue;
                }
                promptRepository.save(newPrompt);
                promptRepository.flush();
                log.debug("created prompt: {}", newPrompt);
            }
        }
        catch (IOException e) {
            throw new PromptSetupException("Could not find file for prompts.");
        }
    }

    private void initialisePotentialQuestionRepository() throws PromptSetupException, IOException {
        try (BufferedReader input = new BufferedReader(new FileReader("src/main/resources/potentialQuestions.txt"))){
            String line;

            while ((line = input.readLine()) != null) {
                if (line.startsWith("\\")) {
                    continue;
                }
                String[] questionInfo = line.split(": ");
                PotentialQuestion newPotentialQuestion = parsePotentialQuestion(questionInfo);
                if (newPotentialQuestion == null) {
                    System.out.println("Could not properly parse potential question input: " + line);
                    continue;
                }
                potentialQuestionsRepository.save(newPotentialQuestion);
                potentialQuestionsRepository.flush();
                log.debug("created potential question: {}", newPotentialQuestion);
            }

            for (Prompt prompt : promptRepository.findAll()) {
                if (potentialQuestionsRepository.findAllByAssociatedPrompt(prompt) == null) {
                    throw new PromptSetupException("Prompt is missing a potential question!: " + prompt.getPromptNr().toString());
                }
            }
        }
        catch (IOException e) {
            throw new PromptSetupException("Could not find file for potential questions.");
        }
    }

    /**
     * Helper functions
     */

    private Prompt parsePrompt(String[] promptLine) {
        try {
            Prompt newPrompt = new Prompt();
            newPrompt.setPromptNr(Integer.parseInt(promptLine[0]));
            newPrompt.setPromptType(PromptType.transformToType(promptLine[1]));
            newPrompt.setPromptText(promptLine[2]);

            log.debug("Prompt parsed as: {}", newPrompt);
            return newPrompt;
        }
        catch (Exception e) {
            log.debug("Error Thrown: {}", e.getClass());
            return null;
        }
    }

    private PotentialQuestion parsePotentialQuestion(String[] potentialQuestionLine) {
        try {
            PotentialQuestion newPotentialQuestion = new PotentialQuestion();
            newPotentialQuestion.setAssociatedPrompt(promptRepository.findByPromptNr(Integer.valueOf(potentialQuestionLine[0])));
            newPotentialQuestion.setQuestionType(QuestionType.transformToType(potentialQuestionLine[1]));
            newPotentialQuestion.setQuestionText(potentialQuestionLine[2]);
            newPotentialQuestion.setRequiresTextInput(Boolean.parseBoolean(potentialQuestionLine[3]));
            newPotentialQuestion.setDisplayType(DisplayType.transformToType(potentialQuestionLine[4]));

            log.debug("Potential question parsed as: {}", newPotentialQuestion);
            return newPotentialQuestion;
        }
        catch (Throwable e) {
            log.debug("Error Thrown: {}", e.getClass());
            return null;
        }
    }

    private List<Prompt> selectNrOfPromptsFromList(List<Prompt> allPromptsOfType, int wantedNumber) {
        List<Prompt> selectedPrompts = new ArrayList<>();

        for (int i = 0; i < wantedNumber; i++) {
            if (allPromptsOfType.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You selected more text prompts than are available.");
            }
            int randomIndex = rand.nextInt(allPromptsOfType.size());
            Prompt randomPrompt = allPromptsOfType.get(randomIndex);
            selectedPrompts.add(randomPrompt);
            allPromptsOfType.remove(randomIndex);
        }

        return selectedPrompts;
    }


}
