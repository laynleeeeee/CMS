package eulap.eb.web.dto;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import eulap.eb.domain.hibernate.FleetInsurancePermitRenewal;

/**
 * Fleet Insurance PermitRenewal Dto.

 */
public class FleetInsurancePermitRenewalDto extends FleetProfileDto {
	private List<FleetInsurancePermitRenewal> insurancePermitRenewals;
	private String insurancePermitRenewalJson;
	private String fleetIprErrMsg;
	private Integer fleetTypeId;

	public void serializeInsurancePermitRenewals(){
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		insurancePermitRenewalJson = gson.toJson(insurancePermitRenewals);
	}

	public void deserializeInsurancePermitRenewals() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat("MM/dd/yyyy").create();
		Type type = new TypeToken <List<FleetInsurancePermitRenewal>>(){}.getType();
		insurancePermitRenewals = gson.fromJson(insurancePermitRenewalJson, type);
	}

	public List<FleetInsurancePermitRenewal> getInsurancePermitRenewals() {
		return insurancePermitRenewals;
	}

	public void setInsurancePermitRenewals(List<FleetInsurancePermitRenewal> insurancePermitRenewals) {
		this.insurancePermitRenewals = insurancePermitRenewals;
	}

	public String getInsurancePermitRenewalJson() {
		return insurancePermitRenewalJson;
	}

	public void setInsurancePermitRenewalJson(String insurancePermitRenewalJson) {
		this.insurancePermitRenewalJson = insurancePermitRenewalJson;
	}

	public String getFleetIprErrMsg() {
		return fleetIprErrMsg;
	}

	public void setFleetIprErrMsg(String fleetIprErrMsg) {
		this.fleetIprErrMsg = fleetIprErrMsg;
	}

	public Integer getFleetTypeId() {
		return fleetTypeId;
	}

	public void setFleetTypeId(Integer fleetTypeId) {
		this.fleetTypeId = fleetTypeId;
	}

	@Override
	public String toString() {
		return "FleetInsurancePermitRenewalDto [insurancePermitRenewals=" + insurancePermitRenewals
				+ ", insurancePermitRenewalJson=" + insurancePermitRenewalJson + ", fleetIprErrMsg=" + fleetIprErrMsg
				+ ", fleetTypeId=" + fleetTypeId + "]";
	}
}
