package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.PromptType;
import ch.uzh.ifi.hase.soprafs23.entity.Prompt;
import ch.uzh.ifi.hase.soprafs23.exceptions.PromptSetupException;
import ch.uzh.ifi.hase.soprafs23.repository.PromptRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

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


    @Autowired
    public PromptService(@Qualifier("promptRepository") PromptRepository promptRepository) throws PromptSetupException {
        this.promptRepository = promptRepository;
        initialiseRepository();
    }

    public List<Prompt> getPrompts(){
        List<Prompt> allPromptsInRepository = promptRepository.findAll();
        return allPromptsInRepository;
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
                newPrompt.setPromptType(PromptType.transformToType(promptInfo[0]));
                newPrompt.setPromptText(promptInfo[1]);
                promptRepository.save(newPrompt);
                promptRepository.flush();
            }
        }catch(Exception e){
            System.out.println("Something went wrong while creating the prompts.");
            throw new PromptSetupException();
        }

    }


}
