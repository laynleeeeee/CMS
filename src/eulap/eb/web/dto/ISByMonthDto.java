package eulap.eb.web.dto;

import java.util.List;

/**
 * Income Statement By Month DTO.

 * 
 */
public class ISByMonthDto {
	private List<TimePeriodMonth> months;
	private List<ISBSTotalDto> isbsTotalDtos;
	private List<ISBSTypeDto> typeDtos;

	public List<TimePeriodMonth> getMonths() {
		return months;
	}

	public void setMonths(List<TimePeriodMonth> months) {
		this.months = months;
	}

	public List<ISBSTotalDto> getIsbsTotalDtos() {
		return isbsTotalDtos;
	}

	public void setIsbsTotalDtos(List<ISBSTotalDto> isbsTotalDtos) {
		this.isbsTotalDtos = isbsTotalDtos;
	}

	public List<ISBSTypeDto> getTypeDtos() {
		return typeDtos;
	}

	public void setTypeDtos(List<ISBSTypeDto> typeDtos) {
		this.typeDtos = typeDtos;
	}
}
