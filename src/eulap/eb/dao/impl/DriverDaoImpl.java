package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.common.dao.DaoUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.dao.DriverDao;
import eulap.eb.domain.hibernate.Driver;

/**
 * Implementation class for {@link DriverDao}

 *
 */
public class DriverDaoImpl extends BaseDao<Driver> implements DriverDao {

	@Override
	protected Class<Driver> getDomainClass() {
		return Driver.class;
	}

	@Override
	public Page<Driver> searchDrivers(String name, Integer companyId, SearchStatus status,
			PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();

		Criterion criterion = null;
		if(name != null && !name.trim().isEmpty()) {
			criterion = Restrictions.or(Restrictions.like(Driver.FIELD.firstName.name(), "%"+name+"%"),
					Restrictions.like(Driver.FIELD.middleName.name(), "%"+name+"%"));
			dc.add(Restrictions.or(criterion, Restrictions.like(Driver.FIELD.lastName.name(), "%"+name+"%")));
		}
		if(companyId != -1) {
			dc.add(Restrictions.eq(Driver.FIELD.companyId.name(), companyId));
		}
		dc = DaoUtil.setSearchStatus(dc, Driver.FIELD.active.name(), status);
		return getAll(dc, pageSetting);
	}

	@Override
	public boolean isUniqueName(Integer driverId, Integer companyId, String firstName, String middleName, String lastName) {
		DetachedCriteria dc = getDetachedCriteria();
		if(driverId != null) {
			dc.add(Restrictions.ne(Driver.FIELD.id.name(), driverId));
		}
		if(companyId != null) {
			dc.add(Restrictions.eq(Driver.FIELD.companyId.name(), companyId));
		}
		if(firstName != null && !firstName.trim().isEmpty()) {
			dc.add(Restrictions.eq(Driver.FIELD.firstName.name(), firstName));
		}
		if(middleName != null && !middleName.trim().isEmpty()) {
			dc.add(Restrictions.eq(Driver.FIELD.middleName.name(), middleName));
		}
		if(lastName != null && !lastName.trim().isEmpty()) {
			dc.add(Restrictions.eq(Driver.FIELD.lastName.name(), lastName));
		}
		return getAll(dc).size() == 0;
	}

	@Override
	public List<Driver> getDriversByName(Integer companyId, String name) {
		DetachedCriteria dc = getDetachedCriteria();
		Criterion criterion = null;
		if(name != null && !name.trim().isEmpty()) {
			criterion = Restrictions.or(Restrictions.like(Driver.FIELD.firstName.name(), "%"+name+"%"),
					Restrictions.like(Driver.FIELD.middleName.name(), "%"+name+"%"));
			dc.add(Restrictions.or(criterion, Restrictions.like(Driver.FIELD.lastName.name(), "%"+name+"%")));
		}
		dc.add(Restrictions.eq(Driver.FIELD.active.name(), true));
		return getAll(dc, 10);//Will limit the max result to 10.
	}
}
