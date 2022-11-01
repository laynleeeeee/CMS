package eulap.eb.web.dto;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import javax.persistence.Transient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import eulap.eb.domain.hibernate.CashSaleArLine;
import eulap.eb.domain.hibernate.CashSaleReturnItem;

/**
 * Container class that will hold the values for Cash Sales Return.

 * 
 */
public class CashSaleReturnDto {
	private Integer refId;
	private Integer companyId;
	private Integer arCustomerId;
	private Integer arCustomerAcctId;
	private String saleInvoice;
	private Date date;
	private String seqNo;
	private String customerName;
	private String customerAcctName;
	private String reference;
	private Integer cashSaleTypeId;
	private List<CashSaleReturnItem> saleItems;
	private List<CashSaleArLine> arLines;
	private String cashSaleReturnItemsJson;

	public static CashSaleReturnDto getInstanceOf(List<CashSaleReturnItem> cashSaleItems) {
		CashSaleReturnDto cashSaleReturnDto = new CashSaleReturnDto();
		cashSaleReturnDto.setSaleItems(cashSaleItems);
		return cashSaleReturnDto;
	}

	public List<CashSaleReturnItem> getSaleItems() {
		return saleItems;
	}

	public void setSaleItems(List<CashSaleReturnItem> saleItems) {
		this.saleItems = saleItems;
	}

	public List<CashSaleArLine> getArLines() {
		return arLines;
	}

	public void setArLines(List<CashSaleArLine> arLines) {
		this.arLines = arLines;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public Integer getArCustomerId() {
		return arCustomerId;
	}

	public void setArCustomerId(Integer arCustomerId) {
		this.arCustomerId = arCustomerId;
	}

	public Integer getArCustomerAcctId() {
		return arCustomerAcctId;
	}

	public void setArCustomerAcctId(Integer arCustomerAcctId) {
		this.arCustomerAcctId = arCustomerAcctId;
	}

	public String getSaleInvoice() {
		return saleInvoice;
	}

	public void setSaleInvoice(String saleInvoice) {
		this.saleInvoice = saleInvoice;
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

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerAcctName() {
		return customerAcctName;
	}

	public void setCustomerAcctName(String customerAcctName) {
		this.customerAcctName = customerAcctName;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public Integer getRefId() {
		return refId;
	}

	public void setRefId(Integer refId) {
		this.refId = refId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CashSaleReturnDto [refId=").append(refId).append(", companyId=").append(companyId)
				.append(", arCustomerId=").append(arCustomerId).append(", arCustomerAcctId=").append(arCustomerAcctId)
				.append(", saleInvoice=").append(saleInvoice).append(", date=").append(date).append(", seqNo=")
				.append(seqNo).append(", customerName=").append(customerName).append(", customerAcctName=")
				.append(customerAcctName).append(", reference=").append(reference).append(", saleItems=")
				.append(saleItems).append(", arLines=").append(arLines).append("]");
		return builder.toString();
	}

	public Integer getCashSaleTypeId() {
		return cashSaleTypeId;
	}

	public void setCashSaleTypeId(Integer cashSaleTypeId) {
		this.cashSaleTypeId = cashSaleTypeId;
	}

	@Transient
	public String getCashSaleReturnItemsJson() {
		return cashSaleReturnItemsJson;
	}

	public void setCashSaleReturnItemsJson(String cashSaleReturnItemsJson) {
		this.cashSaleReturnItemsJson = cashSaleReturnItemsJson;
	}

	@Transient
	public void serializeItems() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
				.create();
		cashSaleReturnItemsJson = gson.toJson(saleItems);
	}

	@Transient
	public void deserializeItems() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
				.create();
		Type type = new TypeToken<List<CashSaleReturnItem>>() {
		}.getType();
		saleItems = gson.fromJson(cashSaleReturnItemsJson, type);
	}
}
