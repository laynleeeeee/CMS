package eulap.eb.service.report;

/**
 * A class that handles different parameters in filtering the Monthly Value Added
 * Tax Declaration Report.

 */

public class MonthlyValueAddedTaxDecParam {
	private int companyId;
	private int divisionId;
	private int month;
	private int year;
	private int quarter;

	public int getCompanyId() {
		return companyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	public int getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(int divisionId) {
		this.divisionId = divisionId;
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

	public int getQuarter() {
		return quarter;
	}

	public void setQuarter(int quarter) {
		this.quarter = quarter;
	}

	public String getMonthName() {
		String monthName = "";
		switch (month) {
		case 1:
			monthName = "January";
			 break;
		case 2:
			monthName = "Febuary";
			 break;
		case 3:
			monthName = "March";
			 break;
		case 4:
			monthName = "April";
			 break;
		case 5:
			monthName = "May";
			 break;
		case 6:
			monthName = "June";
			 break;
		case 7:
			monthName = "July";
			 break;
		case 8:
			monthName = "August";
			 break;
		case 9:
			monthName = "September";
			 break;
		case 10:
			monthName = "October";
			 break;
		case 11:
			monthName = "November";
			 break;
		case 12:
			monthName = "December";
			 break;
		};
		return monthName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MonthlyValueAddedTaxDecParam [companyId=").append(companyId).append(", divisionId=").append(divisionId)
		.append(", month=").append(month).append("]");
		return builder.toString();
	}
}
