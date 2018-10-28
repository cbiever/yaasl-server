package yaasl.server.jsonapi

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonPropertyOrder

import java.util.ArrayList
import java.util.HashMap

import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL

@JsonPropertyOrder("id", "type", "attributes")
class Element {

    private var id: String? = null
    private var type: String? = null
    private var attributes: MutableMap<String, Any?> = HashMap()
    private var relationships: MutableMap<String, Any?> = HashMap()
    private var included: MutableList<Element> = ArrayList()

    fun getId() = id

    fun setId(id: String?) {
        this.id = id
    }

    fun getType() = type

    fun setType(type: String?) {
        this.type = type
    }

    fun addAttribute(name: String, value: Any?) {
        attributes[name] = value
    }

    @JsonInclude(NON_NULL)
    fun getAttributes(): Map<String, Any?> {
        return attributes
    }

    fun setAttributes(attributes: MutableMap<String, Any?>) {
        this.attributes = attributes
    }

    fun addRelationship(name: String, value: Any) {
        relationships[name] = value
    }

    @JsonInclude(NON_NULL)
    fun getRelationships(): Map<String, Any?> {
        return relationships
    }

    fun setRelationships(relationships: MutableMap<String, Any?>) {
        this.relationships = relationships
    }

    fun addIncluded(element: Element) {
        included.add(element)
    }

    @JsonInclude(NON_NULL)
    fun getIncluded(): List<Element> {
        return included
    }

    fun setIncluded(included: MutableList<Element>) {
        this.included = included
    }

}
