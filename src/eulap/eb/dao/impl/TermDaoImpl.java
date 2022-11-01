package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.TermDao;
import eulap.eb.domain.hibernate.ArCustomerAccount;
import eulap.eb.domain.hibernate.SupplierAccount;
import eulap.eb.domain.hibernate.Term;

/**
 * A class that implements the hibernate access object of {@link TermDao}

 *
 */
public class TermDaoImpl extends BaseDao<Term> implements TermDao{

	@Override
	protected Class<Term> getDomainClass() {
		return Term.class;
	}

	@Override
	public List<Term> getAllTerms(int serviceLeaseKeyId) {
		DetachedCriteria dc = getDCWithSLkeyIdAndActive(serviceLeaseKeyId);
		dc.addOrder(Order.asc(Term.FIELD.days.name()));
		return getAll(dc);
	}

	@Override
	public Term getTermBySupplierAccount(int supplierAccountId,
			int serviceLeaseKeyId) {
		DetachedCriteria dc = getDCWithSLkeyIdAndActive(serviceLeaseKeyId);
		DetachedCriteria supplierAcctCriteria = DetachedCriteria.forClass(SupplierAccount.class);
		supplierAcctCriteria.add(Restrictions.eq(SupplierAccount.FIELD.id.name(), supplierAccountId));
		supplierAcctCriteria.setProjection(Projections.property(SupplierAccount.FIELD.termId.name()));
		dc.add(Subqueries.propertyIn(Term.FIELD.id.name(), supplierAcctCriteria));
		return get(dc);
	}

	@Override
	public Term getTermByCustomerAccount(int customerAccountId,
			int serviceLeaseKeyId) {
		DetachedCriteria dc = getDCWithSLkeyIdAndActive(serviceLeaseKeyId);

		DetachedCriteria customerAcctCriteria = DetachedCriteria.forClass(ArCustomerAccount.class);
		customerAcctCriteria.add(Restrictions.eq(ArCustomerAccount.FIELD.id.name(), customerAccountId));
		customerAcctCriteria.setProjection(Projections.property(ArCustomerAccount.FIELD.termId.name()));

		dc.add(Subqueries.propertyIn(Term.FIELD.id.name(), customerAcctCriteria));
		return get(dc);
	}

	@Override
	public Page<Term> searchTerm(String name, Integer days, int status,
			PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		boolean result = status == 1 ? true : false;
		if(!name.isEmpty())
			dc.add(Restrictions.like(Term.FIELD.name.name(), "%" + name.trim() + "%"));
		if(days != null)
			dc.add(Restrictions.sqlRestriction("DAYS LIKE ?", "%" + days + "%", Hibernate.STRING));
		if(status != -1)
			dc.add(Restrictions.eq(Term.FIELD.active.name(), result));
		dc.addOrder(Order.asc(Term.FIELD.name.name()));
		return getAll(dc, pageSetting);
	}

	@Override
	public boolean isUnique(Term term) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.like(Term.FIELD.name.name(), term.getName().trim()));
		return getAll(dc).size() < 1;
	}

	@Override
	public Term getTermByName(String name) {
		DetachedCriteria termDc = getDetachedCriteria();
		termDc.add(Restrictions.eq(Term.FIELD.name.name(), name));
		return get(termDc);
	}

	@Override
	public List<Term> getAllTerms() {
		DetachedCriteria dc = getDetachedCriteria();
		dc.addOrder(Order.asc(Term.FIELD.days.name()));
		return getAll(dc);
	}

	@Override
	public List<Term> getTerms(Integer termId) {
		DetachedCriteria dc = getDetachedCriteria();
		if (termId != null) {
			dc.add(Restrictions.or(Restrictions.eq(Term.FIELD.active.name(), true), 
					Restrictions.eq( Term.FIELD.id.name(), termId)));
		} else {
			dc.add(Restrictions.eq(Term.FIELD.active.name(), true));
		}
		dc.addOrder(Order.asc(Term.FIELD.days.name()));
		return getAll(dc);
	}
}
