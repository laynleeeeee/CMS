package eulap.eb.web.dto;

import java.util.Date;

import eulap.common.util.DateUtil;
import eulap.eb.domain.hibernate.User;

/**
 * A class that holds the general information of forms that will be approved. 

 *
 */
public class FormApprovalDto {
	private final int id;
	private final String shortDesc;
	private final String lastUpdatedBy;
	private final String lastUpdatedDate;
	private final String status;
	private final boolean highlight;
	
	private FormApprovalDto (int id, String shortDesc, User lastUpdateBy, Date lastUpdatedDate, String status, boolean highlight) {
		this.id = id;
		this.shortDesc = shortDesc;
		this.lastUpdatedBy = lastUpdateBy.getFirstName() + " " + lastUpdateBy.getLastName();
		this.lastUpdatedDate = DateUtil.formatDate(lastUpdatedDate);
		this.status = status;
		this.highlight = highlight;
	}

	/**
	 * Get the instance of this class.
	 * @param id The unique id of form.
	 * @param shortDesc The short description of the form
	 * @param lastUpdateBy The created by user. 
	 * @return
	 */
	public static FormApprovalDto getInstanceBy (int id, String shortDesc, String status,
			User lastUpdateBy, Date lastUpdatedDate, boolean highlight) {
		return new FormApprovalDto(id, shortDesc, lastUpdateBy, lastUpdatedDate, status, highlight);
	}
	
	public int getId() {
		return id;
	}

	public String getShortDesc() {
		return shortDesc;
	}

	public String getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public String getLastUpdatedDate() {
		return lastUpdatedDate;
	}
	
	public String getStatus() {
		return status;
	}
	
	public boolean isHighlight() {
		return highlight;
	}

	@Override
	public String toString() {
		return "FormApprovalDto [id=" + id + ", shortDesc=" + shortDesc
				+ ", lastUpdatedBy=" + lastUpdatedBy + "]";
	}
}
