package eulap.eb.web.dto;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import eulap.eb.domain.hibernate.FleetVoyage;

/**
 * Data Transfer Object for {@link FleetVoyage}

 *
 */
public class FleetVoyageDto extends FleetProfileDto{
	private List<FleetVoyage> fleetVoyages;
	private String fleetVoyagesJson;
	private String fleetVoyagesErrMsg;

	public List<FleetVoyage> getFleetVoyages() {
		return fleetVoyages;
	}

	public void setFleetVoyages(List<FleetVoyage> fleetVoyages) {
		this.fleetVoyages = fleetVoyages;
	}

	public String getFleetVoyagesJson() {
		return fleetVoyagesJson;
	}

	public void setFleetVoyagesJson(String fleetVoyagesJson) {
		this.fleetVoyagesJson = fleetVoyagesJson;
	}

	public String getFleetVoyagesErrMsg() {
		return fleetVoyagesErrMsg;
	}

	public void setFleetVoyagesErrMsg(String fleetVoyagesErrMsg) {
		this.fleetVoyagesErrMsg = fleetVoyagesErrMsg;
	}

	public void serializeFleetVoyages(){
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		fleetVoyagesJson = gson.toJson(fleetVoyages);
	}

	public void deserializeFleetVoyages() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat("MM/dd/yyyy").create();
		Type type = new TypeToken <List<FleetVoyage>>(){}.getType();
		fleetVoyages = gson.fromJson(fleetVoyagesJson, type);
	}

	@Override
	public String toString() {
		return "FleetVoyagesDto [fleetVoyages=" + fleetVoyages + ", fleetVoyagesJson=" + fleetVoyagesJson + "]";
	}

}
