package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * Object representaion for the table GEMMA_WITHDRAWAL_SLIP

 */
@Entity
@Table(name="JYEI_WITHDRAWAL_SLIP")
public class JyeiWithdrawalSlip extends BaseDomain {
	private int withdrawalSlipId;
	private int requisitionTypeId;
	private String withdrawnBy;
	private WithdrawalSlip withdrawalSlip;

	public enum FIELD {
		id, withdrawalSlipId, requisitionTypeId, reqFormNo
	}

	/**
	 * OR Type ID for Withdrawal Slip-Serial Item relationship: 3000
	 */
	public static final int WS_SERIAL_ITEM_OR_TYPE = 3000;
	/**
	 * OR Type ID for Withdrawal Slip-Requisition Form relationship: 3001
	 */
	public static final int WITHDRAWAL_SLIP_REQUISITION_FORM_OR_TYPE = 3001;

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "JYEI_WITHDRAWAL_SLIP_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name="WITHDRAWAL_SLIP_ID", columnDefinition="int(10)")
	public int getWithdrawalSlipId() {
		return withdrawalSlipId;
	}

	public void setWithdrawalSlipId(int withdrawalSlipId) {
		this.withdrawalSlipId = withdrawalSlipId;
	}

	@Column(name="REQUISITION_TYPE_ID", columnDefinition="int(10)")
	public int getRequisitionTypeId() {
		return requisitionTypeId;
	}

	public void setRequisitionTypeId(int requisitionTypeId) {
		this.requisitionTypeId = requisitionTypeId;
	}

	@Column(name="WITHDRAWN_BY", columnDefinition="varchar(100)")
	public String getWithdrawnBy() {
		return withdrawnBy;
	}

	public void setWithdrawnBy(String withdrawnBy) {
		this.withdrawnBy = withdrawnBy;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GemmaWithdrawalSlip [withdrawalSlipId=").append(withdrawalSlipId).append(", requisitionTypeId=")
				.append(requisitionTypeId).append("]");
		return builder.toString();
	}

	@OneToOne
	@JoinColumn(name="WITHDRAWAL_SLIP_ID", insertable=false, updatable=false)
	public WithdrawalSlip getWithdrawalSlip() {
		return withdrawalSlip;
	}

	public void setWithdrawalSlip(WithdrawalSlip withdrawalSlip) {
		this.withdrawalSlip = withdrawalSlip;
	}

}
