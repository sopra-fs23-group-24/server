package ch.uzh.ifi.hase.soprafs23.rest.dto;

public class PromptPostDTO {
    private Integer textNr;
    private Integer truefalseNr;
    private Integer drawingNr;

    public Integer getTextNr() {
        return textNr;
    }

    public void setTextNr(Integer textNr) {this.textNr = textNr;
    }

    public Integer getTruefalseNr() {
        return truefalseNr;
    }

    public void setTruefalseNr(Integer TFNr) {
        this.truefalseNr = TFNr;
    }

    public Integer getDrawingNr() {
        return drawingNr;
    }

    public void setDrawingNr(Integer drawingNr) {this.drawingNr = drawingNr;
    }
}
