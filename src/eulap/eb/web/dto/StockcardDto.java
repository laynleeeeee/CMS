package eulap.eb.web.dto;

import java.util.Date;

import eulap.common.util.NumberFormatUtil;
/**
 * DTO for stockcard per item report.

 */
public class StockcardDto {
	private Integer formId;
	private String divisionName;
	private Integer refId;
	private Date date;
	private String formName;
	private String formNumber;
	private String bmsNumber;
	private String costCenter;
	private String supplier;
	private Double quantity;
	private Double balanceAmount;
	private Double balance;
	private Double inventoryCost;
	private Double unitCost;
	private String stockcode;
	private String description;
	private String invoiceNumber;
	private String drNumber;
	private Double srp;

	public static String RR_FORM_NAME = "RR";
	public static String RTS_FORM_NAME = "RTS";

	public static StockcardDto getInstance(Date date, String formNumber, String costCenter, String supplier, Double quantity, Double balanceAmount,
			Double balance, Double inventoryCost, String stockcode, Integer quantityOnHand, String divisionName, String bmsNumber) {
		StockcardDto stockcard = new StockcardDto();
		stockcard.date = date;
		stockcard.formNumber = formNumber;
		stockcard.costCenter = costCenter;
		stockcard.supplier = supplier;
		stockcard.quantity = quantity;
		stockcard.balance = balance;
		stockcard.inventoryCost = inventoryCost;
		stockcard.stockcode = stockcode;
		stockcard.balanceAmount = balanceAmount;
		stockcard.divisionName = divisionName;
		stockcard.bmsNumber = bmsNumber;
		return stockcard;
	}

	public Integer getFormId() {
		return formId;
	}

	public void setFormId(Integer formId) {
		this.formId = formId;
	}

	public Integer getRefId() {
		return refId;
	}

	public void setRefId(Integer refId) {
		this.refId = refId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public String getFormNumber() {
		return formNumber;
	}

	public void setFormNumber(String formNumber) {
		this.formNumber = formNumber;
	}

	public String getCostCenter() {
		return costCenter;
	}

	public void setCostCenter(String costCenter) {
		this.costCenter = costCenter;
	}

	public String getSupplier() {
		return supplier;
	}
	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public Double getBalanceAmount() {
		return balanceAmount;
	}

	public void setBalanceAmount(Double balanceAmount) {
		this.balanceAmount = balanceAmount;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public Double getInventoryCost() {
		return inventoryCost;
	}

	public void setInventoryCost(Double inventoryCost) {
		this.inventoryCost = inventoryCost;
	}

	public Double getUnitCost() {
		return unitCost;
	}

	public void setUnitCost(Double unitCost) {
		this.unitCost = unitCost;
	}

	public String getStockcode() {
		return stockcode;
	}

	public void setStockcode(String stockcode) {
		this.stockcode = stockcode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public String getDrNumber() {
		return drNumber;
	}

	public void setDrNumber(String drNumber) {
		this.drNumber = drNumber;
	}

	public Double getSrp() {
		return srp;
	}

	public void setSrp(Double srp) {
		this.srp = srp;
	}

	public String getDivisionName() {
		return divisionName;
	}

	public void setDivisionName(String divisionName) {
		this.divisionName = divisionName;
	}

	public String getBmsNumber() {
		return bmsNumber;
	}

	public void setBmsNumber(String bmsNumber) {
		this.bmsNumber = bmsNumber;
	}

	@Override
	public String toString() {
		return "StockcardDto [formId=" + formId + ", divisionName=" + divisionName + ", refId=" + refId + ", date="
				+ date + ", formName=" + formName + ", formNumber=" + formNumber + ", bmsNumber=" + bmsNumber
				+ ", costCenter=" + costCenter + ", supplier=" + supplier + ", quantity=" + quantity
				+ ", balanceAmount=" + balanceAmount + ", balance=" + balance + ", inventoryCost=" + inventoryCost
				+ ", unitCost=" + unitCost + ", stockcode=" + stockcode + ", description=" + description
				+ ", invoiceNumber=" + invoiceNumber + ", drNumber=" + drNumber + ", srp=" + srp + "]";
	}

	public Double getAmount() {
		inventoryCost = NumberFormatUtil.roundOffNumber(inventoryCost, NumberFormatUtil.SIX_DECIMAL_PLACES);
		return NumberFormatUtil.roundOffTo2DecPlaces(inventoryCost*quantity);
	}
}
