package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.AdditionalDisplayType;
import ch.uzh.ifi.hase.soprafs23.constant.PromptType;
import ch.uzh.ifi.hase.soprafs23.constant.QuestionType;
import ch.uzh.ifi.hase.soprafs23.entity.PotentialQuestion;
import ch.uzh.ifi.hase.soprafs23.entity.Prompt;
import ch.uzh.ifi.hase.soprafs23.exceptions.PromptSetupException;
import ch.uzh.ifi.hase.soprafs23.repository.PotentialQuestionsRepository;
import ch.uzh.ifi.hase.soprafs23.repository.PromptRepository;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PromptPostDTO;
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

    private final PotentialQuestionsRepository potentialQuestionsRepository;

    private GameService gameService;

    private final Random rand = SecureRandom.getInstanceStrong();

    private BufferedReader input;

    @Autowired
    public PromptService(@Qualifier("promptRepository") PromptRepository promptRepository, @Qualifier("potentialQuestionRepository") PotentialQuestionsRepository potentialQuestionsRepository) throws PromptSetupException, NoSuchAlgorithmException, IOException {
        this.promptRepository = promptRepository;
        this.potentialQuestionsRepository = potentialQuestionsRepository;
        initialisePromptRepository();
        initialisePotentialQuestionRepository();
    }

    @Autowired
    private void setGameService(GameService gameService) {
        this.gameService = gameService;
    }

    //TODO: test Integration?
    public List<Prompt> getPrompts() {
        return promptRepository.findAll();
    }

    //TODO: test Integration?
    //TODO test Service? (issues because needs GameService)
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

        gameService.addPromptsToGame(promptsForGame, gamePin);
        return promptsForGame;
    }

    /**
     * set up functions
     */

    //TODO: test Integration?
    //TODO: test service?
    private void initialisePromptRepository() throws PromptSetupException, IOException {
        try{
            input = new BufferedReader(new FileReader("src/main/resources/prompts.txt"));
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
        }finally {
            input.close();
        }

    }

    //TODO: test Integration?
    //TODO: test service?
    private void initialisePotentialQuestionRepository() throws PromptSetupException, IOException {
        try {
            String line;
            input = new BufferedReader(new FileReader("src/main/resources/potentialQuestions.txt"));
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

            /*for (PotentialQuestion question : potentialQuestionsRepository.findAll()) {
            testCreateQuestions(question);
            }*/
        }catch(IOException e){
            throw new PromptSetupException("Could not find file for potential questions.");
        }finally {
            input.close();
        }
    }

    /**
     * Helper functions
     */
    //TODO: test Integration?
    // TODO: test Service?
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

    //TODO: test Integration?
    // TODO: test Service?
    private PotentialQuestion parsePotentialQuestion(String[] potentialQuestionLine) {
        try {
            PotentialQuestion newPotentialQuestion = new PotentialQuestion();
            newPotentialQuestion.setAssociatedPrompt(promptRepository.findByPromptNr(Integer.valueOf(potentialQuestionLine[0])));
            newPotentialQuestion.setQuestionType(QuestionType.transformToType(potentialQuestionLine[1]));
            newPotentialQuestion.setQuestionText(potentialQuestionLine[2]);
            newPotentialQuestion.setRequiresTextInput(Boolean.parseBoolean(potentialQuestionLine[3]));
            newPotentialQuestion.setDisplayType(AdditionalDisplayType.transformToType(potentialQuestionLine[4]));

            log.debug("Potential question parsed as: {}", newPotentialQuestion);
            return newPotentialQuestion;
        }
        catch (Throwable e) {
            log.debug("Error Thrown: {}", e.getClass());
            return null;
        }
    }

    //TODO: test Integration?
    // TODO: test Service?
    private List<Prompt> selectNrOfPromptsFromList(List<Prompt> allPromptsOfType, int wantedNumber) {
        List<Prompt> selectedPrompts = new ArrayList<>();

        for (int i = 0; i < wantedNumber; i++) {
            if (allPromptsOfType.size() < 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You selected more text prompts than are available.");
            }
            int randomIndex = rand.nextInt(allPromptsOfType.size());
            Prompt randomPrompt = allPromptsOfType.get(randomIndex);
            selectedPrompts.add(randomPrompt);
            allPromptsOfType.remove(randomIndex);
        }

        return selectedPrompts;
    }

    private void testCreateQuestions(PotentialQuestion pq) {
        StringBuilder outputString = new StringBuilder();
        if (pq.isRequiresTextInput()) {
            if (pq.getQuestionType() == QuestionType.PLAYER) {
                outputString.append(String.format(pq.getQuestionText(), "PROMPT INPUT"));
            }
            else {
                outputString.append(String.format(pq.getQuestionText(), "USERNAME"));
            }
        }
        else {
            outputString.append(pq.getQuestionText());
        }
        if (pq.getDisplayType() == AdditionalDisplayType.IMAGE) {
            outputString.append(" ---- SHOW IMAGE TO GUESS");
        }
        else if (pq.getDisplayType() == AdditionalDisplayType.TEXT) {
            outputString.append(" ---- SHOW A TEXT");
        }
        System.out.println(outputString);
    }
}
