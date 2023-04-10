package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.constant.PromptType;
import ch.uzh.ifi.hase.soprafs23.constant.QuestionType;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
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
import java.io.FileNotFoundException;
import java.io.FileReader;
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

    @Autowired
    public PromptService(@Qualifier("promptRepository") PromptRepository promptRepository, @Qualifier("potentialQuestionRepository") PotentialQuestionsRepository potentialQuestionsRepository) throws PromptSetupException {
        this.promptRepository = promptRepository;
        this.potentialQuestionsRepository = potentialQuestionsRepository;
        initialiseRepository();
    }

    @Autowired
    private void setGameService(GameService gameService) {
        this.gameService = gameService;
    }

    public List<Prompt> getPrompts(){
      return promptRepository.findAll();
    }

    private void initialiseRepository() throws PromptSetupException {
        try{
            BufferedReader promptsInput = new BufferedReader(new FileReader("src/main/resources/prompts.txt"));
            String line;
            while((line = promptsInput.readLine()) != null){
                if(line.startsWith("\\")){
                    continue;
                }
                String[] promptInfo = line.split(": ");
                Prompt newPrompt = new Prompt();
                newPrompt.setPromptNr(Integer.parseInt(promptInfo[0]));
                newPrompt.setPromptType(PromptType.transformToType(promptInfo[1]));
                newPrompt.setPromptText(promptInfo[2]);
                promptRepository.save(newPrompt);
                promptRepository.flush();
            }


            BufferedReader potentialQuestionsInput = new BufferedReader(new FileReader("src/main/resources/potentialQuestions.txt"));
            while((line = potentialQuestionsInput.readLine()) != null){
                if(line.startsWith("\\")){
                    continue;
                }
                String[] questionInfo = line.split(": ");
                PotentialQuestion newPotentialQuestion = new PotentialQuestion();
                newPotentialQuestion.setAssociatedPrompt(promptRepository.findByPromptNr(Long.valueOf(questionInfo[0])));
                newPotentialQuestion.setQuestionType(QuestionType.transformToType(questionInfo[1]));
                newPotentialQuestion.setQuestionText(questionInfo[2]);
                potentialQuestionsRepository.save(newPotentialQuestion);
                potentialQuestionsRepository.flush();
            }

            for(Prompt prompt : promptRepository.findAll()){
                if(potentialQuestionsRepository.findAllByAssociatedPrompt(prompt) == null){
                  System.out.println("Prompt is missing a potential question!");
                    throw new PromptSetupException();
                }
            }
        }catch(Exception e){
            System.out.println("Something went wrong while creating the prompts.");
            throw new PromptSetupException();
        }
    }

    public List<Prompt> pickPrompts(PromptPostDTO userRequest, String gamePin){

        int wantedTextPrompts = userRequest.getTextNr();
        int wantedTrueFalsePrompts = userRequest.getTruefalseNr();
        int wantedDrawingPrompts = userRequest.getDrawingNr();

        List<Prompt> allTextPrompts = promptRepository.findAllByPromptType(PromptType.TEXT);
        List<Prompt> allTrueFalsePrompts = promptRepository.findAllByPromptType(PromptType.TRUEFALSE);
        List<Prompt> allDrawingPrompts = promptRepository.findAllByPromptType(PromptType.DRAWING);

        List<Prompt> promptsForGame = new ArrayList<>();
        Random rand = new Random();

        for (int i = 0; i < wantedTextPrompts; i++) {
            if(allTextPrompts.size() < 1){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You selected more text prompts than are available.");
            }
            int randomIndex = rand.nextInt(allTextPrompts.size());
            Prompt randomPrompt = allTextPrompts.get(randomIndex);
            promptsForGame.add(randomPrompt);
            allTextPrompts.remove(randomIndex);
        }

        for (int i = 0; i < wantedTrueFalsePrompts; i++) {
            if(allTrueFalsePrompts.size() < 1){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You selected more true-false prompts than are available.");
            }
            int randomIndex = rand.nextInt(allTrueFalsePrompts.size());
            Prompt randomPrompt = allTrueFalsePrompts.get(randomIndex);
            promptsForGame.add(randomPrompt);
            allTrueFalsePrompts.remove(randomIndex);
        }

        for (int i = 0; i < wantedDrawingPrompts; i++) {
            if(allDrawingPrompts.size() < 1){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You selected more drawing prompts than are available.");
            }
            int randomIndex = rand.nextInt(allDrawingPrompts.size());
            Prompt randomPrompt = allDrawingPrompts.get(randomIndex);
            promptsForGame.add(randomPrompt);
            allDrawingPrompts.remove(randomIndex);
        }

        gameService.addPromptsToGame(promptsForGame, gamePin);
        return promptsForGame;
    }


}
