package eulap.eb.dao;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.GlEntrySource;

/**
 * Data access object of {@link GlEntrySource}

 *
 */
public interface GlEntrySourceDao extends Dao<GlEntrySource>{
	/**
	 * Get the gl entry source domain based on the id
	 * @param glEntrySourceId The gl entry source id.
	 * @return The gl entry source id.
	 */
	GlEntrySource getGlEntrySource (int glEntrySourceId);
}
