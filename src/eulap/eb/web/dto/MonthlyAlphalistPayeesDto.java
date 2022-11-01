package eulap.eb.web.dto;

/**
 * Monthly Alphalist of Payees.

 */

public class MonthlyAlphalistPayeesDto {
	private String tin;
	private String regisName;
	private String atcCode;
	private String naturePayment;
	private double amountTaxBase;
	private double taxRate;
	private double taxHeld;
	private int sequenceNum;

	public int getSequenceNum() {
		return sequenceNum;
	}

	public void setSequenceNum(int sequenceNum) {
		this.sequenceNum = sequenceNum;
	}

	public String getTin() {
		return tin;
	}

	public void setTin(String tin) {
		this.tin = tin;
	}

	public double getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(double taxRate) {
		this.taxRate = taxRate;
	}

	public String getRegisName() {
		return regisName;
	}

	public void setRegisName(String regisName) {
		this.regisName = regisName;
	}

	public String getAtcCode() {
		return atcCode;
	}

	public void setAtcCode(String atcCode) {
		this.atcCode = atcCode;
	}

	public String getNaturePayment() {
		return naturePayment;
	}

	public void setNaturePayment(String naturePayment) {
		this.naturePayment = naturePayment;
	}

	public double getAmountTaxBase() {
		return amountTaxBase;
	}

	public void setAmountTaxBase(double amountTaxBase) {
		this.amountTaxBase = amountTaxBase;
	}

	public double getTaxHeld() {
		return taxHeld;
	}

	public void setTaxHeld(double taxHeld) {
		this.taxHeld = taxHeld;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MonthlyAlphalistPayeesDto [tin=").append(tin).append(", regisName=").append(regisName)
				.append(", atcCode=").append(atcCode).append(", naturePayment=").append(naturePayment)
				.append(", amountTaxBase=").append(amountTaxBase).append(", taxRate=").append(taxRate)
				.append(", taxHeld=").append(taxHeld).append(", sequenceNum=").append(sequenceNum).append("]");
		return builder.toString();
	}
}
