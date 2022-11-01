package eulap.eb.web.dto;

import java.util.Date;

/**
 * The sales report dto.

 */
public class SalesReportDto {
	private Integer soId;
	private Date soDate;
	private Integer soNumber;
	private String poNumber;
	private Double soAmount;
	private String term;
	private String customerAcct;
	private String customer;
	private String salesPersonnel;
	private String drNumber;
	private String drRefNo;
	private String drDate;
	private String drDateReceived;
	private String drReceiver;
	private String drStatus;
	private String ariDate;
	private String ariDateReceived;
	private String ariReceiver;
	private String ariNumber;
	private Double netSales;
	private Double vat;
	private Double grossSales;
	private Double wtAmount;
	private String currency;
	private String arReceiptDate;
	private String arReceiptNo;
	private Double arReceiptAmt;
	private Double balance;

	public SalesReportDto() {}

	public SalesReportDto(Integer soId, Date soDate, Integer soNumber, String poNumber, Double soAmount, 
			String term, String customerAcct, String customer, String salesPersonnel, String drNumber, String drRefNo,
			String drDate, String drDateReceived, String drReceiver, String drStatus, String ariDate, 
			String ariDateReceived, String ariReceiver, String ariNumber, Double netSales, Double vat,
			Double grossSales, Double wtAmount, String currency, String arReceiptNo, String arReceiptDate, 
			Double arReceiptAmt, Double balance) {
		this.soId = soId;
		this.soDate = soDate;
		this.soNumber = soNumber;
		this.poNumber = poNumber;
		this.soAmount = soAmount;
		this.term = term;
		this.customerAcct = customerAcct;
		this.customer = customer;
		this.salesPersonnel = salesPersonnel;
		this.drNumber = drNumber;
		this.drRefNo = drRefNo;
		this.drDate = drDate;
		this.drDateReceived = drDateReceived;
		this.drReceiver = drReceiver;
		this.drStatus = drStatus;
		this.ariDate = ariDate;
		this.ariDateReceived = ariDateReceived;
		this.ariReceiver = ariReceiver;
		this.ariNumber = ariNumber;
		this.netSales = netSales;
		this.vat = vat;
		this.grossSales = grossSales;
		this.wtAmount = wtAmount;
		this.currency = currency;
		this.arReceiptDate = arReceiptDate;
		this.arReceiptNo = arReceiptNo;
		this.arReceiptAmt = arReceiptAmt;
		this.balance = balance;
	}

	public Integer getSoId() {
		return soId;
	}

	public void setSoId(Integer soId) {
		this.soId = soId;
	}

	public Date getSoDate() {
		return soDate;
	}

	public void setSoDate(Date soDate) {
		this.soDate = soDate;
	}

	public Integer getSoNumber() {
		return soNumber;
	}

	public void setSoNumber(Integer soNumber) {
		this.soNumber = soNumber;
	}

	public String getPoNumber() {
		return poNumber;
	}

	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}

	public Double getSoAmount() {
		return soAmount;
	}

	public void setSoAmount(Double soAmount) {
		this.soAmount = soAmount;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public String getCustomerAcct() {
		return customerAcct;
	}

	public void setCustomerAcct(String customerAcct) {
		this.customerAcct = customerAcct;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getSalesPersonnel() {
		return salesPersonnel;
	}

	public void setSalesPersonnel(String salesPersonnel) {
		this.salesPersonnel = salesPersonnel;
	}

	public String getDrNumber() {
		return drNumber;
	}

	public void setDrNumber(String drNumber) {
		this.drNumber = drNumber;
	}

	public String getDrRefNo() {
		return drRefNo;
	}

	public void setDrRefNo(String drRefNo) {
		this.drRefNo = drRefNo;
	}

	public String getDrDate() {
		return drDate;
	}

	public void setDrDate(String drDate) {
		this.drDate = drDate;
	}

	public String getDrDateReceived() {
		return drDateReceived;
	}

	public void setDrDateReceived(String drDateReceived) {
		this.drDateReceived = drDateReceived;
	}

	public String getDrReceiver() {
		return drReceiver;
	}

	public void setDrReceiver(String drReceiver) {
		this.drReceiver = drReceiver;
	}

	public String getDrStatus() {
		return drStatus;
	}

	public void setDrStatus(String drStatus) {
		this.drStatus = drStatus;
	}

	public String getAriDate() {
		return ariDate;
	}

	public void setAriDate(String ariDate) {
		this.ariDate = ariDate;
	}

	public String getAriDateReceived() {
		return ariDateReceived;
	}

	public void setAriDateReceived(String ariDateReceived) {
		this.ariDateReceived = ariDateReceived;
	}

	public String getAriReceiver() {
		return ariReceiver;
	}

	public void setAriReceiver(String ariReceiver) {
		this.ariReceiver = ariReceiver;
	}

	public String getAriNumber() {
		return ariNumber;
	}

	public void setAriNumber(String ariNumber) {
		this.ariNumber = ariNumber;
	}

	public Double getNetSales() {
		return netSales;
	}

	public void setNetSales(Double netSales) {
		this.netSales = netSales;
	}

	public Double getVat() {
		return vat;
	}

	public void setVat(Double vat) {
		this.vat = vat;
	}

	public Double getGrossSales() {
		return grossSales;
	}

	public void setGrossSales(Double grossSales) {
		this.grossSales = grossSales;
	}

	public Double getWtAmount() {
		return wtAmount;
	}

	public void setWtAmount(Double wtAmount) {
		this.wtAmount = wtAmount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getArReceiptNo() {
		return arReceiptNo;
	}

	public void setArReceiptNo(String arReceiptNo) {
		this.arReceiptNo = arReceiptNo;
	}

	public Double getArReceiptAmt() {
		return arReceiptAmt;
	}

	public void setArReceiptAmt(Double arReceiptAmt) {
		this.arReceiptAmt = arReceiptAmt;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public String getArReceiptDate() {
		return arReceiptDate;
	}

	public void setArReceiptDate(String arReceiptDate) {
		this.arReceiptDate = arReceiptDate;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SalesReportDto [soId=").append(soId).append(", soDate=").append(soDate).append(", soNumber=")
				.append(soNumber).append(", poNumber=").append(poNumber).append(", soAmount=").append(soAmount)
				.append(", term=").append(term).append(", customerAcct=").append(customerAcct).append(", customer=")
				.append(customer).append(", salesPersonnel=").append(salesPersonnel).append(", drNumber=")
				.append(drNumber).append(", drRefNo=").append(drRefNo).append(", drDate=").append(drDate)
				.append(", drDateReceived=").append(drDateReceived).append(", drReceiver=").append(drReceiver)
				.append(", drStatus=").append(drStatus).append(", ariDate=").append(ariDate)
				.append(", ariDateReceived=").append(ariDateReceived).append(", ariReceiver=").append(ariReceiver)
				.append(", ariNumber=").append(ariNumber).append(", netSales=").append(netSales).append(", vat=")
				.append(vat).append(", grossSales=").append(grossSales).append(", wtAmount=").append(wtAmount)
				.append(", currency=").append(currency).append(", arReceiptNo=").append(arReceiptNo)
				.append(", arReceiptAmt=").append(arReceiptAmt).append(", balance=").append(balance).append("]");
		return builder.toString();
	}
}
