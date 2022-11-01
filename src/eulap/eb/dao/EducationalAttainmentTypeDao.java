package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.EducationalAttainmentType;

/**
 * Data Access Object interface of {@link EducationalAttainmentType}.

 *
 */
public interface EducationalAttainmentTypeDao extends Dao<EducationalAttainmentType> {
	/**
	 * Get the list of educational attainment types with inactive.
	 * @param educAttnmntTypeId The educational attainment type id.
	 * @return The list of educational attainment types.
	 */
	public List<EducationalAttainmentType> getAllWithInactive(Integer educAttnmntTypeId);

}
