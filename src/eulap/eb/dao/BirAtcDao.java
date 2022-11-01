package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.BirAtc;

/**
 * Data access object interface for {@link BirAtc}

 */
	
public interface BirAtcDao extends Dao<BirAtc> {

	/**
	 * Get The {@link BirAtc} by wt type id
	 * @param wtTypeId The wt type id
	 * @return The list of {@link BirAtc}
	 */
	List<BirAtc> getListOfBirAtcByWtType(Integer[] wtTypeId);
}
