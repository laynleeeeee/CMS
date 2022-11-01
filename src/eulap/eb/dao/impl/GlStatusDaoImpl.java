package eulap.eb.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.GlStatusDao;
import eulap.eb.domain.hibernate.GlStatus;

/**
 * Implementation class of {@link GlStatusDao}

 *
 */
public class GlStatusDaoImpl extends BaseDao<GlStatus> implements GlStatusDao{
	@Override
	protected Class<GlStatus> getDomainClass() {
		return GlStatus.class;
	}

	@Override
	public GlStatus getGlStatus(int glStatusId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(GlStatus.FIELD.id.name(), glStatusId));
		return get(dc);
	}
}
