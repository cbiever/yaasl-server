package yaasl.server.jsonapi;

import java.util.ArrayList;
import java.util.List;

public class MultiData {

    private List<Element> data = new ArrayList<Element>();

    public List<Element> getData() {
        return data;
    }

    public void setData(List<Element> data) {
        this.data = data;
    }

}
