package eulap.eb.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.GlEntrySourceDao;
import eulap.eb.domain.hibernate.GlEntrySource;

/**
 * Implementation class of {@link GlEntrySourceDao}

 *
 */
public class GlEntrySourceDaoImpl extends BaseDao<GlEntrySource> implements GlEntrySourceDao{
	@Override
	protected Class<GlEntrySource> getDomainClass() {
		return GlEntrySource.class;
	}

	@Override
	public GlEntrySource getGlEntrySource(int glEntrySourceId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(GlEntrySource.FIELD.id.name(), glEntrySourceId));
		return get(dc);
	}
}
