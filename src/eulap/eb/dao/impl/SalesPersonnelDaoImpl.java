package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.common.dao.DaoUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.SalesPersonnelDao;
import eulap.eb.domain.hibernate.SalesPersonnel;

/**
 * DAO Implementation of {@link SalesPersonnelDao}

 *
 */

public class SalesPersonnelDaoImpl extends BaseDao<SalesPersonnel> implements SalesPersonnelDao{

	@Override
	protected Class<SalesPersonnel> getDomainClass() {
		return SalesPersonnel.class;
	}

	@Override
	public boolean isUnique(SalesPersonnel salesPersonnel) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(SalesPersonnel.FIELD.name.name(), StringFormatUtil.removeExtraWhiteSpaces(salesPersonnel.getName())));
		if(salesPersonnel.getId() != 0) {
			dc.add(Restrictions.ne(SalesPersonnel.FIELD.id.name(), salesPersonnel.getId()));
		}
		dc.add(Restrictions.eq(SalesPersonnel.FIELD.companyId.name(), salesPersonnel.getCompanyId()));
		return getAll(dc).isEmpty();
	}

	@Override
	public Page<SalesPersonnel> searchSalesPersonnel(Integer companyId, String name, SearchStatus status, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		if(companyId != -1 && companyId != 0) {
			dc.add(Restrictions.eq(SalesPersonnel.FIELD.companyId.name(), companyId));
		}
		if(!name.trim().isEmpty()) {
			dc.add(Restrictions.like(SalesPersonnel.FIELD.name.name(), StringFormatUtil.appendWildCard(name.trim())));
		}
		dc = DaoUtil.setSearchStatus(dc, SalesPersonnel.FIELD.active.name(), status);
		dc.addOrder(Order.asc(SalesPersonnel.FIELD.name.name()));
		return getAll(dc, pageSetting);
	}

	@Override
	public List<SalesPersonnel> getSalesPersonnelByName(Integer companyId, String name, Boolean isExact) {
		DetachedCriteria dc = getDetachedCriteria();
		if(companyId != null) {
			dc.add(Restrictions.eq(SalesPersonnel.FIELD.companyId.name(), companyId));
		}
		if(isExact) {
			dc.add(Restrictions.eq(SalesPersonnel.FIELD.name.name(), name.trim()));
		} else {
			dc.add(Restrictions.like(SalesPersonnel.FIELD.name.name(), StringFormatUtil.appendWildCard(name)));
		}
		dc.add(Restrictions.eq(SalesPersonnel.FIELD.active.name(), true));
		return getAll(dc);
	}
}
