package eulap.eb.service.is;

import java.util.List;


/**
 * Income Statement holds the information needed for the income statement.
 * 

 * 
 */
public final class IncomeStatement {
	
	private List<SubStatement> subStatements;
	private double amount;
	
	private IncomeStatement () {
		// Only for classes with the same package.
	}
	
	protected static IncomeStatement getInstanceOf (List<SubStatement> subStatements) {
		IncomeStatement is = new IncomeStatement();
		is.subStatements = subStatements;
		return is;
	}

	public List<SubStatement> getSubStatements() {
		return subStatements;
	}

	public double getAmount() {
		return amount;
	}
	
	public void setAmount(double amount) {
		this.amount = amount;
	}

	@Override
	public String toString() {
		return "IncomeStatement [subStatements=" + subStatements + ", amount="
				+ amount + "]";
	}

}
