package eulap.eb.web.dto;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import eulap.eb.domain.hibernate.FleetDriver;

/**
 * Data transfer object for {@link FleetDriver}

 *
 */
public class FleetDriverDto extends FleetProfileDto {
	private String fleetDriversJson;
	private List<FleetDriver> fleetDrivers;
	private String fleetDriversErrMsg;

	public String getFleetDriversJson() {
		return fleetDriversJson;
	}

	public void setFleetDriversJson(String fleetDriversJson) {
		this.fleetDriversJson = fleetDriversJson;
	}

	public List<FleetDriver> getFleetDrivers() {
		return fleetDrivers;
	}

	public void setFleetDrivers(List<FleetDriver> fleetDrivers) {
		this.fleetDrivers = fleetDrivers;
	}

	public void serializeFleetDrivers (){
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		fleetDriversJson = gson.toJson(fleetDrivers);
	}

	public void deserializeFleetDrivers () {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat("MM/dd/yyyy").create();
		Type type = new TypeToken <List<FleetDriver>>(){}.getType();
		fleetDrivers = gson.fromJson(fleetDriversJson, type);
	}

	public String getFleetDriversErrMsg() {
		return fleetDriversErrMsg;
	}

	public void setFleetDriversErrMsg(String fleetDriversErrMsg) {
		this.fleetDriversErrMsg = fleetDriversErrMsg;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FleetDriverDto [fleetDriversJson=").append(fleetDriversJson).append(", fleetDrivers=")
				.append(fleetDrivers).append("]");
		return builder.toString();
	}
}
