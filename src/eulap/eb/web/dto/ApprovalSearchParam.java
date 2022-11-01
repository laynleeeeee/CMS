package eulap.eb.web.dto;

import java.util.Date;

import eulap.common.util.DateUtil;
import eulap.common.util.StringFormatUtil;
import eulap.eb.domain.hibernate.User;

/**
 * Container to hold values for searching in the approval form.

 */
public class ApprovalSearchParam {
	private Date dateFrom;
	private Date dateTo;
	private Double amount;
	private String companyNo;
	private String searchCriteria;
	private User user;

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

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getCompanyNo() {
		return companyNo;
	}

	public void setCompanyNo(String companyNo) {
		this.companyNo = companyNo;
	}

	public String getSearchCriteria() {
		return searchCriteria;
	}

	public void setSearchCriteria(String searchCriteria) {
		this.searchCriteria = searchCriteria;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "ApprovalSearchParam [dateFrom=" + dateFrom + ", dateTo="
				+ dateTo + ", amount=" + amount + ", companyNo=" + companyNo
				+ ", searchCriteria=" + searchCriteria + "]";
	}

	/**
	 * Parse the search criteria to extract the necessary data.
	 * @param searchCriteria The search criteria.
	 * @return The parsed data and set it the attributes to {@link ApprovalSearchParam}
	 */
	public static ApprovalSearchParam parseSearchCriteria(String searchCriteria) {
		ApprovalSearchParam searchParam = new ApprovalSearchParam();
		searchParam.setSearchCriteria(searchCriteria);
		if(searchCriteria.trim().isEmpty())
			return searchParam;

		//Split the string then set the necessary parameters to the DTO.
		String[] splittedSC = searchCriteria.split("\\s+");
		String criteria = searchCriteria;
		for (int index = 0; index < splittedSC.length; index++) {
			boolean isToBeRemoved = false;
			Date parsedDate = DateUtil.parseDate(splittedSC[index].trim());
			if(parsedDate != null) {
				isToBeRemoved = true;
				//Extract the date from and/or date to from the string
				if(searchParam.getDateFrom() == null)
					searchParam.setDateFrom(parsedDate);
				else
					searchParam.setDateTo(parsedDate);
			} else if(splittedSC[index].startsWith("#c") || splittedSC[index].startsWith("#C")) {
				//Extract the company number if string starts with #C or #c
				if(searchParam.getCompanyNo() == null) {
					//remove #C characters
					String compNo = splittedSC[index].substring(2);
					if(StringFormatUtil.isNumeric(compNo)) {
						searchParam.setCompanyNo(compNo);
						isToBeRemoved = true;
					}
				}
			} else if(splittedSC[index].startsWith("#a") || splittedSC[index].startsWith("#A")) {
				//Extract the amount if string starts with #A or #a
				if(searchParam.getAmount() == null) {
					//remove #A characters
					String strAmount = splittedSC[index].substring(2);
					if(!strAmount.trim().isEmpty()) {
						String toBeValidatedAmt = strAmount.replaceAll("[,.]", "");
						if(StringFormatUtil.isNumeric(toBeValidatedAmt)) {
							searchParam.setAmount(Double.valueOf(strAmount));
							isToBeRemoved = true;
						}
					}
				}
			}

			//Remove the date, company number and/or amount from the search criteria.
			if(isToBeRemoved)
				criteria = criteria.replace(splittedSC[index], "");
		}
		searchParam.setSearchCriteria(criteria.trim());
		return searchParam;
	}
}
