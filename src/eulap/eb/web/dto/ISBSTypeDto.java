package eulap.eb.web.dto;

import java.util.List;

/**
 * Income Statement and Balance Sheet type DTO.

 * 
 */
public class ISBSTypeDto {
	private String label;
	private boolean isAtPositive;
	private boolean asOfBalance;
	private int sequenceOrder; 
	private String name;
	private List<ISBSAccountDto> accounts;
	private List<ISBSTotalDto> totals;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public boolean isAtPositive() {
		return isAtPositive;
	}

	public void setAtPositive(boolean isAtPositive) {
		this.isAtPositive = isAtPositive;
	}

	public boolean isAsOfBalance() {
		return asOfBalance;
	}

	public void setAsOfBalance(boolean asOfBalance) {
		this.asOfBalance = asOfBalance;
	}

	public int getSequenceOrder() {
		return sequenceOrder;
	}

	public void setSequenceOrder(int sequenceOrder) {
		this.sequenceOrder = sequenceOrder;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ISBSAccountDto> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<ISBSAccountDto> accounts) {
		this.accounts = accounts;
	}

	public List<ISBSTotalDto> getTotals() {
		return totals;
	}

	public void setTotals(List<ISBSTotalDto> totals) {
		this.totals = totals;
	}

	@Override
	public String toString() {
		return "ISBSTypeDto [label=" + label + ", isAtPositive=" + isAtPositive + ", asOfBalance=" + asOfBalance
				+ ", sequenceOrder=" + sequenceOrder + ", name=" + name + ", accounts=" + accounts + ", totals="
				+ totals + "]";
	}
}