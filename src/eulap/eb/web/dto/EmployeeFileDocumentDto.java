package eulap.eb.web.dto;

import java.util.Date;

/**
 * Employee File and Document Dto.

 *
 */
public class EmployeeFileDocumentDto {
	private Integer formTypeId;
	private Integer formId;
	private Date date;
	private String description;
	private Integer sequenceNo;

	public static final int FT_ALL = -1;
	public static final int FT_EMPLOYEE_DOCUMENT = 1;
	public static final int FT_EMPLOYEE_LEAVE_CREDIT = 2;
	public static final int FT_CASH_BOND = 3;
	public static final int FT_AUTH_TO_DEDUCT = 4;
	public static final int FT_REQ_FOR_LEAVE = 5;
	public static final int FT_REQ_FOR_OVERTIME = 6;
	public static final int FT_PAN = 7;

	public static EmployeeFileDocumentDto getInstanceOf (Integer formTypeId, Integer formId, Date date, String description, Integer sequenceNo) {
		EmployeeFileDocumentDto ret = new EmployeeFileDocumentDto();
		ret.formTypeId = formTypeId;
		ret.formId = formId;
		ret.date = date;
		ret.description = description;
		ret.sequenceNo = sequenceNo;
		return ret;
	}

	public Integer getFormTypeId() {
		return formTypeId;
	}

	public void setFormTypeId(Integer formTypeId) {
		this.formTypeId = formTypeId;
	}

	public Integer getFormId() {
		return formId;
	}

	public void setFormId(Integer formId) {
		this.formId = formId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getSequenceNo() {
		return sequenceNo;
	}

	public void setSequenceNo(Integer sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

	@Override
	public String toString() {
		return "EmployeeFileDocumentDto [formTypeId=" + formTypeId + ", formId=" + formId + ", date=" + date
				+ ", description=" + description + ", sequenceNo=" + sequenceNo + "]";
	}
}
