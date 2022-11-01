package eulap.eb.web.dto;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import eulap.eb.domain.hibernate.FleetPms;

/**
 * Fleet Preventive maintenance schedule Dto.

 */
public class FleetPmsDto extends FleetProfileDto {
	private List<FleetPms> fleetPms;
	private String fleetPmslJson;
	private String fleetPmsErrMsg;
	private Integer fleetTypeId;

	public void serializeFleetPms(){
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		fleetPmslJson = gson.toJson(fleetPms);
	}

	public void deserializeFleetPms() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat("MM/dd/yyyy").create();
		Type type = new TypeToken <List<FleetPms>>(){}.getType();
		fleetPms = gson.fromJson(fleetPmslJson, type);
	}

	public Integer getFleetTypeId() {
		return fleetTypeId;
	}

	public void setFleetTypeId(Integer fleetTypeId) {
		this.fleetTypeId = fleetTypeId;
	}

	public List<FleetPms> getFleetPms() {
		return fleetPms;
	}

	public void setFleetPms(List<FleetPms> fleetPms) {
		this.fleetPms = fleetPms;
	}

	public String getFleetPmslJson() {
		return fleetPmslJson;
	}

	public void setFleetPmslJson(String fleetPmslJson) {
		this.fleetPmslJson = fleetPmslJson;
	}

	public String getFleetPmsErrMsg() {
		return fleetPmsErrMsg;
	}

	public void setFleetPmsErrMsg(String fleetPmsErrMsg) {
		this.fleetPmsErrMsg = fleetPmsErrMsg;
	}

	@Override
	public String toString() {
		return "FleetPmsDto [fleetPms=" + fleetPms + ", fleetPmslJson=" + fleetPmslJson
				+ ", fleetPmsErrMsg=" + fleetPmsErrMsg + ", fleetTypeId=" + fleetTypeId + "]";
	}
}
