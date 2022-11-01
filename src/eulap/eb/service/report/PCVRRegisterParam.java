package eulap.eb.service.report;

import java.util.Date;

/**
 * A class that handles the report parameters for petty cash replenishment report

 */

public class PCVRRegisterParam {
	private Integer companyId;
	private Integer divisionId;
	private Integer custodianId;
	private Integer pcrNo;
	private Date dateFrom;
	private Date dateTo;
	private Integer status;

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	public Integer getCustodianId() {
		return custodianId;
	}

	public void setCustodianId(Integer custodianId) {
		this.custodianId = custodianId;
	}

	public Integer getPcrNo() {
		return pcrNo;
	}

	public void setPcrNo(Integer pcrNo) {
		this.pcrNo = pcrNo;
	}

	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PCVRRegisterParam [companyId=").append(companyId).append(", divisionId=")
				.append(divisionId).append(", custodianId=").append(custodianId).append(", pcrNo=")
				.append(pcrNo).append(", dateFrom=").append(dateFrom).append(", dateTo=").append(dateTo)
				.append(", status=").append(status).append("]");
		return builder.toString();
	}
}
