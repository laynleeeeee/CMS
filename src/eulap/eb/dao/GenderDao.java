package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.Gender;


/**
 * Data Access Object of {@link Gender}

 */
public interface GenderDao extends Dao<Gender> {
	/**
	 * Get the list of genders with inactive.
	 * @param genderId The gender id.
	 * @return The list of genders.
	 */
	public List<Gender> getAllWithInactive(Integer genderId);

}
