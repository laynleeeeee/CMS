package eulap.eb.web.dto;

import java.util.List;

/**
 * Data transfer object class for petty cash replenishment register report

 */

public class PCRRegisterMainDto {
	private Integer pcvrId;
	private List<PCReplenishmentRegisterDto> prcRegisterDtos;

	public Integer getPcvrId() {
		return pcvrId;
	}

	public void setPcvrId(Integer pcvrId) {
		this.pcvrId = pcvrId;
	}

	public List<PCReplenishmentRegisterDto> getPrcRegisterDtos() {
		return prcRegisterDtos;
	}

	public void setPrcRegisterDtos(List<PCReplenishmentRegisterDto> prcRegisterDtos) {
		this.prcRegisterDtos = prcRegisterDtos;
	}
}
