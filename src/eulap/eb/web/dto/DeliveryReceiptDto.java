package eulap.eb.web.dto;

import java.util.Date;

import eulap.eb.domain.hibernate.DeliveryReceipt;

/**
 * Data transfer object for {@link DeliveryReceipt}

 *
 */
public class DeliveryReceiptDto {
	private Integer drRefObjId;
	private Date date;
	private Integer companyId;
	private String compName;
	private Integer sequenceNo;
	private Integer arCustomerId;
	private String custName;
	private Integer arCustomerAccountId;
	private String custAcctName;
	private Integer termId;
	private String termName;

	public static final int STATUS_ALL = 1;
	public static final int STATUS_UNSERVED = 2;
	public static final int STATUS_USED = 3;

	/**
	 * Create an instance of {@link DeliveryReceiptDto}
	 * @param drRefObjId The delivery receipt reference object id.
	 * @param companyId The company id.
	 * @param companyName The company name.
	 * @param sequenceNo The sequence number or delivery receipt.
	 * @param arCustomerId The customer id.
	 * @param custName The customer name.
	 * @param arCustomerAccountId The customer account id.
	 * @param custAcctName The customer account name.
	 * @param termId The term id.
	 * @param termName The term name.
	 * @return The {@link DeliveryReceiptDto} instance.
	 */
	public static DeliveryReceiptDto getInstanceOf (Integer drRefObjId, Date date, Integer companyId, 
			String compName, Integer sequenceNo, Integer arCustomerId, String custName, Integer arCustomerAccountId, 
			String custAcctName, Integer termId, String termName) {
		DeliveryReceiptDto dr = new DeliveryReceiptDto();
		dr.drRefObjId = drRefObjId;
		dr.date = date;
		dr.companyId = companyId;
		dr.compName = compName;
		dr.sequenceNo = sequenceNo;
		dr.arCustomerId = arCustomerId;
		dr.custName = custName;
		dr.arCustomerAccountId = arCustomerAccountId;
		dr.custAcctName = custAcctName;
		dr.termId = termId;
		dr.termName = termName;
		return dr;
	}

	public Integer getDrRefObjId() {
		return drRefObjId;
	}

	public void setDrRefObjId(Integer drRefObjId) {
		this.drRefObjId = drRefObjId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public String getCompName() {
		return compName;
	}

	public void setCompName(String compName) {
		this.compName = compName;
	}

	public Integer getSequenceNo() {
		return sequenceNo;
	}

	public void setSequenceNo(Integer sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

	public Integer getArCustomerId() {
		return arCustomerId;
	}

	public void setArCustomerId(Integer arCustomerId) {
		this.arCustomerId = arCustomerId;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public Integer getArCustomerAccountId() {
		return arCustomerAccountId;
	}

	public void setArCustomerAccountId(Integer arCustomerAccountId) {
		this.arCustomerAccountId = arCustomerAccountId;
	}

	public String getCustAcctName() {
		return custAcctName;
	}

	public void setCustAcctName(String custAcctName) {
		this.custAcctName = custAcctName;
	}

	public Integer getTermId() {
		return termId;
	}

	public void setTermId(Integer termId) {
		this.termId = termId;
	}

	public String getTermName() {
		return termName;
	}

	public void setTermName(String termName) {
		this.termName = termName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DeliveryReceiptDto [drRefObjId=").append(drRefObjId).append(", date=").append(date)
				.append(", companyId=").append(companyId).append(", compName=").append(compName).append(", sequenceNo=")
				.append(sequenceNo).append(", arCustomerId=").append(arCustomerId).append(", custName=")
				.append(custName).append(", arCustomerAccountId=").append(arCustomerAccountId).append(", custAcctName=")
				.append(custAcctName).append(", termId=").append(termId).append(", termName=").append(termName)
				.append("]");
		return builder.toString();
	}
}
