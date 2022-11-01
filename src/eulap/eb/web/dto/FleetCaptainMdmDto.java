package eulap.eb.web.dto;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import eulap.eb.domain.hibernate.FleetCaptainMdm;

/**
 * A class that will hold the data of {@link FleetCaptainMdm}

 *
 */
public class FleetCaptainMdmDto extends FleetProfileDto{

	private List<FleetCaptainMdm> fleetCaptainMdms;
	private String fleetCaptainsJson;
	private String fleetCaptainErrMsg;

	public List<FleetCaptainMdm> getFleetCaptainMdms() {
		return fleetCaptainMdms;
	}

	public void setFleetCaptainMdms(List<FleetCaptainMdm> fleetCaptainMdms) {
		this.fleetCaptainMdms = fleetCaptainMdms;
	}

	public String getFleetCaptainsJson() {
		return fleetCaptainsJson;
	}

	public void setFleetCaptainsJson(String fleetCaptainsJson) {
		this.fleetCaptainsJson = fleetCaptainsJson;
	}

	public String getFleetCaptainErrMsg() {
		return fleetCaptainErrMsg;
	}

	public void setFleetCaptainErrMsg(String fleetCaptainErrMsg) {
		this.fleetCaptainErrMsg = fleetCaptainErrMsg;
	}

	public void serializeFleetCaptains(){
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		fleetCaptainsJson = gson.toJson(fleetCaptainMdms);
	}

	public void deserializeFleetCaptains () {
		Gson gson = new GsonBuilder()
				.excludeFieldsWithoutExposeAnnotation()
				.setDateFormat("MM/dd/yyyy")
				.create();
		Type type = new TypeToken <List<FleetCaptainMdm>>(){}.getType();
		fleetCaptainMdms = gson.fromJson(fleetCaptainsJson, type);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FleetCaptainMdmDto [fleetCaptains=").append(fleetCaptainMdms).append(", fleetCaptainsJson=")
				.append(fleetCaptainsJson).append(", fleetCaptainErrMsg=").append(fleetCaptainErrMsg).append("]");
		return builder.toString();
	}

}
