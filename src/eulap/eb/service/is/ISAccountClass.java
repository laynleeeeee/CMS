package eulap.eb.service.is;

import java.util.List;

/**
 * Income statement account class.

 *
 */
public final class ISAccountClass {
	private String name;
	private boolean isPositive;
	private boolean isIncome;
	private Integer sequenceOrder;
	private List<ISAccountType> accountTypes;
	private double amount;
	
	private ISAccountClass () {
		// Intended for the Income statement parser only
	}
	protected static ISAccountClass getInstanceOf (String name, boolean isPositive,
			boolean isIncome, int sequenceNumber) {
		ISAccountClass accountClass = new ISAccountClass();
		accountClass.name = name;
		accountClass.isPositive = isPositive; 
		accountClass.isIncome = isIncome;
		accountClass.sequenceOrder = sequenceNumber;
		return accountClass;
	}

	public String getName() {
		return name;
	}
	
	public boolean isPositive() {
		return isPositive;
	}
	
	public boolean isIncome() {
		return isIncome;
	}
	
	public Integer getSequenceOrder() {
		return sequenceOrder;
	}
	
	public List<ISAccountType> getAccountTypes() {
		return accountTypes;
	}
	
	protected void setAccountTypes(List<ISAccountType> accountTypes) {
		this.accountTypes = accountTypes;
	}

	public double getAmount() {
		return amount;
	}
	
	public void setAmount(double amount) {
		this.amount = amount;
	}

	@Override
	public String toString() {
		return "ISAccountClass [name=" + name + ", isPositive=" + isPositive
				+ ", isIncome=" + isIncome + ", sequenceOrder="
				+ sequenceOrder + ", accountTypes=" + accountTypes + ", amount="
				+ amount + "]";
	}
}
