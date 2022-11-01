package eulap.eb.domain.hibernate;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * Object representation of CUSTODIAN_ACCOUNT table.

 *
 */
@Entity
@Table(name="CUSTODIAN_ACCOUNT")
public class CustodianAccount extends BaseDomain{
	private String custodianName;
	private int companyId;
	private String custodianAccountName;
	private Integer termId;
	private boolean active;
	private Integer cdDivisionId;
	private Integer cdAccountId;
	private Integer cdAccountCombinationId;
	private AccountCombination cdAccountCombination;
	private Integer fdDivisionId;
	private Integer fdAccountId;
	private Integer fdAccountCombinationId;
	private AccountCombination fdAccountCombination;
	private Company company;
	private Term term;

	public enum FIELD {
		id, custodianName, companyId, custodianAccountName, termId, active,
		cdAccountCombinationId, fdAccountCombinationId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "CUSTODIAN_ACCOUNT_ID", columnDefinition = "int(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Override
	@Column(name = "CREATED_BY", columnDefinition = "int(10)")
	public int getCreatedBy() {
		return super.getCreatedBy();
	}

	@Override
	@Column(name = "CREATED_DATE", columnDefinition = "timestamp")
	public Date getCreatedDate() {
		return super.getCreatedDate();
	}

	@Override
	@Column(name = "UPDATED_BY", columnDefinition = "int(10)")
	public int getUpdatedBy() {
		return super.getUpdatedBy();
	}

	@Override
	@Column(name = "UPDATED_DATE", columnDefinition = "timestamp")
	public Date getUpdatedDate() {
		return super.getUpdatedDate();
	}

	@Column(name="CUSTODIAN_NAME", columnDefinition="varchar(100)")
	public String getCustodianName() {
		return custodianName;
	}
	public void setCustodianName(String custodianName) {
		this.custodianName = custodianName;
	}

	@Column(name="COMPANY_ID", columnDefinition="int(10)")
	public int getCompanyId() {
		return companyId;
	}
	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	@OneToOne
	@JoinColumn(name = "COMPANY_ID", insertable = false, updatable = false)
	public Company getCompany() {
		return company;
	}
	public void setCompany(Company company) {
		this.company = company;
	}

	@Column(name="CUSTODIAN_ACCOUNT_NAME", columnDefinition="varchar(100)")
	public String getCustodianAccountName() {
		return custodianAccountName;
	}
	public void setCustodianAccountName(String custodianAccountName) {
		this.custodianAccountName = custodianAccountName;
	}

	@Column(name="TERM_ID", columnDefinition="int(10)")
	public Integer getTermId() {
		return termId;
	}
	public void setTermId(Integer termId) {
		this.termId = termId;
	}

	@OneToOne
	@JoinColumn(name = "TERM_ID", insertable = false, updatable = false)
	public Term getTerm() {
		return term;
	}
	public void setTerm(Term term) {
		this.term = term;
	}

	@Column(name="ACTIVE", columnDefinition="tinyint(1)")
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}

	@Transient
	public Integer getCdDivisionId() {
		return cdDivisionId;
	}
	public void setCdDivisionId(Integer cdDivisionId) {
		this.cdDivisionId = cdDivisionId;
	}

	@Transient
	public Integer getCdAccountId() {
		return cdAccountId;
	}
	public void setCdAccountId(Integer cdAccountId) {
		this.cdAccountId = cdAccountId;
	}

	@Column(name="CD_ACCOUNT_COMBINATION_ID", columnDefinition="int(10)")
	public Integer getCdAccountCombinationId() {
		return cdAccountCombinationId;
	}
	public void setCdAccountCombinationId(Integer cdAccountCombinationId) {
		this.cdAccountCombinationId = cdAccountCombinationId;
	}

	@OneToOne
	@JoinColumn(name = "CD_ACCOUNT_COMBINATION_ID", insertable = false, updatable = false)
	public AccountCombination getCdAccountCombination() {
		return cdAccountCombination;
	}
	public void setCdAccountCombination(AccountCombination cdAccountCombination) {
		this.cdAccountCombination = cdAccountCombination;
	}

	@Transient
	public Integer getFdDivisionId() {
		return fdDivisionId;
	}
	public void setFdDivisionId(Integer fdDivisionId) {
		this.fdDivisionId = fdDivisionId;
	}

	@Transient
	public Integer getFdAccountId() {
		return fdAccountId;
	}
	public void setFdAccountId(Integer fdAccountId) {
		this.fdAccountId = fdAccountId;
	}

	@Column(name="FD_ACCOUNT_COMBINATION_ID", columnDefinition="int(10)")
	public Integer getFdAccountCombinationId() {
		return fdAccountCombinationId;
	}
	public void setFdAccountCombinationId(Integer fdAccountCombinationId) {
		this.fdAccountCombinationId = fdAccountCombinationId;
	}

	@OneToOne
	@JoinColumn(name = "FD_ACCOUNT_COMBINATION_ID", insertable = false, updatable = false)
	public AccountCombination getFdAccountCombination() {
		return fdAccountCombination;
	}
	public void setFdAccountCombination(AccountCombination fdAccountCombination) {
		this.fdAccountCombination = fdAccountCombination;
	}

	@Override
	public String toString() {
		return "CustodianAccount [custodianName=" + custodianName + ", companyId=" + companyId
				+ ", custodianAccountName=" + custodianAccountName + ", termId=" + termId + ", active=" + active
				+ ", cdDivisionId=" + cdDivisionId + ", cdAccountId=" + cdAccountId + ", cdAccountCombinationId="
				+ cdAccountCombinationId + ", fdDivisionId=" + fdDivisionId + ", fdAccountId=" + fdAccountId
				+ ", fdAccountCombinationId=" + fdAccountCombinationId + "]";
	}
}
