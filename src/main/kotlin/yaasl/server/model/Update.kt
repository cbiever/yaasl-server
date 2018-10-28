package yaasl.server.model

import yaasl.server.jsonapi.Element
import yaasl.server.jsonapi.SingleData

class Update {

    var action: String? = null
    var payload: SingleData? = null

    constructor(action: String, payload: Element) {
        this.action = action
        this.payload = SingleData(payload)
    }

    constructor(action: String, originatorID: String) {
        this.action = action
        val element = Element()
        element.addAttribute("originatorID", originatorID)
        this.payload = SingleData(element)
    }

}
