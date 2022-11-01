package eulap.eb.service.is;

import java.util.List;

/**
 * Income statement sub report class.

 *
 */
public final class SubStatement {
	private String title;
	private String totalLabel;
	private double amount;
	private List<ISAccountClass> accountClasses;
	private boolean isIncome;
	
	private SubStatement() {
		// use instance by
	}

	protected static SubStatement getInstanceBy(String title,
			String totalLabel, boolean isIncome, List<ISAccountClass> accountClasses) {
		SubStatement subStatement = new SubStatement();
		subStatement.title = title;
		subStatement.totalLabel = totalLabel;
		subStatement.accountClasses = accountClasses;
		subStatement.isIncome = isIncome;
		return subStatement;
	}

	public String getTitle() {
		return title;
	}

	public String getTotalLabel() {
		return totalLabel;
	}

	public double getAmount() {
		return amount;
	}

	public boolean isIncome() {
		return isIncome;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public List<ISAccountClass> getAccountClasses() {
		return accountClasses;
	}

	public void setAccountClasses(List<ISAccountClass> accountClasses) {
		this.accountClasses = accountClasses;
	}

	@Override
	public String toString() {
		return "SubStatement [title=" + title + ", totalLabel=" + totalLabel
				+ ", amount=" + amount + ", accountClasses=" + accountClasses
				+ "]";
	}

}
