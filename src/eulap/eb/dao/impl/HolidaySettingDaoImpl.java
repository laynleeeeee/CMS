package eulap.eb.dao.impl;

import java.util.Date;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.common.dao.DaoUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.dao.HolidaySettingDao;
import eulap.eb.domain.hibernate.HolidaySetting;

/**
 * DAO implementation class for {@link HolidaySettingDao}

 *
 */
public class HolidaySettingDaoImpl extends BaseDao<HolidaySetting> implements HolidaySettingDao{

	@Override
	protected Class<HolidaySetting> getDomainClass() {
		return HolidaySetting.class;
	}

	@Override
	public boolean isUniqueName(HolidaySetting holidaySetting) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(HolidaySetting.FIELD.name.name(), holidaySetting.getName()));
		if(holidaySetting.getCompanyId() != null){
			dc.add(Restrictions.eq(HolidaySetting.FIELD.companyId.name(), holidaySetting.getCompanyId()));
		}
		if(holidaySetting.getId() != 0){
			dc.add(Restrictions.ne(HolidaySetting.FIELD.id.name(), holidaySetting.getId()));
		}
		return getAll(dc).isEmpty();
	}

	@Override
	public Page<HolidaySetting> getHolidaySettings(Integer companyId, String name, Integer holidayTypeId, Date date, SearchStatus status,
			PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		if(companyId != null && !companyId.equals(-1)){
			dc.add(Restrictions.eq(HolidaySetting.FIELD.companyId.name(), companyId ));
		}
		if(holidayTypeId != null && !holidayTypeId.equals(-1)){
			dc.add(Restrictions.eq(HolidaySetting.FIELD.holidayTypeId.name(), holidayTypeId ));
		}
		if(name != null && !name.trim().isEmpty()){
			dc.add(Restrictions.like(HolidaySetting.FIELD.name.name(), "%" + name.trim() + "%"));
		}
		if(date != null){
			dc.add(Restrictions.eq(HolidaySetting.FIELD.date.name(),   date ));
		}
		dc = DaoUtil.setSearchStatus(dc, HolidaySetting.FIELD.active.name(), status);
		dc.addOrder(Order.asc(HolidaySetting.FIELD.name.name()));
		return getAll(dc, pageSetting);
	}

	@Override
	public boolean isHoliday(Date date, Integer companyId) {
		DetachedCriteria dc = getDetachedCriteria();
		if (companyId != null) {
			dc.add(Restrictions.eq(HolidaySetting.FIELD.companyId.name(), companyId));
		}
		dc.add(Restrictions.eq(HolidaySetting.FIELD.date.name(), date));
		dc.add(Restrictions.eq(HolidaySetting.FIELD.active.name(), true));
		return getAll(dc).size() > 0;
	}

	@Override
	public HolidaySetting getByDate(Date date) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(HolidaySetting.FIELD.date.name(), date));
		dc.add(Restrictions.eq(HolidaySetting.FIELD.active.name(), true));
		return get(dc);
	}
}
