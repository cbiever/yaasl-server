package yaasl.server.jsonapi;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class MultiData {

    private List<Element> elements;

    public MultiData(List<Element> elements) {
        this.elements = elements;
    }

    @JsonProperty("data")
    public List<Element> getElements() {
        return elements;
    }

    public void setElements(List<Element> elements) {
        this.elements = elements;
    }

}
