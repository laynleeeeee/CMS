package eulap.eb.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.TimePeriodDao;
import eulap.eb.domain.hibernate.TimePeriod;
import eulap.eb.domain.hibernate.TimePeriodStatus;
import eulap.eb.web.dto.FormDetailsDto;

/**
 * DAO implementation of {@link TimePeriodDao}

 */
public class TimePeriodDaoImpl extends BaseDao<TimePeriod> implements TimePeriodDao{

	@Override
	protected Class<TimePeriod> getDomainClass() {
		return TimePeriod.class;
	}

	@Override
	public Page<TimePeriod> searchTimePeriods(String name, int periodStatusId,
			Date dateFrom, Date dateTo, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();

		if(name != null && !name.isEmpty())
			dc.add(Restrictions.like(TimePeriod.Field.name.name(), "%"+name+"%"));
		if(periodStatusId != -1)
			dc.add(Restrictions.eq(TimePeriod.Field.periodStatusId.name(), periodStatusId));

		String sql = "? BETWEEN DATE_FROM AND DATE_TO";
		if(dateFrom != null && dateTo != null) {
			LogicalExpression betweenFromTo =
					Restrictions.or(Restrictions.between(TimePeriod.Field.dateFrom.name(), dateFrom, dateTo),
								Restrictions.between(TimePeriod.Field.dateTo.name(), dateFrom, dateTo));

			LogicalExpression withinRangeExpression =
					Restrictions.or(Restrictions.sqlRestriction(sql, dateFrom, Hibernate.DATE),
							Restrictions.sqlRestriction(sql, dateTo, Hibernate.DATE));
			dc.add(Restrictions.or(betweenFromTo, withinRangeExpression));
		} else if (dateFrom != null) {
			dc.add(Restrictions.sqlRestriction(sql, dateFrom, Hibernate.DATE));
		} else if(dateTo != null) {
			dc.add(Restrictions.sqlRestriction(sql, dateTo, Hibernate.DATE));
		}
		dc.addOrder(Order.asc(TimePeriod.Field.dateFrom.name()));
		return getAll(dc, pageSetting);
	}

	@Override
	public boolean isUniqueName(String name) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.like(TimePeriod.Field.name.name(), name.trim()));
		return getAll(dc).size() < 1;
	}

	@Override
	public boolean isValidTimePeriod(TimePeriod timePeriod) {
		boolean isEdit = timePeriod.getId() != 0;
		String sql = "SELECT * FROM TIME_PERIOD where (? BETWEEN DATE_FROM AND DATE_TO" +
				" OR ? BETWEEN  DATE_FROM AND DATE_TO OR DATE_FROM >= ? and DATE_TO <= ?) ";
		if (isEdit)
			sql += "and TIME_PERIOD_ID != ?";
		Session session = null;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(0, DateUtil.formatToSqlDate(timePeriod.getDateFrom()));
			query.setParameter(1, DateUtil.formatToSqlDate(timePeriod.getDateTo()));
			query.setParameter(2, DateUtil.formatToSqlDate(timePeriod.getDateFrom()));
			query.setParameter(3, DateUtil.formatToSqlDate(timePeriod.getDateTo()));
			if (isEdit)
				query.setParameter(4, timePeriod.getId());
			List<?> list = query.list();
			return list.size() < 1;
		} finally {
			if (session != null)
				session.close();
		}
	}

	@Override
	public Page<TimePeriod> getAllTimePeriods() {
		DetachedCriteria dc = getDetachedCriteria();
		dc.addOrder(Order.asc(TimePeriod.Field.dateFrom.name()));
		return getAll(dc, new PageSetting(1));
	}

	@Override
	public Collection<TimePeriod> getOpenTimePeriods() {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(TimePeriod.Field.periodStatusId.name(), TimePeriodStatus.OPEN));
		return getAll(dc);
	}

	@Override
	public List<TimePeriod> getTimePeriods() {
		DetachedCriteria dc = getDetachedCriteria();
		dc.addOrder(Order.desc(TimePeriod.Field.dateFrom.name()));
		return getAll(dc);
	}

	@Override
	public List<TimePeriod> getCurrentTimePeriods(Date date) {
		DetachedCriteria dc = getDetachedCriteria();
		if(date != null){
			dc.add(Restrictions.le(TimePeriod.Field.dateFrom.name(), date));
			dc.addOrder(Order.desc(TimePeriod.Field.dateFrom.name()));
		}
		return getAll(dc);
	}

	@Override
	public List<FormDetailsDto> getUnpostedForms(Date dateFrom, Date dateTo) {
		List<FormDetailsDto> tpDtos = new ArrayList<FormDetailsDto>();
		List<Object> objs = executeSP("GET_UNPOSTED_FORMS", dateFrom, dateTo);
		if(objs != null && !objs.isEmpty()) {
			for (Object obj : objs) {
				Object[] row = (Object[]) obj;
				int columnNumber = 0;
				String source = (String)row[columnNumber++]; //0
				Integer formId = (Integer)row[columnNumber++]; //1
				String seqNo = (String)row[columnNumber++]; //2
				String refNo = (String)row[columnNumber++]; //3
				Date date = (Date)row[columnNumber++]; //4
				FormDetailsDto tpDto = FormDetailsDto.getInstanceOf(source, formId, seqNo, refNo, date);
				tpDtos.add(tpDto);
			}
		}
		return tpDtos;
	}
}