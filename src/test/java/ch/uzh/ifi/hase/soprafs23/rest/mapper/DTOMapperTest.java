package ch.uzh.ifi.hase.soprafs23.rest.mapper;

import ch.uzh.ifi.hase.soprafs23.constant.CompletionStatus;
import ch.uzh.ifi.hase.soprafs23.constant.DisplayType;
import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.constant.PromptType;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.DrawingPromptAnswer;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.Prompt;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.TextPromptAnswer;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.TrueFalsePromptAnswer;
import ch.uzh.ifi.hase.soprafs23.entity.quiz.AnswerOption;
import ch.uzh.ifi.hase.soprafs23.entity.quiz.QuizAnswer;
import ch.uzh.ifi.hase.soprafs23.entity.quiz.QuizQuestion;
import ch.uzh.ifi.hase.soprafs23.rest.dto.*;
import ch.uzh.ifi.hase.soprafs23.rest.dto.prompt.DrawingPromptAnswerPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.prompt.PromptGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.prompt.TextPromptAnswerPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.prompt.TrueFalsePromptAnswerPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.quiz.QuizAnswerPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.quiz.QuizQuestionGetDTO;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class DTOMapperTest {

    @Test
    public void convertToGameGetDTO_fromGame_success() {
        QuizQuestion exampleQuestion = new QuizQuestion();

        Game testGame = new Game();
        testGame.setGameId(1L);
        testGame.setGamePin("123456");
        testGame.setStatus(GameStatus.LOBBY);
        testGame.setCurrentQuestion(exampleQuestion);
        testGame.setTimer(40);

        GameGetDTO gameGetDTO = DTOMapper.INSTANCE.convertToGameGetDTO(testGame);

        assertEquals(gameGetDTO.getGameId(), testGame.getGameId());
        assertEquals(gameGetDTO.getGamePin(), testGame.getGamePin());
        assertEquals(gameGetDTO.getStatus(), testGame.getStatus());
        assertEquals(gameGetDTO.getCurrentQuestion().getQuizQuestionText(), DTOMapper.INSTANCE.convertToQuizQuestionGetDTO(testGame.getCurrentQuestion()).getQuizQuestionText());
        assertEquals(gameGetDTO.getTimer(), testGame.getTimer());
    }

    @Test
    public void convertFromGamePutDTO_toGame_success() {
        GamePutDTO gamePutDTO = new GamePutDTO();
        gamePutDTO.setStatus(GameStatus.SELECTION);

        Game game = DTOMapper.INSTANCE.convertFromGamePutDTO(gamePutDTO);
        assertEquals(gamePutDTO.getStatus(), game.getStatus());
    }

    @Test
    public void convertToPlayerGetDTO_fromPlayer_success() {
        Player testPlayer = new Player();
        testPlayer.setPlayerId(1L);
        testPlayer.setPlayerName("test");
        testPlayer.setScore(100);
        testPlayer.setLatestScore(10);

        PlayerGetDTO playerGetDTO = DTOMapper.INSTANCE.convertToPlayerGetDTO(testPlayer);
        assertEquals(playerGetDTO.getPlayerId(), testPlayer.getPlayerId());
        assertEquals(playerGetDTO.getPlayerName(), testPlayer.getPlayerName());
        assertEquals(playerGetDTO.getScore(), testPlayer.getScore());
        assertEquals(playerGetDTO.getLatestScore(), testPlayer.getLatestScore());
    }

    @Test
    public void convertFromPlayerPostDTO_toPlayer_success() {
        PlayerPostDTO playerPostDTO = new PlayerPostDTO();
        playerPostDTO.setPlayerName("test");
        playerPostDTO.setIsHost(true);

        Player player = DTOMapper.INSTANCE.convertFromPlayerPostDTO(playerPostDTO);
        assertEquals(playerPostDTO.getPlayerName(), player.getPlayerName());
        assertEquals(playerPostDTO.isHost(), player.isHost());
    }

    @Test
    public void convertFromPlayerPutDTO_toPlayer_success() {
        PlayerPutDTO playerPutDTO = new PlayerPutDTO();
        playerPutDTO.setPlayerName("test");

        Player player = DTOMapper.INSTANCE.convertFromPlayerPutDTO(playerPutDTO);
        assertEquals(playerPutDTO.getPlayerName(), player.getPlayerName());
    }

    @Test
    public void convertToPromptGetDTO_fromPrompt_success() {
        Prompt testPrompt = new Prompt();
        testPrompt.setPromptId(1L);
        testPrompt.setPromptNr(999);
        testPrompt.setPromptText("Tell a story");
        testPrompt.setPromptType(PromptType.TRUEFALSE);

        PromptGetDTO getDTO = DTOMapper.INSTANCE.convertToPromptGetDTO(testPrompt);
        assertEquals(testPrompt.getPromptId(), getDTO.getPromptId());
        assertEquals(testPrompt.getPromptNr(), getDTO.getPromptNr());
        assertEquals(testPrompt.getPromptType(), getDTO.getPromptType());
        assertEquals(testPrompt.getPromptText(), getDTO.getPromptText());
    }

    @Test
    public void convertFromTextPromptAnswerPostDTO_toTextPromptAnswer_success() {
        TextPromptAnswerPostDTO postDTO = new TextPromptAnswerPostDTO();
        postDTO.setAssociatedPromptNr(1);
        postDTO.setAnswer("some answer");

        TextPromptAnswer textPromptAnswer = DTOMapper.INSTANCE.convertFromTextPromptAnswerPostDTO(postDTO);
        assertEquals(textPromptAnswer.getAssociatedPromptNr(), postDTO.getAssociatedPromptNr());
        assertEquals(textPromptAnswer.getAnswer(), postDTO.getAnswer());
    }

    @Test
    public void convertFromTrueFalsePromptAnswerPostDTO_toTrueFalsePromptAnswer_success() {
        TrueFalsePromptAnswerPostDTO postDTO = new TrueFalsePromptAnswerPostDTO();
        postDTO.setAssociatedPromptNr(1);
        postDTO.setAnswerBoolean(true);
        postDTO.setAnswerText("some story");

        TrueFalsePromptAnswer tfPromptAnswer = DTOMapper.INSTANCE.convertFromTrueFalsePromptAnswerPostDTO(postDTO);
        assertEquals(tfPromptAnswer.getAssociatedPromptNr(), postDTO.getAssociatedPromptNr());
        assertEquals(tfPromptAnswer.getAnswerBoolean(), postDTO.getAnswerBoolean());
        assertEquals(tfPromptAnswer.getAnswerText(), postDTO.getAnswerText());
    }

    @Test
    public void convertFromDrawingPromptAnswerPostDTO_toDrawingPromptAnswer_success() {
        DrawingPromptAnswerPostDTO postDTO = new DrawingPromptAnswerPostDTO();
        postDTO.setAssociatedPromptNr(1);
        postDTO.setAnswerDrawing("some drawing");

        DrawingPromptAnswer drawingPromptAnswer = DTOMapper.INSTANCE.convertFromDrawingPromptAnswerPostDTO(postDTO);
        assertEquals(drawingPromptAnswer.getAssociatedPromptNr(), postDTO.getAssociatedPromptNr());
        assertEquals(drawingPromptAnswer.getAnswerDrawing(), postDTO.getAnswerDrawing());
    }

    @Test
    public void convertToQuizQuestionGetDTO_fromQuizQuestion_success() {
        QuizQuestion testQuizQuestion = new QuizQuestion();
        testQuizQuestion.setQuestionId(1L);
        testQuizQuestion.setQuizQuestionText("text");
        testQuizQuestion.setImageToDisplay("some image");
        testQuizQuestion.setStoryToDisplay("some story");
        testQuizQuestion.setAnswerOptions(List.of(new AnswerOption()));
        testQuizQuestion.setAnswerDisplayType(DisplayType.IMAGE);
        testQuizQuestion.setCorrectAnswer(new AnswerOption());
        testQuizQuestion.setQuestionStatus(CompletionStatus.NOT_FINISHED);

        QuizQuestionGetDTO getDTO = DTOMapper.INSTANCE.convertToQuizQuestionGetDTO(testQuizQuestion);
        assertEquals(getDTO.getQuestionId(), testQuizQuestion.getQuestionId());
        assertEquals(getDTO.getQuizQuestionText(), testQuizQuestion.getQuizQuestionText());
        assertEquals(getDTO.getImageToDisplay(), testQuizQuestion.getImageToDisplay());
        assertEquals(getDTO.getStoryToDisplay(), testQuizQuestion.getStoryToDisplay());
        assertEquals(getDTO.getAnswerOptions(), testQuizQuestion.getAnswerOptions());
        assertEquals(getDTO.getAnswerDisplayType(), testQuizQuestion.getAnswerDisplayType());
        assertEquals(getDTO.getCorrectAnswer(), testQuizQuestion.getCorrectAnswer());
        assertEquals(getDTO.getQuestionStatus(), testQuizQuestion.getQuestionStatus());
    }

    @Test
    public void convertFromQuizAnswerPostDTO_toQuizAnswer_success() {
        QuizAnswerPostDTO postDTO = new QuizAnswerPostDTO();
        postDTO.setPickedAnswerOptionId(99L);
        postDTO.setTimer(10);

        QuizAnswer quizAnswer = DTOMapper.INSTANCE.convertFromQuizAnswerPostDTO(postDTO);
        assertEquals(quizAnswer.getPickedAnswerOptionId(), postDTO.getPickedAnswerOptionId());
        assertEquals(quizAnswer.getTimer(), postDTO.getTimer());
    }

}