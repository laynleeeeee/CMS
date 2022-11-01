package eulap.eb.dao.impl;

import java.util.Collection;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.CheckbookTemplateDao;
import eulap.eb.domain.hibernate.CheckbookTemplate;

/**
 * DAO Implementation class of {@link CheckbookTemplateDao}

 *
 */
public class CheckbookTemplateDaoImpl extends BaseDao<CheckbookTemplate> implements CheckbookTemplateDao{

	@Override
	protected Class<CheckbookTemplate> getDomainClass() {
		return CheckbookTemplate.class;
	}

	@Override
	public Collection<CheckbookTemplate> getCheckbookTemplates() {
		DetachedCriteria dc = getDetachedCriteria();
		dc.addOrder(Order.asc(CheckbookTemplate.FIELD.name.name()));
		return getAll(dc);
	}
}
