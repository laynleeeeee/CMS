package eulap.eb.service.report.bs;

import java.util.List;

import eulap.eb.service.is.ISAccountType;

/**
 * Balance sheet account class.

 *
 */
public class BSAccountClass {
	private String name;
	private boolean isAsset;
	private boolean isCurrentAsset;
	private boolean isLiability;
	private Integer sequenceOrder;
	private List<ISAccountType> accountTypes;
	private String totalLabel;
	private double amount;
	
	private BSAccountClass () {
		// Intended for the balance sheet parser only
	}
	
	protected static BSAccountClass getInstanceOf (String name, boolean isAsset,
			boolean isCurrentAsset, boolean isLiability, String totalLabel, int sequenceOrder) {
		BSAccountClass accountClass = new BSAccountClass();
		accountClass.name = name;
		accountClass.isAsset = isAsset;
		accountClass.isCurrentAsset = isCurrentAsset;
		accountClass.isLiability = isLiability;
		accountClass.sequenceOrder = sequenceOrder;
		accountClass.totalLabel = totalLabel;
		return accountClass;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isAsset() {
		return isAsset;
	}

	public void setAsset(boolean isAsset) {
		this.isAsset = isAsset;
	}

	public boolean isCurrentAsset() {
		return isCurrentAsset;
	}

	public void setCurrentAsset(boolean isCurrentAsset) {
		this.isCurrentAsset = isCurrentAsset;
	}

	public boolean isLiability() {
		return isLiability;
	}

	public void setLiability(boolean isLiability) {
		this.isLiability = isLiability;
	}
	
	public Integer getSequenceOrder() {
		return sequenceOrder;
	}

	public void setSequenceOrder(Integer sequenceOrder) {
		this.sequenceOrder = sequenceOrder;
	}

	public List<ISAccountType> getAccountTypes() {
		return accountTypes;
	}

	public void setAccountTypes(List<ISAccountType> accountTypes) {
		this.accountTypes = accountTypes;
	}
	
	public String getTotalLabel() {
		return totalLabel;
	}
	
	public void setTotalLabel(String totalLabel) {
		this.totalLabel = totalLabel;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	@Override
	public String toString() {
		return "BSAccountClass [name=" + name + ", isAsset=" + isAsset
				+ ", isCurrentAsset=" + isCurrentAsset + ", isLiability="
				+ isLiability + ", sequenceOrder=" + sequenceOrder
				+ ", accountTypes=" + accountTypes + ", totalLabel="
				+ totalLabel + ", amount=" + amount + "]";
	}
}
