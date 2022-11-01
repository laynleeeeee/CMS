package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.CivilStatus;

/**
 * Data Access Object of {@link CivilStatus}

 */
public interface CivilStatusDao extends Dao<CivilStatus> {
	/**
	 * Get the list of civil statuses with inactive.
	 * @param civilStatusId The civil status id.
	 * @return The list of civil statuses.
	 */
	public List<CivilStatus> getAllWithInactive(Integer civilStatusId);

	/**
	 * Get the list of active {@link CivilStatus}.
	 * @return The list active civil status.
	 */
	List<CivilStatus> getActiveCivilStatuses();

}
