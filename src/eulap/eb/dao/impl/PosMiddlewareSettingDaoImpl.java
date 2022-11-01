package eulap.eb.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.common.dao.DaoUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.dao.PosMiddlewareSettingDao;
import eulap.eb.domain.hibernate.ArCustomer;
import eulap.eb.domain.hibernate.PosMiddlewareSetting;
import eulap.eb.domain.hibernate.User;

/**
 * Implementing class of {@link PosMiddlewareSettingDao}

 *
 */
public class PosMiddlewareSettingDaoImpl extends BaseDao<PosMiddlewareSetting> 
	implements PosMiddlewareSettingDao{

	@Override
	protected Class<PosMiddlewareSetting> getDomainClass() {
		return PosMiddlewareSetting.class;
	}

	@Override
	public Page<PosMiddlewareSetting> getMiddlewares(Integer companyId,
			Integer warehouseId, String customerName, SearchStatus searchStatus, User user,
			PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		if (companyId != null && companyId != -1) {
			dc.add(Restrictions.eq(PosMiddlewareSetting.FIELD.companyId.name(), companyId));
		}
		if (warehouseId != null && warehouseId != -1) {
			dc.add(Restrictions.eq(PosMiddlewareSetting.FIELD.warehouseId.name(), warehouseId));
		}
		if (customerName != null && !customerName.trim().isEmpty()) {
			DetachedCriteria custDc = DetachedCriteria.forClass(ArCustomer.class);
			custDc.setProjection(Projections.property(ArCustomer.FIELD.id.name()));
			custDc.add(Restrictions.like(ArCustomer.FIELD.name.name(), "%" + customerName.trim() + "%"));
			dc.add(Subqueries.propertyIn(PosMiddlewareSetting.FIELD.arCustomerId.name(), custDc));
		}
		addUserCompany(dc, user);
		dc = DaoUtil.setSearchStatus(dc, PosMiddlewareSetting.FIELD.active.name(), searchStatus);
		return getAll(dc, pageSetting);
	}

	@Override
	public boolean hasActive(PosMiddlewareSetting setting) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.ne(PosMiddlewareSetting.FIELD.id.name(), setting.getId()));
		dc.add(Restrictions.eq(PosMiddlewareSetting.FIELD.companyId.name(), setting.getCompanyId()));
		dc.add(Restrictions.eq(PosMiddlewareSetting.FIELD.active.name(), true));
		return getAll(dc).size() > 0;
	}

	@Override
	public PosMiddlewareSetting getByCompany(int companyId, User user) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(PosMiddlewareSetting.FIELD.companyId.name(), companyId));
		dc.add(Restrictions.eq(PosMiddlewareSetting.FIELD.active.name(), true));
		if(user != null) {
			addUserCompany(dc, user);
		}
		return get(dc);
	}

	@Override
	public PosMiddlewareSetting getByCompanyAndWarehouse(Integer companyId, Integer warehouseId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(PosMiddlewareSetting.FIELD.companyId.name(), companyId));
		dc.add(Restrictions.eq(PosMiddlewareSetting.FIELD.warehouseId.name(), warehouseId));
		dc.add(Restrictions.eq(PosMiddlewareSetting.FIELD.active.name(), true));
		return get(dc);
	}
}
