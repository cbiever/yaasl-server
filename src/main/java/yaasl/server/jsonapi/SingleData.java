package yaasl.server.jsonapi;

public class SingleData {

    private Element data;

    public SingleData() {
    }

    public SingleData(Element data) {
        this.data = data;
    }

    public Element getData() {
        return data;
    }

    public void setData(Element data) {
        this.data = data;
    }

}
