package eulap.eb.web.dto;

import java.util.Date;

import eulap.eb.domain.hibernate.TimePeriod;

/**
 * Data transfer object for {@link TimePeriod}

 */
public class FormDetailsDto {
	private String source;
	private Integer formId;
	private String seqNo;
	private String refNo;
	private Date date;

	public static FormDetailsDto getInstanceOf(String source, Integer formId,
			String seqNo, String refNo, Date date) {
		FormDetailsDto tpDto = new FormDetailsDto();
		tpDto.source = source;
		tpDto.formId = formId;
		tpDto.seqNo = seqNo;
		tpDto.refNo = refNo;
		tpDto.date = date;
		return tpDto;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Integer getFormId() {
		return formId;
	}

	public void setFormId(Integer formId) {
		this.formId = formId;
	}

	public String getRefNo() {
		return refNo;
	}

	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(String seqNo) {
		this.seqNo = seqNo;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TimePeriodDto [source=").append(source)
				.append(", formId=").append(formId).append(", refNo=")
				.append(refNo).append(", seqNo=").append(seqNo)
				.append(", date=").append(date).append("]");
		return builder.toString();
	}
}
