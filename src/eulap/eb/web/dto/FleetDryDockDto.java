package eulap.eb.web.dto;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import eulap.eb.domain.hibernate.FleetDryDock;

/**
 * Data transfer object for {@link FleetDryDock}

 *
 */
public class FleetDryDockDto extends FleetProfileDto {
	private List<FleetDryDock> dryDockings;
	private String dryDockingsJson;
	private String dryDockingErrMsg;

	public List<FleetDryDock> getDryDockings() {
		return dryDockings;
	}

	public void setDryDockings(List<FleetDryDock> dryDockings) {
		this.dryDockings = dryDockings;
	}

	public String getDryDockingsJson() {
		return dryDockingsJson;
	}

	public void setDryDockingsJson(String dryDockingsJson) {
		this.dryDockingsJson = dryDockingsJson;
	}

	public String getDryDockingErrMsg() {
		return dryDockingErrMsg;
	}

	public void setDryDockingErrMsg(String dryDockingErrMsg) {
		this.dryDockingErrMsg = dryDockingErrMsg;
	}

	public void serializeFleetDryDocking(){
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		dryDockingsJson = gson.toJson(dryDockings);
	}

	public void deserializeFleetDryDocking() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat("MM/dd/yyyy").create();
		Type type = new TypeToken <List<FleetDryDock>>(){}.getType();
		dryDockings = gson.fromJson(dryDockingsJson, type);
	}

	@Override
	public String toString() {
		return "FleetDryDockingDto [dryDockings=" + dryDockings + ", dryDockingsJson=" + dryDockingsJson
				+ ", dryDockingErrMsg=" + dryDockingErrMsg + "]";
	}

}
