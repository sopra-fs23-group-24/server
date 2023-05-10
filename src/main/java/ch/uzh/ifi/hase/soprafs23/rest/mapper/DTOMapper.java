package ch.uzh.ifi.hase.soprafs23.rest.mapper;

import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.DrawingPromptAnswer;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.Prompt;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.TextPromptAnswer;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.TrueFalsePromptAnswer;
import ch.uzh.ifi.hase.soprafs23.entity.quiz.QuizAnswer;
import ch.uzh.ifi.hase.soprafs23.entity.quiz.QuizQuestion;
import ch.uzh.ifi.hase.soprafs23.rest.dto.*;
import ch.uzh.ifi.hase.soprafs23.rest.dto.prompt.*;
import ch.uzh.ifi.hase.soprafs23.rest.dto.quiz.QuizAnswerPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.quiz.QuizQuestionGetDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * DTOMapper
 * This class is responsible for generating classes that will automatically
 * transform/map the internal representation
 * of an entity (e.g., the User) to the external/API representation (e.g.,
 * UserGetDTO for getting, UserPostDTO for creating)
 * and vice versa.
 * Additional mappers can be defined for new entities.
 * Always created one mapper for getting information (GET) and one mapper for
 * creating information (POST).
 */

@Mapper
public interface DTOMapper {

    DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);


    // game
    @Mapping(source = "gameId", target = "gameId")
    @Mapping(source = "gamePin", target = "gamePin")
    @Mapping(source = "status", target = "status")
    // does it need the timer - i would say so.
    @Mapping(source = "timer", target = "timer")
    GameGetDTO convertToGameGetDTO(Game game);

    @Mapping(source = "status", target = "status")
    Game convertFromGamePutDTO(GamePutDTO game);


    // player
    @Mapping(source = "playerId", target = "playerId")
    @Mapping(source = "playerName", target = "playerName")
    @Mapping(source = "score", target = "score")
    PlayerGetDTO convertToPlayerGetDTO(Player player);

    @Mapping(source = "playerName", target = "playerName")
    @Mapping(source = "host", target = "host")
    Player convertFromPlayerPostDTO(PlayerPostDTO game);

    @Mapping(source = "playerName", target = "playerName")
    Player convertFromPlayerPutDTO(PlayerPutDTO player);


    // prompt answers
    @Mapping(source = "associatedPromptNr", target = "associatedPromptNr")
    @Mapping(source = "answer", target = "answer")
    TextPromptAnswer convertFromTextPromptAnswerPostDTO(TextPromptAnswerPostDTO textPromptAnswerPostDTO);

    @Mapping(source = "associatedPromptNr", target = "associatedPromptNr")
    @Mapping(source = "answerText", target = "answerText")
    @Mapping(source = "answerBoolean", target = "answerBoolean")
    TrueFalsePromptAnswer convertFromTrueFalsePromptAnswerPostDTO(TrueFalsePromptAnswerPostDTO trueFalsePromptAnswerPostDTO);

    @Mapping(source = "associatedPromptNr", target = "associatedPromptNr")
    @Mapping(source = "answerDrawing", target = "answerDrawing")
    DrawingPromptAnswer convertFromDrawingPromptAnswerPostDTO(DrawingPromptAnswerPostDTO drawingPromptAnswerPostDTO);


    // prompt
    @Mapping(source = "promptId", target = "promptId")
    @Mapping(source = "promptNr", target = "promptNr")
    @Mapping(source = "promptType", target = "promptType")
    @Mapping(source = "promptText", target = "promptText")
    PromptGetDTO convertToPromptGetDTO(Prompt prompt);


    // quiz question
    @Mapping(source = "questionId", target = "questionId")
    @Mapping(source = "quizQuestionText", target = "quizQuestionText")
    @Mapping(source = "imageToDisplay", target = "imageToDisplay")
    @Mapping(source = "storyToDisplay", target = "storyToDisplay")
    @Mapping(source = "answerOptions", target = "answerOptions")
    @Mapping(source = "answerDisplayType", target = "answerDisplayType")
    @Mapping(source = "correctAnswer", target = "correctAnswer")
    @Mapping(source = "questionStatus", target = "questionStatus")
    QuizQuestionGetDTO convertToQuizQuestionGetDTO(QuizQuestion question);


    // quiz answer
    @Mapping(source = "pickedAnswerOptionId", target = "pickedAnswerOptionId")
    QuizAnswer convertFromQuizAnswerPostDTO(QuizAnswerPostDTO quizAnswerPostDTO);


}
