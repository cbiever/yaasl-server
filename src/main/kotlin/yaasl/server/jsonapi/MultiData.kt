package yaasl.server.jsonapi

import com.fasterxml.jackson.annotation.JsonProperty

import java.util.ArrayList

class MultiData(@get:JsonProperty("data")
                var elements: List<Element>?)
