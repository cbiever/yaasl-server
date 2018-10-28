package yaasl.server.export

import yaasl.server.model.Flight
import java.util.*
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlElementWrapper

class Data(@get:XmlElementWrapper(name = "flights")
           @get:XmlElement(name = "flight")
           var flights: List<Flight>, var location: String?, var date: Date?)
