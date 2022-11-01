package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.common.dao.DaoUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.UserCustodianDao;
import eulap.eb.domain.hibernate.CustodianAccount;
import eulap.eb.domain.hibernate.UserCustodian;

/**
 * Implementing class of {@link UserCustodianDao}

 *
 */
public class UserCustodianDaoImpl extends BaseDao<UserCustodian> implements UserCustodianDao{

	@Override
	protected Class<UserCustodian> getDomainClass() {
		return UserCustodian.class;
	}

	@Override
	public Page<UserCustodian> getUserCustodians(Integer companyId, Integer divisionId, String userCustodianName,
			SearchStatus status, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		if(companyId != -1) {
			dc.add(Restrictions.eq(UserCustodian.FIELD.companyId.name(), companyId));
		}
		if(divisionId != -1) {
			dc.add(Restrictions.eq(UserCustodian.FIELD.divisionId.name(), divisionId));
		}
		if(userCustodianName != null && !userCustodianName.isEmpty()){
			DetachedCriteria caDc = DetachedCriteria.forClass(CustodianAccount.class);
			caDc.setProjection(Projections.property(CustodianAccount.FIELD.id.name()));
			caDc.add(Restrictions.like(CustodianAccount.FIELD.custodianName.name(), "%"+userCustodianName+"%"));
			dc.add(Subqueries.propertyIn(UserCustodian.FIELD.custodianAccountId.name(), caDc));
		}
		dc = DaoUtil.setSearchStatus(dc, UserCustodian.FIELD.active.name(), status);
		dc.addOrder(Order.asc(UserCustodian.FIELD.id.name()));
		return getAll(dc, pageSetting);
	}

	@Override
	public boolean isDuplicateUserCustodian(UserCustodian userCustodian) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(UserCustodian.FIELD.companyId.name(), userCustodian.getCompanyId()));
		dc.add(Restrictions.eq(UserCustodian.FIELD.divisionId.name(), userCustodian.getDivisionId()));
		dc.add(Restrictions.eq(UserCustodian.FIELD.custodianAccountId.name(), userCustodian.getCustodianAccountId()));
		if(userCustodian.getId() != 0){
			dc.add(Restrictions.ne(UserCustodian.FIELD.id.name(), userCustodian.getId()));
		}
		return !getAll(dc).isEmpty();
	}

	@Override
	public List<UserCustodian> getUserCustodians(Integer companyId, Integer divisionId, Integer userCustodianId) {
		DetachedCriteria dc = getDetachedCriteria();
		Criterion criterion = Restrictions.eq(UserCustodian.FIELD.active.name(), true);
		if (companyId != null) {
			dc.add(Restrictions.eq(UserCustodian.FIELD.companyId.name(), companyId));
		}
		if (divisionId != null) {
			dc.add(Restrictions.eq(UserCustodian.FIELD.divisionId.name(), divisionId));
		}
		if(userCustodianId != null) {
			criterion = Restrictions.or(criterion, Restrictions.and(Restrictions.eq(UserCustodian.FIELD.id.name(), userCustodianId),
					Restrictions.eq(UserCustodian.FIELD.active.name(), false)));
		}
		dc.add(criterion);
		return getAll(dc);
	}

	@Override
	public List<UserCustodian> getUserCustodians(Integer companyId, Integer divisionId, Integer userCustodianId, String name, boolean isExact) {
		DetachedCriteria dc = getDetachedCriteria();
		if (companyId != null) {
			dc.add(Restrictions.eq(UserCustodian.FIELD.companyId.name(), companyId));
		}
		if (divisionId != null && divisionId > 0) {
			dc.add(Restrictions.eq(UserCustodian.FIELD.divisionId.name(), divisionId));
		}
		if (userCustodianId != null && userCustodianId > 0) {
			dc.add(Restrictions.eq(UserCustodian.FIELD.id.name(), userCustodianId));
		}
		if (name != null && !name.isEmpty()) {
			DetachedCriteria caDC = DetachedCriteria.forClass(CustodianAccount.class);
			caDC.setProjection(Projections.property(CustodianAccount.FIELD.id.name()));
			if (isExact) {
				caDC.add(Restrictions.eq(CustodianAccount.FIELD.custodianName.name(), StringFormatUtil.removeExtraWhiteSpaces(name)));
			} else {
				caDC.add(Restrictions.like(CustodianAccount.FIELD.custodianName.name(), StringFormatUtil.appendWildCard(name)));
			}
			dc.add(Subqueries.propertyIn(UserCustodian.FIELD.custodianAccountId.name(), caDC));
		}
		dc.add(Restrictions.eq(UserCustodian.FIELD.active.name(), true));
		return getAll(dc);
	}
}
