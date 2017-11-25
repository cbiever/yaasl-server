package yaasl.server.jsonapi;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonPropertyOrder({"id", "type", "attributes"})
public class Element {

    private String id;
    private String type;
    private Map<String, Object> attributes;
    private Map<String, Object> relationships;
    private List<Element> included;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void addAttribute(String name, Object value) {
        if (attributes == null) {
            attributes = new HashMap<String, Object>();
        }
        attributes.put(name, value);
    }

    @JsonInclude(NON_NULL)
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public void addRelationship(String name, Object value) {
       if (relationships == null) {
           relationships = new HashMap<String, Object>();
       }
       relationships.put(name, value);
    }

    @JsonInclude(NON_NULL)
    public Map<String, Object> getRelationships() {
        return relationships;
    }

    public void setRelationships(Map<String, Object> relationships) {
        this.relationships = relationships;
    }

    public void addIncluded(Element element) {
        if (included == null) {
            included = new ArrayList<Element>();
        }
        included.add(element);
    }

    @JsonInclude(NON_NULL)
    public List<Element> getIncluded() {
        return included;
    }

    public void setIncluded(List<Element> included) {
        this.included = included;
    }
}
