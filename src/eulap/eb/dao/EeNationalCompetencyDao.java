package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.EeNationalCompetency;

/**
 * Data Access Object interface of {@link EeNationalCompetency}.

 *
 */
public interface EeNationalCompetencyDao extends Dao<EeNationalCompetency> {

	/**
	 * Get the list of employee national competencies.
	 * @param ebObjectId The parent EB object id
	 * @return The list of employee licenses and certificates
	 */
	List<EeNationalCompetency> getNatCompByEbObjectId(Integer ebObjectId);

}
