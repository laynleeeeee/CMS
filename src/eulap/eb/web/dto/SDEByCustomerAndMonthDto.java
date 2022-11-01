package eulap.eb.web.dto;

/**
 * A class that handles the Sales Delivery Efficiency By Customer and Month report data.

 * 
 */
public class SDEByCustomerAndMonthDto {
	private String customerName;
	private double totalDelivery;
	private int month;
	private int year;
	private String monthName;
	private int totalMonths;

	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public double getTotalDelivery() {
		return totalDelivery;
	}
	public void setTotalDelivery(double totalDelivery) {
		this.totalDelivery = totalDelivery;
	}

	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}

	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}

	public String getMonthName() {
		return monthName;
	}
	public void setMonthName(String monthName) {
		this.monthName = monthName;
	}

	public int getTotalMonths() {
		return totalMonths;
	}
	public void setTotalMonths(int totalMonths) {
		this.totalMonths = totalMonths;
	}

	@Override
	public String toString() {
		return "SDEByCustomerAndMonthDto [customerName=" + customerName + ", totalDelivery=" + totalDelivery
				+ ", month=" + month + ", year=" + year + ", monthName=" + monthName + ", totalMonths=" + totalMonths
				+ "]";
	}
}
