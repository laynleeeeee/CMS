package eulap.eb.domain.hibernate;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import eulap.eb.service.oo.OOChild;

/**
 * A class that represents the PETTY_CASH_VOUCHER_LIQUIDATION table.

 *
 */
@Entity
@Table(name="PETTY_CASH_VOUCHER_LIQUIDATION")
public class PettyCashVoucherLiquidation extends BaseFormWorkflow{
	private Integer sequenceNo;
	private Integer companyId;
	private Integer divisionId;
	private Integer pettyCashVoucherId;
	private Integer userCustodianId;
	private Date pcvlDate;
	private String requestor;
	private String referenceNo;
	private String description;
	private Double amount;
	private Double cashReturned;
	private Company company;
	private Division division;
	private PettyCashVoucher pettyCashVoucer;
	private UserCustodian userCustodian;
	private List<PettyCashVoucherLiquidationLine> pcvlLines;
	private String pcvlLinesJson;
	private String pcvlErrorMessage;
	private List<ReferenceDocument> referenceDocuments;
	private String referenceDocumentsJson;
	private String referenceDocsMessage;
	private Integer pcvNumber;
	private Date pcvDate;
	private String userCustodianName;
	private Double grandTotal;
	private String pcvDateString;

	public enum FIELD {
		id, formWorkflowId, sequenceNo, companyId, divisionId, pettyCashVoucherId, userCustodianId, pcvlDate, requestor, referenceNo, description, amount, cashReturned
	}

	public static final int PCVL_OBJECT_TYPE_ID = 24013;

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "PETTY_CASH_VOUCHER_LIQUIDATION_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Override
	@Column(name = "CREATED_BY")
	public int getCreatedBy() {
		return super.getCreatedBy();
	}

	@Override
	@Column(name = "CREATED_DATE")
	public Date getCreatedDate() {
		return super.getCreatedDate();
	}

	@Override
	@Column(name = "UPDATED_BY")
	public int getUpdatedBy() {
		return super.getUpdatedBy();
	}

	@Override
	@Column(name = "UPDATED_DATE")
	public Date getUpdatedDate() {
		return super.getUpdatedDate();
	}

	@Column(name = "COMPANY_ID")
	public Integer getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	@OneToOne
	@JoinColumn(name="COMPANY_ID", insertable=false, updatable=false)
	public Company getCompany() {
		return company;
	}
	public void setCompany(Company company) {
		this.company = company;
	}

	@Column(name = "PETTY_CASH_VOUCHER_ID")
	public Integer getPettyCashVoucherId() {
		return pettyCashVoucherId;
	}
	public void setPettyCashVoucherId(Integer pettyCashVoucherId) {
		this.pettyCashVoucherId = pettyCashVoucherId;
	}

	@OneToOne
	@JoinColumn(name="PETTY_CASH_VOUCHER_ID", insertable=false, updatable=false)
	public PettyCashVoucher getPettyCashVoucer() {
		return pettyCashVoucer;
	}
	public void setPettyCashVoucer(PettyCashVoucher pettyCashVoucer) {
		this.pettyCashVoucer = pettyCashVoucer;
	}

	@Column(name = "DIVISION_ID")
	public Integer getDivisionId() {
		return divisionId;
	}
	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	@OneToOne
	@JoinColumn(name="DIVISION_ID", insertable=false, updatable=false)
	public Division getDivision() {
		return division;
	}
	public void setDivision(Division division) {
		this.division = division;
	}

	@Column(name = "USER_CUSTODIAN_ID")
	public Integer getUserCustodianId() {
		return userCustodianId;
	}
	public void setUserCustodianId(Integer userCustodianId) {
		this.userCustodianId = userCustodianId;
	}

	@OneToOne
	@JoinColumn(name="USER_CUSTODIAN_ID", insertable=false, updatable=false)
	public UserCustodian getUserCustodian() {
		return userCustodian;
	}
	public void setUserCustodian(UserCustodian userCustodian) {
		this.userCustodian = userCustodian;
	}

	@Column(name = "PCVL_DATE")
	public Date getPcvlDate() {
		return pcvlDate;
	}
	public void setPcvlDate(Date pcvlDate) {
		this.pcvlDate = pcvlDate;
	}

	@Column(name = "REQUESTOR")
	public String getRequestor() {
		return requestor;
	}
	public void setRequestor(String requestor) {
		this.requestor = requestor;
	}

