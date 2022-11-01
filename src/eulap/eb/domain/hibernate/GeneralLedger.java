package eulap.eb.domain.hibernate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;

import eulap.eb.service.oo.OOChild;

/**
 * A class that represents the GENERAL_LEDGER table.

 *
 */
@Entity
@Table(name = "GENERAL_LEDGER")
public class GeneralLedger extends BaseFormWorkflow {
	private int sequenceNo;
	private int glStatusId;
	private int glEntrySourceId;
	private Integer companyId;
	private Integer divisionId;
	private Date glDate;
	private String comment;
	private GlEntrySource glEntrySource;
	private Collection<GlEntry> glEntries;
	private User preparedBy;
	private User checkedBy;
	private User approvedBy;
	private Date checkedDate;
	private Date approvedDate;
	private Position preparedPosition;
	private Position approvedPosition;
	private Company company;
	private Division division;
	private List<ReferenceDocument> referenceDocuments;
	private Integer currencyId;
	private Integer currencyRateId;
	private Double currencyRateValue;
	private Currency currency;
	private CurrencyRate currencyRate;

	/**
	 * Object type Id for General Ledger = 130.
	 */
	public static final int GL_OBJECT_TYPE_ID = 130;

	public enum FIELD {id, sequenceNo, divisionId, glStatusId, ebObjectId, glEntrySourceId, glDate, comment, formWorkflowId, createdBy, updatedBy}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "GENERAL_LEDGER_ID", unique = true, nullable = false, insertable = false, updatable = false)
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

	/**
	 * Get the sequence no.
	 * @return The sequence no.
	 */
	@Column (name = "SEQUENCE_NO")
	public int getSequenceNo() {
		return sequenceNo;
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return GL_OBJECT_TYPE_ID;
	}
	/**
	 * Set the sequence no.
	 * @param sequenceNo The sequence no.
	 */
	public void setSequenceNo(int sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

	/**
	 * Get the gl status id.
	 * @return The gl status id.
	 */
	@Column (name = "GL_STATUS_ID")
	public int getGlStatusId() {
		return glStatusId;
	}

	/**
	 * Set the gl status id.
	 * @param glStatusId The gl status id.
	 */
	public void setGlStatusId(int glStatusId) {
		this.glStatusId = glStatusId;
	}

	/**
	 * Get the gl entry source id.
	 * @return The gl entry source id.
	 */
	@Column (name = "GL_ENTRY_SOURCE_ID")
	public int getGlEntrySourceId() {
		return glEntrySourceId;
	}

	/**
	 * Set the gl entry source id.
	 * @param glEntrySourceId The gl entry source id.
	 */
	public void setGlEntrySourceId(int glEntrySourceId) {
		this.glEntrySourceId = glEntrySourceId;
	}

	/**
	 * Get the company Id
	 * @return companyId
	 */
	@Column(name = "COMPANY_ID", columnDefinition="int(10)")
	public Integer getCompanyId() {
		return companyId;
	}

	/**
	 * Set the company Id
	 * @param companyId
	 */
	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	/**
	 * Get the divisionId
	 * @return divisionId
	 */
	@Column (name = "DIVISION_ID", columnDefinition="int(10)")
	public Integer getDivisionId() {
		return divisionId;
	}

	/**
	 * Set the division Id
	 * @param divisionId
	 */
	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	/**
	 * Get the gl date.
	 * @return The gl date.
	 */
	@Column (name = "GL_DATE")
	public Date getGlDate() {
		return glDate;
	}

	/**
	 * Set the gl date.
	 * @param glDate The gl date.
	 */
	public void setGlDate(Date glDate) {
		this.glDate = glDate;
	}

	/**
	 * Get the comment.
	 * @return
	 */
	@Column (name = "COMMENT")
	public String getComment() {
		return comment;
	}

	/**
	 * Set the comment.
	 * @param comment
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	/**
	 * Get the entry source object.
	 * @return The entry source object.
	 */
	@ManyToOne
	@JoinColumn (name = "GL_ENTRY_SOURCE_ID", insertable=false, updatable=false)
	public GlEntrySource getGlEntrySource() {
		return glEntrySource;
	}

	/**
	 * Set the entry source object.
	 * @param glEntrySource The entry source object.
	 */
	public void setGlEntrySource(GlEntrySource glEntrySource) {
		this.glEntrySource = glEntrySource;
	}
	
	/**
	 * Get the collection of related general ledger entries.
	 * @return The collection of related general ledger entries.
	 */
	@OneToMany (fetch = FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	@JoinColumn (name = "GENERAL_LEDGER_ID", insertable=false, updatable=false)
	public Collection<GlEntry> getGlEntries() {
		return glEntries;
	}

	/**
	 * Set the collection of related general ledger entries.
	 * @param glEntries The collection of related general ledger entries.
	 */
	public void setGlEntries(Collection<GlEntry> glEntries) {
		this.glEntries = glEntries;
	}
	
	@OneToOne (fetch = FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	@JoinColumn (name = "CREATED_BY", insertable=false, updatable=false)
	public User getPreparedBy() {
		return preparedBy;
	}
	
	public void setPreparedBy(User preparedBy) {
		this.preparedBy = preparedBy;
	}
	
	@Transient
	public User getCheckedBy() {
		return checkedBy;
	}
	
	public void setCheckedBy(User checkedBy) {
		this.checkedBy = checkedBy;
	}
	
	@Transient
	public User getApprovedBy() {
		return approvedBy;
	}
	
	public void setApprovedBy(User approvedBy) {
		this.approvedBy = approvedBy;
	}

	@Transient
	public Date getCheckedDate() {
		return checkedDate;
	}

	public void setCheckedDate(Date checkedDate) {
		this.checkedDate = checkedDate;
	}

	@Transient
	public Date getApprovedDate() {
		return approvedDate;
	}

	public void setApprovedDate(Date approvedDate) {
		this.approvedDate = approvedDate;
	}
	
	@Transient
	public Position getPreparedPosition() {
		return preparedPosition;
	}

	public void setPreparedPosition(Position preparedPosition) {
		this.preparedPosition = preparedPosition;
	}

	@Transient
	public Position getApprovedPosition() {
		return approvedPosition;
	}

	public void setApprovedPosition(Position approvedPosition) {
		this.approvedPosition = approvedPosition;
	}

	@OneToOne
	@JoinColumn (name = "COMPANY_ID", insertable=false, updatable=false)
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@OneToOne
	@JoinColumn (name = "DIVISION_ID", insertable=false, updatable=false)
	public Division getDivision() {
		return division;
	}

	public void setDivision(Division division) {
		this.division = division;
	}

	@Override
	@Transient
	public String getWorkflowName() {
		return super.getWorkflowName() + divisionId;
	}

	@Override
	@Transient
	public List<OOChild> getChildren() {
		List<OOChild> children = new ArrayList<OOChild>(glEntries);
		if (glEntries != null) {
			children.addAll(glEntries);
		}
		if (referenceDocuments != null) {
			children.addAll(referenceDocuments);
		}
		return children;
	}

	@Transient
	public List<ReferenceDocument> getReferenceDocuments() {
		return referenceDocuments;
	}

	public void setReferenceDocuments(List<ReferenceDocument> referenceDocuments) {
		this.referenceDocuments = referenceDocuments;
	}

	/**
	 * Formats the Journal Voucher Number to: M 1
	 * @return The formatted Journal Voucher Number.
	 */
	@Transient
	public String getFormattedJVNumber() {
		if (company != null) {
			if (company.getCompanyCode() != null) {
				String companyCode = company.getCompanyCode();
				return companyCode + StringUtils.SPACE + sequenceNo;
			} else {
				char firstLetter = company.getName().charAt(0);
				return firstLetter + StringUtils.SPACE + sequenceNo;
			}
		}
		return null;
	}

	@Column (name = "CURRENCY_ID")
	public Integer getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(Integer currencyId) {
		this.currencyId = currencyId;
	}

	@Column (name = "CURRENCY_RATE_ID")
	public Integer getCurrencyRateId() {
		return currencyRateId;
	}

	public void setCurrencyRateId(Integer currencyRateId) {
		this.currencyRateId = currencyRateId;
	}

	@Column (name = "CURRENCY_RATE_VALUE")
	public Double getCurrencyRateValue() {
		return currencyRateValue;
	}

	public void setCurrencyRateValue(Double currencyRateValue) {
		this.currencyRateValue = currencyRateValue;
	}

	@OneToOne
	@JoinColumn (name = "CURRENCY_ID", insertable=false, updatable=false)
	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	@OneToOne
	@JoinColumn (name = "CURRENCY_RATE_ID", insertable=false, updatable=false)
	public CurrencyRate getCurrencyRate() {
		return currencyRate;
	}

	public void setCurrencyRate(CurrencyRate currencyRate) {
		this.currencyRate = currencyRate;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GeneralLedger [sequenceNo=").append(sequenceNo).append(", glStatusId=").append(glStatusId)
				.append(", glEntrySourceId=").append(glEntrySourceId).append(", companyId=").append(companyId)
				.append(", divisionId=").append(divisionId).append(", glDate=").append(glDate).append(", comment=")
				.append(comment).append(", glEntrySource=").append(glEntrySource).append(", checkedDate=")
				.append(checkedDate).append(", approvedDate=").append(approvedDate).append(", currencyId=")
				.append(currencyId).append(", currencyRateId=").append(currencyRateId).append(", currencyRateValue=")
				.append(currencyRateValue).append("]");
		return builder.toString();
	}
}
