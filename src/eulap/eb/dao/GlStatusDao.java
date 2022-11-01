package eulap.eb.dao;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.GlStatus;

/**
 * Data access object of {@link GlStatus}

 *
 */
public interface GlStatusDao extends Dao<GlStatus>{
	/**
	 * Get the gl status domain based on the id.
	 * @param glStatusId The gl status id.
	 * @return The gl status id.
	 */
	GlStatus getGlStatus (int glStatusId);
}
