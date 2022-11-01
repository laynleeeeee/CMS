package eulap.eb.service.report.bs;

/**
 * A class that holds the actual report of the balance sheet. 

 *
 */
public class BalanceSheetRowData {
	public final static BalanceSheetRowData EMPTY = new BalanceSheetRowData();
	private String title;
	private String subTitle;
	private double amountColumn1;
	private boolean aC1Underlined;
	private double amountColumn2;
	private boolean aC2Underlined;
	private double amountColumn3;
	private boolean aC3Underlined;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	public double getAmountColumn1() {
		return amountColumn1;
	}

	public void setAmountColumn1(double amountColumn1) {
		this.amountColumn1 = amountColumn1;
	}

	public boolean isaC1Underlined() {
		return aC1Underlined;
	}

	public void setaC1Underlined(boolean aC1Underlined) {
		this.aC1Underlined = aC1Underlined;
	}

	public double getAmountColumn2() {
		return amountColumn2;
	}

	public void setAmountColumn2(double amountColumn2) {
		this.amountColumn2 = amountColumn2;
	}

	public boolean isaC2Underlined() {
		return aC2Underlined;
	}

	public void setaC2Underlined(boolean aC2Underlined) {
		this.aC2Underlined = aC2Underlined;
	}

	public double getAmountColumn3() {
		return amountColumn3;
	}

	public void setAmountColumn3(double amountColumn3) {
		this.amountColumn3 = amountColumn3;
	}

	public boolean isaC3Underlined() {
		return aC3Underlined;
	}

	public void setaC3Underlined(boolean aC3Underlined) {
		this.aC3Underlined = aC3Underlined;
	}

	@Override
	public String toString() {
		return "BalanceSheetRowData [title=" + title + ", subTitle=" + subTitle
				+ ", amountColumn1=" + amountColumn1 + ", aC1Underlined="
				+ aC1Underlined + ", amountColumn2=" + amountColumn2
				+ ", aC2Underlined=" + aC2Underlined + ", amountColumn3="
				+ amountColumn3 + ", aC3Underlined=" + aC3Underlined + "]";
	}
}
