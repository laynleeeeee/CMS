package eulap.eb.web.dto;

import java.util.Date;

/**
 * Data transfer object class for petty cash replenishment register report

 */

public class PCReplenishmentRegisterDto {
	private String divisionName;
	private Date pcrDate;
	private Integer pcrNo;
	private String custodianName;
	private Date pcvlDate;
	private Integer pcvlNo;
	private String bmsNo;
	private String orSi;
	private String requestor;
	private String supplier;
	private String tinNo;
	private String street;
	private String cityProvince;
	private String description;
	private String account;
	private double grossAmount;
	private String status;
	private Integer sequenceNumber;
	private Integer pvcrId;
	private String remarks;

	public String getOrSi() {
		return orSi;
	}

	public void setOrSi(String orSi) {
		this.orSi = orSi;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCityProvince() {
		return cityProvince;
	}

	public void setCityProvince(String cityProvince) {
		this.cityProvince = cityProvince;
	}

	public Date getPcrDate() {
		return pcrDate;
	}

	public void setPcrDate(Date pcrDate) {
		this.pcrDate = pcrDate;
	}

	public Date getPcvlDate() {
		return pcvlDate;
	}

	public void setPcvlDate(Date pcvlDate) {
		this.pcvlDate = pcvlDate;
	}

	public Integer getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(Integer sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public String getDivisionName() {
		return divisionName;
	}

	public void setDivisionName(String divisionName) {
		this.divisionName = divisionName;
	}

	public Integer getPcrNo() {
		return pcrNo;
	}

	public void setPcrNo(Integer pcrNo) {
		this.pcrNo = pcrNo;
	}

	public String getCustodianName() {
		return custodianName;
	}

	public void setCustodianName(String custodianName) {
		this.custodianName = custodianName;
	}

	public Integer getPcvlNo() {
		return pcvlNo;
	}

	public void setPcvlNo(Integer pcvlNo) {
		this.pcvlNo = pcvlNo;
	}

	public String getRequestor() {
		return requestor;
	}

	public void setRequestor(String requestor) {
		this.requestor = requestor;
	}

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public String getTinNo() {
		return tinNo;
	}

	public void setTinNo(String tinNo) {
		this.tinNo = tinNo;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public double getGrossAmount() {
		return grossAmount;
	}

	public void setGrossAmount(double grossAmount) {
		this.grossAmount = grossAmount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;

	}

	public String getBmsNo() {
		return bmsNo;
	}

	public void setBmsNo(String bmsNo) {
		this.bmsNo = bmsNo;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PettyCashVoucherLiquidationRegisterDTO [divisionName=").append(divisionName).append(", pcrDate=")
			.append(pcrDate).append(", pcrNo=").append(pcrNo)
			.append(", custodianName=").append(custodianName).append(", pcvlDate=").append(pcvlDate)
			.append(", pcvlNo=").append(pcvlNo).append(", bmsNo=").append(bmsNo).append(", orSi=")
			.append(orSi).append(", requestor=").append(requestor).append(", supplier=").append(supplier)
			.append(", tinNo=").append(tinNo).append(", street=").append(street).append(", cityProvince=").append(cityProvince)
			.append(", description=").append(description).append(", account=").append(account).append(", grossAmount=").append(grossAmount)
			.append(", status=").append(status).append("]");
		return builder.toString();
	}

	public Integer getPvcrId() {
		return pvcrId;
	}

	public void setPvcrId(Integer pvcrId) {
		this.pvcrId = pvcrId;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
}