	@Column(name = "REFERENCE_NO")
	public String getReferenceNo() {
		return referenceNo;
	}
	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}

	@Column(name = "SEQUENCE_NO")
	public Integer getSequenceNo() {
		return sequenceNo;
	}
	public void setSequenceNo(Integer sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

	@Column(name = "DESCRIPTION")
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "AMOUNT")
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}

	@Column(name = "CASH_RETURNED")
	public Double getCashReturned() {
		return cashReturned;
	}
	public void setCashReturned(Double cashReturned) {
		this.cashReturned = cashReturned;
	}

	@Override
	@Transient
	public List<OOChild> getChildren() {
		List<OOChild> children = new ArrayList<OOChild>();
		if (pcvlLines != null) {
			children.addAll(pcvlLines);
		}
		if (referenceDocuments != null) {
			children.addAll(referenceDocuments);
		}
		return children;
	}

	@Transient
	public List<PettyCashVoucherLiquidationLine> getPcvlLines() {
		return pcvlLines;
	}
	public void setPcvlLines(List<PettyCashVoucherLiquidationLine> pcvlLines) {
		this.pcvlLines = pcvlLines;
	}

	@Transient
	public String getPcvlLinesJson() {
		return pcvlLinesJson;
	}
	public void setPcvlLinesJson(String pcvlLinesJson) {
		this.pcvlLinesJson = pcvlLinesJson;
	}

	@Transient
	public void serializePcvlLines (){
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		pcvlLinesJson = gson.toJson(pcvlLines);
	}
	@Transient
	public void deserializePcvlLines () {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat("MM/dd/yyyy").create();
		Type type = new TypeToken <List<PettyCashVoucherLiquidationLine>>(){}.getType();
		pcvlLines = gson.fromJson(pcvlLinesJson, type);
	}

	@Transient
	public List<ReferenceDocument> getReferenceDocuments() {
		return referenceDocuments;
	}
	public void setReferenceDocuments(List<ReferenceDocument> referenceDocuments) {
		this.referenceDocuments = referenceDocuments;
	}

	@Transient
	public String getReferenceDocumentsJson() {
		return referenceDocumentsJson;
	}
	public void setReferenceDocumentsJson(String referenceDocumentsJson) {
		this.referenceDocumentsJson = referenceDocumentsJson;
	}

	@Transient
	public void serializeReferenceDocuments (){
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		referenceDocumentsJson = gson.toJson(referenceDocuments);
	}
	@Transient
	public void deserializeReferenceDocuments () {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<ReferenceDocument>>(){}.getType();
		referenceDocuments = gson.fromJson(referenceDocumentsJson, type);
	}

	@Transient
	public String getReferenceDocsMessage() {
		return referenceDocsMessage;
	}
	public void setReferenceDocsMessage(String referenceDocsMessage) {
		this.referenceDocsMessage = referenceDocsMessage;
	}

	@Transient
	public String getPcvlErrorMessage() {
		return pcvlErrorMessage;
	}
	public void setPcvlErrorMessage(String pcvlErrorMessage) {
		this.pcvlErrorMessage = pcvlErrorMessage;
	}

	@Override
	@Transient
	public String getWorkflowName() {
		return super.getWorkflowName()+divisionId;
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return PCVL_OBJECT_TYPE_ID;
	}

	@Override
	public String toString() {
		return "PettyCashVoucherLiquidation [sequenceNo=" + sequenceNo + ", companyId=" + companyId + ", divisionId="
				+ divisionId + ", pettyCashVoucherId=" + pettyCashVoucherId + ", userCustodianId=" + userCustodianId
				+ ", pcvlDate=" + pcvlDate + ", requestor=" + requestor + ", referenceNo=" + referenceNo
				+ ", description=" + description + ", amount=" + amount + ", cashReturned=" + cashReturned + "]";
	}

	@Transient
	public Integer getPcvNumber() {
		return pcvNumber;
	}
	public void setPcvNumber(Integer pcvNumber) {
		this.pcvNumber = pcvNumber;
	}

	@Transient
	public Date getPcvDate() {
		return pcvDate;
	}
	public void setPcvDate(Date pcvDate) {
		this.pcvDate = pcvDate;
	}

	@Transient
	public String getUserCustodianName() {
		return userCustodianName;
	}
	public void setUserCustodianName(String userCustodianName) {
		this.userCustodianName = userCustodianName;
	}

	@Override
	@Transient
	public Date getGLDate() {
		return pcvDate;
	}

	@Transient
	public Double getGrandTotal() {
		return grandTotal;
	}
	public void setGrandTotal(Double grandTotal) {
		this.grandTotal = grandTotal;
	}

	@Transient
	public String getPcvDateString() {
		return pcvDateString;
	}
	public void setPcvDateString(String pcvDateString) {
		this.pcvDateString = pcvDateString;
	}
}
