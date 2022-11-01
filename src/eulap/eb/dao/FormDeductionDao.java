package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.FormDeduction;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.ApprovalSearchParam;

/**
 * Data Access Object of {@link FormDeduction}

 *
 */
public interface FormDeductionDao extends Dao<FormDeduction>{

	/**
	 * Generates sequence number by type Id and company Id.
	 * @param typeId The type Id
	 * @return The generated sequence number.
	 */
	Integer generateSequenceNo(Integer typeId);

	/**
	 * Get the paged FormDeductions
	 * @param searchParam 
	 * @param param The search paramenter
	 * @param typeId The form deduction type id.
	 * @return The paged collection of {@link FormDeduction}
	 */
	Page<FormDeduction> getFormDeductions(ApprovalSearchParam searchParam, List<Integer> formStatusIds, Integer typeId, PageSetting pageSetting);

	/**
	 * Get the list of FormDeduction by criteria and by type.
	 * @param searchCriteria The search criteria.
	 * @param The type id of the FormDeduction.
	 * @param user The current logged user.
	 * @return The list of FomrDeductions.
	 */
	List<FormDeduction> searchFormDeductions(String searchCriteria, Integer typeId, User user);
}
