package eulap.eb.web.dto;

import java.util.Date;

/**
 * Container class for holding the values needed to generate the Petty
 * Cash Voucher Register Report.

 */
public class PettyCashVoucherDto {
	private String division;
	private Integer custodianid;
	private Date pcvDate;
	private String pcvNo;
	private String custodianAcctName;
	private String requestor;
	private String reference;
	private String description;
	private double rqstdAmount;
	private String transStatus;
	private String cancellationRemarks;

	public static PettyCashVoucherDto getInstanceOf(String division, Date pcvDate, String pcvNo,
			String custodianAcctName, String requestor, String reference, String description, double rqstdAmount,
			String transStatus, String cancellationRemarks) {
		PettyCashVoucherDto pcvRegisterDto = new PettyCashVoucherDto();
		pcvRegisterDto.division = division;
		pcvRegisterDto.pcvDate = pcvDate;
		pcvRegisterDto.pcvNo = pcvNo;
		pcvRegisterDto.custodianAcctName = custodianAcctName;
		pcvRegisterDto.requestor = requestor;
		pcvRegisterDto.reference = reference;
		pcvRegisterDto.description = description;
		pcvRegisterDto.rqstdAmount = rqstdAmount;
		pcvRegisterDto.transStatus = transStatus;
		pcvRegisterDto.cancellationRemarks = cancellationRemarks;
		return pcvRegisterDto;
	}


	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public Integer getCustodianid() {
		return custodianid;
	}

	public void setCustodianid(Integer custodianid) {
		this.custodianid = custodianid;
	}

	public Date getPcvDate() {
		return pcvDate;
	}

	public void setPcvDate(Date pcvDate) {
		this.pcvDate = pcvDate;
	}

	public String getPcvNo() {
		return pcvNo;
	}


	public void setPcvNo(String pcvNo) {
		this.pcvNo = pcvNo;
	}

	public String getCustodianAcctName() {
		return custodianAcctName;
	}

	public void setCustodianAcctName(String custodianAcctName) {
		this.custodianAcctName = custodianAcctName;
	}

	public String getRequestor() {
		return requestor;
	}

	public void setRequestor(String requestor) {
		this.requestor = requestor;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getRqstdAmount() {
		return rqstdAmount;
	}

	public void setRqstdAmount(double rqstdAmount) {
		this.rqstdAmount = rqstdAmount;
	}

	public String getTransStatus() {
		return transStatus;
	}

	public void setTransStatus(String transStatus) {
		this.transStatus = transStatus;
	}

	public String getCancellationRemarks() {
		return cancellationRemarks;
	}

	public void setCancellationRemarks(String cancellationRemarks) {
		this.cancellationRemarks = cancellationRemarks;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PettyCashVoucherDto [division=").append(division)
				.append(", custodianid=").append(custodianid).append(", pcvDate=").append(pcvDate).append(", pcvNo=")
				.append(pcvNo).append(", custodianAcctName=").append(custodianAcctName).append(", requestor=")
				.append(requestor).append(", reference=").append(reference).append(", description=").append(description)
				.append(", rqstdAmount=").append(rqstdAmount).append(", transStatus=").append(transStatus)
				.append(", cancellationRemarks=").append(cancellationRemarks).append("]");
		return builder.toString();
	}

}
