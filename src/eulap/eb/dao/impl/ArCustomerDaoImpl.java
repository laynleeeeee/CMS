package eulap.eb.dao.impl;

import java.util.Collection;
import java.util.List;

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
import eulap.eb.dao.ArCustomerDao;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.ArCustomer;
import eulap.eb.domain.hibernate.ArCustomerAccount;
import eulap.eb.domain.hibernate.BusinessClassification;

/**
 * DAO Implementation of {@link ArCustomerDao}

 *
 */
public class ArCustomerDaoImpl extends BaseDao<ArCustomer> implements ArCustomerDao{

	@Override
	protected Class<ArCustomer> getDomainClass() {
		return ArCustomer.class;
	}

	@Override
	public boolean isUniqueName(String name) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ArCustomer.FIELD.name.name(), name));
		return getAll(dc).size() < 1;
	}

	@Override
	public Page<ArCustomer> searchArCustomer(String searchCriteria, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.like(ArCustomer.FIELD.name.name(), "%"+searchCriteria.trim()+"%"));
		dc.addOrder(Order.asc(ArCustomer.FIELD.name.name()));
		return getAll(dc, pageSetting);
	}

	@Override
	public Collection<ArCustomer> getArCustomers(int serviceLeaseKeyId) {
		DetachedCriteria dc = getDCWithSLkeyIdAndActive(serviceLeaseKeyId);
		dc.addOrder(Order.asc(ArCustomer.FIELD.name.name()));
		return getAll(dc);
	}

	@SuppressWarnings("null")
	@Override
	public List<ArCustomer> getArCustomers(String name, Boolean isExact, int serviceLeaseKeyId) {
		DetachedCriteria dc = getDCWithSLkeyIdAndActive(serviceLeaseKeyId);
		if ((name != null || !name.trim().isEmpty()) && isExact == null)
			dc.add(Restrictions.like(ArCustomer.FIELD.name.name(), "%" + name.trim() + "%"));
		else if (isExact != null)
			dc.add(Restrictions.eq(ArCustomer.FIELD.name.name(), name.trim()));
		dc.addOrder(Order.asc(ArCustomer.FIELD.name.name()));
		dc.getExecutableCriteria(getSession()).setMaxResults(10);
		return getAll(dc);
	}

	@Override
	public Page<ArCustomer> searchArCustomer(Integer bussClassId, String name, String streetBrgy, String cityProvince, SearchStatus status,
			PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		if (!name.trim().isEmpty()) {
			dc.add(Restrictions.like(ArCustomer.FIELD.name.name(), StringFormatUtil.appendWildCard(name.trim())));
		}
		if(bussClassId != -1) {
			dc.add(Restrictions.eq(ArCustomer.FIELD.bussinessClassificationId.name(), bussClassId));
		}
		if(streetBrgy != null && !streetBrgy.trim().isEmpty()) {
			dc.add(Restrictions.like(ArCustomer.FIELD.streetBrgy.name(), StringFormatUtil.appendWildCard(streetBrgy.trim())));
		}
		if(cityProvince != null && !cityProvince.trim().isEmpty()) {
			dc.add(Restrictions.like(ArCustomer.FIELD.cityProvince.name(), StringFormatUtil.appendWildCard(cityProvince.trim())));
		}
		dc = DaoUtil.setSearchStatus(dc, ArCustomer.FIELD.active.name(), status);
		dc.addOrder(Order.asc(ArCustomer.FIELD.name.name()));
		return getAll(dc, pageSetting);
	}

	@Override
	public List<ArCustomer> getArCustomerList(Integer companyId) {
		DetachedCriteria dc =  getDetachedCriteria();
		if(companyId != null) {
			DetachedCriteria custAcctDC = DetachedCriteria.forClass(ArCustomerAccount.class);
			custAcctDC.setProjection(Projections.property(ArCustomerAccount.FIELD.arCustomerId.name()));
			custAcctDC.add(Restrictions.eq(ArCustomerAccount.FIELD.companyId.name(), companyId));
			dc.add(Subqueries.propertyIn(ArCustomer.FIELD.id.name(), custAcctDC));
		}
		dc.addOrder(Order.asc(ArCustomer.FIELD.name.name()));
		return getAll(dc);
	}

	@Override
	public List<ArCustomer> getArCustomers(Integer companyId, String name, Boolean isExact, Integer divisionId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ArCustomer.FIELD.active.name(), true));
		if ((name != null && !name.isEmpty()) && (isExact == null || !isExact)) {
			dc.add(Restrictions.like(ArCustomer.FIELD.name.name(), "%" + name.trim() + "%"));
		} else if (isExact != null && isExact) {
			dc.add(Restrictions.eq(ArCustomer.FIELD.name.name(), name.trim()));
		}
		if (companyId != null) {
			DetachedCriteria dcCustAcct =  DetachedCriteria.forClass(ArCustomerAccount.class);
			dcCustAcct.setProjection(Projections.property(ArCustomerAccount.FIELD.arCustomerId.name()));
			dcCustAcct.add(Restrictions.eq(ArCustomerAccount.FIELD.companyId.name(), companyId));
			if (divisionId != null) {
				DetachedCriteria acDc =  DetachedCriteria.forClass(AccountCombination.class);
				acDc.setProjection(Projections.property(AccountCombination.FIELD.id.name()));
				acDc.add(Restrictions.eq(AccountCombination.FIELD.divisionId.name(), divisionId));
				dcCustAcct.add(Subqueries.propertyIn(ArCustomerAccount.FIELD.defaultDebitACId.name(), acDc));
			}
			dc.add(Subqueries.propertyIn(ArCustomer.FIELD.id.name(), dcCustAcct));
		}
		dc.addOrder(Order.asc(ArCustomer.FIELD.name.name()));
		dc.getExecutableCriteria(getSession()).setMaxResults(10);
		return getAll(dc);
	}

	@Override
	public List<ArCustomer> getArCustomers(String name, boolean activeOnly, Integer limit) {
		DetachedCriteria dc = getDetachedCriteria();
		if (name != null && !name.trim().isEmpty()) {
			dc.add(Restrictions.like(ArCustomer.FIELD.name.name(),
					"%" + StringFormatUtil.removeExtraWhiteSpaces(name) + "%"));
		}
		if (activeOnly) {
			dc.add(Restrictions.eq(ArCustomer.FIELD.active.name(), true));
		}
		if (limit != null) {
			dc.getExecutableCriteria(getSession()).setMaxResults(limit);
		}
		dc.addOrder(Order.asc(ArCustomer.FIELD.name.name()));
		return getAll(dc);
	}

	@Override
	public ArCustomer getByName(String customerName) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ArCustomer.FIELD.name.name(), customerName.trim()));
		return get(dc);
	}

	@Override
	public boolean isUniqueTin(ArCustomer arCustomer) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ArCustomer.FIELD.tin.name(), arCustomer.getTin().trim()));
		if(arCustomer.getId() != 0){
			dc.add(Restrictions.ne(ArCustomer.FIELD.id.name(), arCustomer.getId()));
		}
		return getAll(dc).isEmpty();
	}

	@Override
	public boolean isUniqueCustomer(ArCustomer arCustomer) {
		DetachedCriteria dc = getDetachedCriteria();
		if(arCustomer.getBussinessClassificationId() == BusinessClassification.INDIVIDUAL_TYPE) {
			dc.add(Restrictions.eq(ArCustomer.FIELD.firstName.name(), arCustomer.getFirstName()));
			dc.add(Restrictions.eq(ArCustomer.FIELD.lastName.name(), arCustomer.getLastName()));
			dc.add(Restrictions.eq(ArCustomer.FIELD.middleName.name(), arCustomer.getMiddleName()));
		} else {
			dc.add(Restrictions.eq(ArCustomer.FIELD.name.name(), arCustomer.getName()));
		}

		if(arCustomer.getId() > 0) {
			dc.add(Restrictions.ne(ArCustomer.FIELD.id.name(), arCustomer.getId()));
		}
		return getAll(dc).isEmpty();
	}
}
