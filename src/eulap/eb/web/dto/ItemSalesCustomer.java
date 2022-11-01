package eulap.eb.web.dto;

import java.util.Date;

/**
 * Item Sales Customer report Dto.

 *
 */
public class ItemSalesCustomer {
	private Date date;
	private String stockCode;
	private String description;
	private String refNo;
	private Double qty;
	private String uom;
	private Double srp;
	private Double amount;
	private Double discount;
	private Double netAmount;
	private Double volumeConversion;
	private Integer itemId;
	private String divisionName;
	private String bmsNumber;


	public String getBmsNumber() {
		return bmsNumber;
	}

	public void setBmsNumber(String bmsNumber) {
		this.bmsNumber = bmsNumber;
	}

	
	public String getDivisionName() {
		return divisionName;
	}

	public void setDivisionName(String divisionName) {
		this.divisionName = divisionName;
	}

	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	public String getStockCode() {
		return stockCode;
	}
	
	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getRefNo() {
		return refNo;
	}
	
	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}
	
	public Double getQty() {
		return qty;
	}
	
	public void setQty(Double qty) {
		this.qty = qty;
	}
	
	public String getUom() {
		return uom;
	}
	
	public void setUom(String uom) {
		this.uom = uom;
	}
	
	public Double getSrp() {
		return srp;
	}
	
	public void setSrp(Double srp) {
		this.srp = srp;
	}
	
	public Double getAmount() {
		return amount;
	}
	
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	
	public Double getDiscount() {
		return discount;
	}
	
	public void setDiscount(Double discount) {
		this.discount = discount;
	}
	
	public Double getNetAmount() {
		return netAmount;
	}
	
	public void setNetAmount(Double netAmount) {
		this.netAmount = netAmount;
	}

	@Override
	public String toString() {
		return "ItemSalesCustomer [divisionName="+ divisionName + ", date=" + date + ", stockCode=" + stockCode
				+ ", description=" + description + ", bmsNumber="+ bmsNumber + ", refNo=" + refNo
				+ ", qty=" + qty + ", uom=" + uom + ", srp=" + srp
				+ ", amount=" + amount + ", discount=" + discount
				+ ", netAmount=" + netAmount + "]";
	}

	public Double getVolumeConversion() {
		return volumeConversion;
	}

	public void setVolumeConversion(Double volumeConversion) {
		this.volumeConversion = volumeConversion;
	}

	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

}
