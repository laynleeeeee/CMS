package eulap.eb.web.dto;

import java.util.List;

/**
 * Data transfer object for fleet attributable cost printout.

 *
 */
public class FleetAttribCostJrDto {
	private String codeVessel;
	private String make;
	private String officialNo;
	private String chassisNo;
	private String callSign;
	private String engineNo;
	private List<FleetJobOrderDto> jobOrderDtos;
	private List<FleetItemConsumedDto> itemConsumedDtos;

	public String getCodeVessel() {
		return codeVessel;
	}

	public void setCodeVessel(String codeVessel) {
		this.codeVessel = codeVessel;
	}

	public String getMake() {
		return make;
	}

	public void setMake(String make) {
		this.make = make;
	}

	public String getOfficialNo() {
		return officialNo;
	}

	public void setOfficialNo(String officialNo) {
		this.officialNo = officialNo;
	}

	public String getChassisNo() {
		return chassisNo;
	}

	public void setChassisNo(String chassisNo) {
		this.chassisNo = chassisNo;
	}

	public String getCallSign() {
		return callSign;
	}

	public void setCallSign(String callSign) {
		this.callSign = callSign;
	}

	public String getEngineNo() {
		return engineNo;
	}

	public void setEngineNo(String engineNo) {
		this.engineNo = engineNo;
	}

	public List<FleetJobOrderDto> getJobOrderDtos() {
		return jobOrderDtos;
	}

	public void setJobOrderDtos(List<FleetJobOrderDto> jobOrderDtos) {
		this.jobOrderDtos = jobOrderDtos;
	}

	public List<FleetItemConsumedDto> getItemConsumedDtos() {
		return itemConsumedDtos;
	}

	public void setItemConsumedDtos(List<FleetItemConsumedDto> itemConsumedDtos) {
		this.itemConsumedDtos = itemConsumedDtos;
	}

	@Override
	public String toString() {
		return "FleetAttribCostJrDto [codeVessel=" + codeVessel + ", make=" + make + ", officialNo=" + officialNo
				+ ", chassisNo=" + chassisNo + ", callSign=" + callSign + ", engineNo=" + engineNo + "]";
	}

}
