package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.ArMiscellaneousLine;

/**
 * Data access object of {@link ArMiscellaneousLine}

 *
 */
public interface ArMiscellaneousLineDao extends Dao<ArMiscellaneousLine>{

	/**
	 * Get the of AR Miscellaneous Lines
	 * @param arLineSetupId
	 * @return The list of AR Miscellaneous line.
	 */
	List<ArMiscellaneousLine> getArMiscLines(int arLineSetupId);
}
