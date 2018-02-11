package yaasl.server.export;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import yaasl.server.model.Flight;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Component
public class CSVExporter {

    private static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    private Logger LOG = LoggerFactory.getLogger(getClass());

    public byte[] generate(List<Flight> flights) throws Exception {
        StringWriter writer = new StringWriter();
        flights.forEach(flight -> addFlight(flight, writer));
        return writer.toString().getBytes("UTF-8");
    }

    private void addFlight(Flight flight, StringWriter writer) {
        if (flight.getAircraft() != null) {
            writer.append(flight.getAircraft().getCallSign());
        }
        writer.append(",");

        if (flight.getPilot1() != null) {
            writer.append(flight.getPilot1().getName());
        }
        writer.append(",");

        if (flight.getStartTime() != null) {
            writer.append(timeFormat.format(flight.getStartTime()));
        }
        writer.append(",");

        if (flight.getStartLocation() != null) {
            writer.append(flight.getStartLocation().getName());
        }
        writer.append(",");

        if (flight.getLandingTime() != null) {
            writer.append(timeFormat.format(flight.getLandingTime()));
        }
        writer.append(",");

        if (flight.getLandingLocation() != null) {
            writer.append(flight.getLandingLocation().getName());
        }
        writer.append(",");

        if (flight.getPilot1Role() != null) {
            writer.append(flight.getPilot1Role().getDescription());
        }
        writer.append(",");

        if (flight.getPilot2() != null) {
            writer.append(flight.getPilot2().getName());
        }
        writer.append(",");

        if (flight.getPilot2Role() != null) {
            writer.append(flight.getPilot2Role().getDescription());
        }
        writer.append(",");

        if (flight.getTowPlane() != null) {
            writer.append(flight.getTowPlane().getCallSign());
        }
        writer.append(",");

        if (flight.getTowPilot() != null) {
            writer.append(flight.getTowPilot().getName());
        }
        writer.append(",");

        if (flight.getTowPlaneLandingTime() != null) {
            writer.append(timeFormat.format(flight.getTowPlaneLandingTime()));
        }
        writer.append(",");

        if (flight.getCostSharing() != null) {
            writer.append(flight.getCostSharing().getDescription());
        }
        writer.append(",");

        if (isNotEmpty(flight.getComment())) {
            writer.append(flight.getComment());
        }

        writer.append("\r\n");
    }

}
