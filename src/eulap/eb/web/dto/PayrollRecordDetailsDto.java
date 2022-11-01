package eulap.eb.web.dto;

/**
 * Data transfer object for payroll records details

 */

public class PayrollRecordDetailsDto {
	private String employeeName;
	private String title;
	private Double value;
	private int titleOrder;
	private boolean computeTotal;

	public PayrollRecordDetailsDto(String employeeName, String title, Double value, int titleOrder, boolean computeTotal) {
		this.employeeName = employeeName;
		this.title = title;
		this.value = value;
		this.titleOrder = titleOrder;
		this.computeTotal = computeTotal;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public int getTitleOrder() {
		return titleOrder;
	}

	public void setTitleOrder(int titleOrder) {
		this.titleOrder = titleOrder;
	}

	public boolean isComputeTotal() {
		return computeTotal;
	}

	public void setComputeTotal(boolean computeTotal) {
		this.computeTotal = computeTotal;
	}
}
