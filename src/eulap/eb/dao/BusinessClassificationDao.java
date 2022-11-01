package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.BusinessClassification;

/**
 * Data access object of {@link BusinessClassification}

 *
 */
public interface BusinessClassificationDao extends Dao<BusinessClassification> {
	/**
	 * Get the list of {@link BusinessClassification}.
	 * @param busClassId The {@link BusinessClassification} id. 
	 * @return The list of {@link BusinessClassification}.
	 */
	List<BusinessClassification> getBusinessClassifications(Integer busClassId);

}
