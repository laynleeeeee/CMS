package eulap.eb.web.dto;

import java.util.Date;

/**
 * Monthly cash flow account date data transfer object.

 *
 */
public class MCFlowAccountDateDto {
	private int month;
	private String monthName;
	private Date dateFrom;
	private Date dateTo;
	private int year;

	public MCFlowAccountDateDto() {}

	public MCFlowAccountDateDto(int month, int year, String monthName, Date dateFrom, Date dateTo) {
		this.month = month;
		this.setYear(year);
		this.monthName = monthName;
		this.dateFrom = dateFrom;
		this.dateTo = dateTo;
	}

	/**
	 * Create an instance of {@code MonthCashFlowDateDto}
	 */
	public static MCFlowAccountDateDto getInstanceOf(
			int month, String monthName, Date dateFrom, Date dateTo ) {
		MCFlowAccountDateDto obj = new MCFlowAccountDateDto();
		obj.setMonth(month);
		obj.setMonthName(monthName);
		obj.setDateFrom(dateFrom);
		obj.setDateTo(dateTo);
		return obj;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public String getMonthName() {
		return monthName;
	}

	public void setMonthName(String monthName) {
		this.monthName = monthName;
	}

	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}


	@Override
	public String toString() {
		return "MCFlowAccountDateDto [month=" + month + ", monthName=" + monthName + ", dateFrom=" + dateFrom
				+ ", dateTo=" + dateTo + "]";
	}
}
