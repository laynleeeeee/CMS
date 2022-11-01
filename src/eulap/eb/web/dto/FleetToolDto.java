package eulap.eb.web.dto;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import eulap.common.util.Page;
import eulap.eb.domain.hibernate.FleetToolCondition;

/**
 * Data Transfer Object for {@link FleetTool}

 *
 */
public class FleetToolDto extends FleetProfileDto{

	private Page<FleetToolCondition> fleetToolConditions;
	private String fleetToolConditionsJson;
	private String fleetToolsErrMsg;
	private List<FleetToolCondition> lsFleetToolConditions;
	private int pageNumber;

	public static final int TOOL_ITEM_CATEGORY_ID = 1;
	public static final int OR_TYPE_ID = 53;

	public Page<FleetToolCondition> getFleetToolConditions() {
		return fleetToolConditions;
	}

	public void setFleetToolConditions(Page<FleetToolCondition> fleetToolConditions) {
		this.fleetToolConditions = fleetToolConditions;
	}

	public String getFleetToolConditionsJson() {
		return fleetToolConditionsJson;
	}

	public void setFleetToolConditionsJson(String fleetToolConditionsJson) {
		this.fleetToolConditionsJson = fleetToolConditionsJson;
	}

	public String getFleetToolsErrMsg() {
		return fleetToolsErrMsg;
	}

	public void setFleetToolsErrMsg(String fleetToolsErrMsg) {
		this.fleetToolsErrMsg = fleetToolsErrMsg;
	}

	public List<FleetToolCondition> getLsFleetToolConditions() {
		return lsFleetToolConditions;
	}

	public void setLsFleetToolConditions(List<FleetToolCondition> lsFleetToolConditions) {
		this.lsFleetToolConditions = lsFleetToolConditions;
	}

	public void serializeFleetToolConditions(){
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		fleetToolConditionsJson = gson.toJson(lsFleetToolConditions);
	}

	public void deserializeFleetToolConditions () {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat("MM/dd/yyyy").create();
		Type type = new TypeToken <List<FleetToolCondition>>(){}.getType();
		lsFleetToolConditions = gson.fromJson(fleetToolConditionsJson, type);
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}
}
