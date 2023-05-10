package ch.uzh.ifi.hase.soprafs23.rest.dto.prompt;

public class PromptPostDTO {
    private Integer textNr;
    private Integer truefalseNr;
    private Integer drawingNr;

    private Integer timer;

    public Integer getTextNr() {
        return textNr;
    }

    public void setTextNr(Integer textNr) {
        this.textNr = textNr;
    }

    public Integer getTruefalseNr() {
        return truefalseNr;
    }

    public void setTrueFalseNr(Integer trueFalseNr) {
        this.truefalseNr = trueFalseNr;
    }

    public Integer getDrawingNr() {
        return drawingNr;
    }

    public void setDrawingNr(Integer drawingNr) {
        this.drawingNr = drawingNr;
    }

    public Integer getTimer() {
        return timer;
    }

    public void setTimer(Integer timer) {
        this.timer = timer;
    }
}
