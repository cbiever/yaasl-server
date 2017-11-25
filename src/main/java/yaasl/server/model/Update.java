package yaasl.server.model;

import yaasl.server.jsonapi.Element;
import yaasl.server.jsonapi.SingleData;

public class Update {

    private String action;
    private SingleData payload;

    public Update(String action, Element payload) {
        this.action = action;
        this.payload = new SingleData(payload);
    }

    public Update(String action, String originatorID) {
        this.action = action;
        Element element = new Element();
        element.addAttribute("originatorID", originatorID);
        this.payload = new SingleData(element);
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public SingleData getPayload() {
        return payload;
    }

    public void setPayload(SingleData payload) {
        this.payload = payload;
    }

}
