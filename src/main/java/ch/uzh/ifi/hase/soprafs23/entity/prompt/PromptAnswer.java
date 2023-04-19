package ch.uzh.ifi.hase.soprafs23.entity.prompt;

public interface PromptAnswer {

    // maybe this interface will not be used, because each type of answer is an own individual thing anyway

    long playerId = 0;
    long promptAnswerId = 0;
    int associatedPromptNr = 0;
    String answer = null;

}
