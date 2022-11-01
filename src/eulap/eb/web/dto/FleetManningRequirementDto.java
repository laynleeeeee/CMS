package eulap.eb.web.dto;

import eulap.eb.domain.hibernate.FleetManningRequirement;

/**
 * Fleet Manning Requirements Dto.

 */
public class FleetManningRequirementDto extends FleetProfileDto {
	private FleetManningRequirement manningRequirement;

	public FleetManningRequirement getManningRequirement() {
		return manningRequirement;
	}

	public void setManningRequirement(FleetManningRequirement manningRequirement) {
		this.manningRequirement = manningRequirement;
	}

	@Override
	public String toString() {
		return "FleetManningRequirementDto [manningRequirement=" + manningRequirement + "]";
	}
}
