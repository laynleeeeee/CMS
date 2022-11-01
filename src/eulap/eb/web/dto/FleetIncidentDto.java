package eulap.eb.web.dto;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import eulap.eb.domain.hibernate.FleetIncident;

/**
 * Data transfer object for {@link FleetIncident}

 *
 */
public class FleetIncidentDto extends FleetProfileDto{

	private List<FleetIncident> fleetIncidents;
	private String fleetIncidentJson;
	private String fleetIncidentErrorMsg;

	public List<FleetIncident> getFleetIncidents() {
		return fleetIncidents;
	}

	public void setFleetIncidents(List<FleetIncident> fleetIncidents) {
		this.fleetIncidents = fleetIncidents;
	}

	public String getFleetIncidentJson() {
		return fleetIncidentJson;
	}

	public void setFleetIncidentJson(String fleetIncidentJson) {
		this.fleetIncidentJson = fleetIncidentJson;
	}

	public void serializeFleetIncidents(){
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		fleetIncidentJson = gson.toJson(fleetIncidents);
	}

	public void deserializeFleetIncidents () {
		Gson gson = new GsonBuilder()
				.excludeFieldsWithoutExposeAnnotation()
				.setDateFormat("MM/dd/yyyy")
				.create();
		Type type = new TypeToken <List<FleetIncident>>(){}.getType();
		fleetIncidents = gson.fromJson(fleetIncidentJson, type);
	}

	public String getFleetIncidentErrorMsg() {
		return fleetIncidentErrorMsg;
	}

	public void setFleetIncidentErrorMsg(String fleetIncidentErrorMsg) {
		this.fleetIncidentErrorMsg = fleetIncidentErrorMsg;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FleetIncidentDto [fleetIncidents=").append(fleetIncidents).append(", fleetIncidentJson=")
				.append(fleetIncidentJson).append("]");
		return builder.toString();
	}
}
