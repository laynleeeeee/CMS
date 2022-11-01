package eulap.eb.service.report.bs;

import java.util.List;


/**
 * Balance sheet holds the information needed for the balance sheet.
 * 

 * 
 */
public final class BalanceSheet {
	private String title;
	private String totalLabel;
	private double amount;
	private List<BSAccountClass> accountClasses;
	
	public BalanceSheet() {
		// use instance by
	}
	
	protected static BalanceSheet getInstanceBy (String title, String totalLabel, 
		List<BSAccountClass> accountClasses) {
		BalanceSheet balanceSheet = new BalanceSheet();
		balanceSheet.title = title;
		balanceSheet.totalLabel = totalLabel;
		balanceSheet.accountClasses = accountClasses;
		return balanceSheet;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
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
	
	public List<BSAccountClass> getAccountClasses() {
		return accountClasses;
	}
	
	public void setAccountClasses(List<BSAccountClass> accountClasses) {
		this.accountClasses = accountClasses;
	}

	@Override
	public String toString() {
		return "BalanceSheet [title=" + title + ", totalLabel=" + totalLabel
				+ ", amount=" + amount + ", accountClasses=" + accountClasses
				+ "]";
	}
}
