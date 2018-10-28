package yaasl.server.persistence

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import yaasl.server.model.Flight
import yaasl.server.model.Location

import java.util.Date

interface FlightRepository : CrudRepository<Flight, Long> {

    @Query("select flight from Flight flight")
    fun findAllFlights(): List<Flight>

    @Query("select flight from Flight flight where flight.startLocation = ?1 or flight.landingLocation = ?1")
    fun findByLocation(location: Location): List<Flight>

    @Query("select flight from Flight flight where (flight.startLocation = ?1 or flight.landingLocation = ?1) and (flight.startTime between ?2 and ?3 or flight.startTime is null)")
    fun findByLocationAndDate(location: Location, beginningOfDate: Date, endOfDay: Date): List<Flight>

}
