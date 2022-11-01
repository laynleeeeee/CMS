package eulap.eb.dao.impl;

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
import eulap.eb.dao.SupplierDao;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.Supplier;
import eulap.eb.domain.hibernate.SupplierAccount;
import eulap.eb.domain.hibernate.User;

/**
 * Dao Implementation of {@link SupplierDao}

 *
 */
public class SupplierDaoImpl extends BaseDao<Supplier> implements SupplierDao {

	@Override
	protected Class<Supplier> getDomainClass() {
		return Supplier.class;
	}

	@Override
	public boolean isUniqueSupplier(Supplier supplier) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.like(Supplier.FIELD.name.name(), supplier.getName()));
		dc.add(Restrictions.ne(Supplier.FIELD.id.name(), supplier.getId()));
		return getAll(dc).isEmpty();
	}

	@Override
	public List<Supplier> getSuppliers(int serviceLeaseKeyId) {
		DetachedCriteria dc = getDCWithSLkeyIdAndActive(serviceLeaseKeyId);
		dc.addOrder(Order.asc(Supplier.FIELD.name.name()));
		return getAll(dc);
	}

	@Override
	public Page<Supplier> searchSupplier(String searchCriteria,
			PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.like(Supplier.FIELD.name.name(), "%" + searchCriteria.trim() + "%"));
		dc.addOrder(Order.asc(Supplier.FIELD.name.name()));
		return getAll(dc, pageSetting);
	}

	@Override
	public List<Supplier> getAllSuppliers(int serviceLeaseKeyId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(Supplier.FIELD.serviceLeaseKeyId.name(), serviceLeaseKeyId));
		dc.addOrder(Order.asc(Supplier.FIELD.name.name()));
		return getAll(dc);
	}

	@Override
	public List<Supplier> getSuppliersByCompany(int companyId, int serviceLeaseKeyId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(Supplier.FIELD.serviceLeaseKeyId.name(), serviceLeaseKeyId));
		dc.addOrder(Order.asc(Supplier.FIELD.name.name()));
		//Subquery for Supplier Account
		DetachedCriteria supplierAcctCriteria = DetachedCriteria.forClass(SupplierAccount.class);
		supplierAcctCriteria.add(Restrictions.eq(SupplierAccount.FIELD.companyId.name(), companyId));
		supplierAcctCriteria.setProjection(Projections.property(SupplierAccount.FIELD.supplierId.name()));
		dc.add(Subqueries.propertyIn(Supplier.FIELD.id.name(), supplierAcctCriteria));
		return getAll(dc);
	}

	@Override
	public Page<Supplier> loadSuppliers(int serviceLeaseKeyId, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(Supplier.FIELD.serviceLeaseKeyId.name(), serviceLeaseKeyId));
		dc.addOrder(Order.asc(Supplier.FIELD.name.name()));
		return getAll(dc, pageSetting);
	}

	@Override
	public Page<Supplier> searchSuppliers(Integer bussinessClassificationId, String name, String streetBrgy, String cityProvince, SearchStatus status,
			int busRegTypeId, int serviceLeaseKeyId, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.like(Supplier.FIELD.name.name(), StringFormatUtil.appendWildCard(name.trim())));
		dc.add(Restrictions.like(Supplier.FIELD.streetBrgy.name(), StringFormatUtil.appendWildCard(streetBrgy.trim())));
		dc.add(Restrictions.like(Supplier.FIELD.cityProvince.name(), StringFormatUtil.appendWildCard(cityProvince.trim())));
		dc = DaoUtil.setSearchStatus(dc, Supplier.FIELD.active.name(), status);
		dc.add(Restrictions.eq(Supplier.FIELD.serviceLeaseKeyId.name(), serviceLeaseKeyId));
		if(busRegTypeId != -1) {
			dc.add(Restrictions.eq(Supplier.FIELD.busRegTypeId.name(), busRegTypeId));
		}
		if(bussinessClassificationId != -1) {
			dc.add(Restrictions.eq(Supplier.FIELD.bussinessClassificationId.name(), bussinessClassificationId));
		}
		dc.addOrder(Order.asc(Supplier.FIELD.name.name()));
		return getAll(dc, pageSetting);
	}

	@Override
	public boolean isUniqueTin(String tin, int serviceLeaseKeyId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(Supplier.FIELD.serviceLeaseKeyId.name(), serviceLeaseKeyId));
		dc.add(Restrictions.eq(Supplier.FIELD.tin.name(), tin));
		return getAll(dc).size() < 1;
	}

	@Override
	public List<Supplier> getSuppliers(User user, int serviceLeaseKeyId, String name) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.like(Supplier.FIELD.name.name(), "%" + name.trim() + "%"));
		dc.addOrder(Order.asc(Supplier.FIELD.active.name()));
		dc.addOrder(Order.asc(Supplier.FIELD.name.name()));
		return getAll(dc);
	}

	@Override
	public Supplier getSupplier(String name) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(Supplier.FIELD.name.name(), name.trim()));
		return get(dc);
	}

	@Override
	public List<Supplier> getSuppliersByBusReg(User user, String name, Integer busRegTypeId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.like(Supplier.FIELD.name.name(), "%" +name.trim()+ "%"));
		if(busRegTypeId != -1) {
			if(busRegTypeId == 1){
				// 1 = ALL VAT (VAT_IN and VAT_EX business registration type)
				dc.add(Restrictions.or(Restrictions.eq(Supplier.FIELD.busRegTypeId.name(), Supplier.BUS_REG_VAT_IN), Restrictions.eq(Supplier.FIELD.busRegTypeId.name(), Supplier.BUS_REG_VAT_EX)));
			} else {
				// NON - VAT
				dc.add(Restrictions.eq(Supplier.FIELD.busRegTypeId.name(), Supplier.BUS_REG_NON_VAT));
			}
		}
		dc.add(Restrictions.eq(Supplier.FIELD.active.name(), true));
		dc.addOrder(Order.asc(Supplier.FIELD.name.name()));
		return getAll(dc);
	}

	@Override
	public Supplier getSupplierByBusRegType(String name, Integer busRegTypeId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(Supplier.FIELD.name.name(), name));
		if(busRegTypeId != -1) {
			if(busRegTypeId == 1){
				// 1 = ALL VAT (VAT_IN and VAT_EX business registration type)
				dc.add(Restrictions.or(Restrictions.eq(Supplier.FIELD.busRegTypeId.name(), Supplier.BUS_REG_VAT_IN), Restrictions.eq(Supplier.FIELD.busRegTypeId.name(), Supplier.BUS_REG_VAT_EX)));
			} else {
				// NON - VAT
				dc.add(Restrictions.eq(Supplier.FIELD.busRegTypeId.name(), Supplier.BUS_REG_NON_VAT));
			}
		}
		dc.add(Restrictions.eq(Supplier.FIELD.active.name(), true));
		return get(dc);
	}

	@Override
	public List<Supplier> getSuppliers(Integer companyId, Integer divisionId, String name,
			Boolean isExact, User user, Integer limit, Integer accountId) {
		DetachedCriteria dc = getDetachedCriteria();
		if ((name != null && !name.isEmpty()) && isExact == null) {
			dc.add(Restrictions.like(Supplier.FIELD.name.name(), "%" + name.trim() + "%"));
		} else if (isExact != null) {
			dc.add(Restrictions.eq(Supplier.FIELD.name.name(), name.trim()));
		}
		// Supplier Account
		DetachedCriteria dcSupplierAcct = DetachedCriteria.forClass(SupplierAccount.class);
		dcSupplierAcct.setProjection(Projections.property(SupplierAccount.FIELD.supplierId.name()));
		addUserCompany(dcSupplierAcct, user);
		if (companyId != null) {
			dcSupplierAcct.add(Restrictions.eq(SupplierAccount.FIELD.companyId.name(), companyId));
		}
		if (divisionId != null || (accountId != null && accountId != -1)) {
			DetachedCriteria acDc = DetachedCriteria.forClass(AccountCombination.class);
			acDc.setProjection(Projections.property(AccountCombination.FIELD.id.name()));
			if(divisionId != null ) {
				acDc.add(Restrictions.eq(AccountCombination.FIELD.divisionId.name(), divisionId));
			}
			if(accountId != null && accountId != -1) {
				acDc.add(Restrictions.eq(AccountCombination.FIELD.accountId.name(), accountId));
			}
			dcSupplierAcct.add(Subqueries.propertyIn(SupplierAccount.FIELD.defaultCreditACId.name(), acDc));
		}
		dc.add(Subqueries.propertyIn(Supplier.FIELD.id.name(), dcSupplierAcct));
		dc.addOrder(Order.asc(Supplier.FIELD.name.name()));
		dc.add(Restrictions.eq(Supplier.FIELD.active.name(), true));
		if (limit != null) {
			dc.getExecutableCriteria(getSession()).setMaxResults(limit);
		}
		return getAll(dc);
	}

	@Override
	public List<Company> getCompaniesBySupplier(Integer supplierId,
			String companyName, User user) {
		DetachedCriteria cDc = DetachedCriteria.forClass(Company.class);
		DetachedCriteria sDc = DetachedCriteria.forClass(SupplierAccount.class);
		sDc.setProjection(Projections.property(SupplierAccount.FIELD.companyId.name()));
		addUserCompany(sDc, user);
		if(supplierId != null) {
			sDc.add(Restrictions.eq(SupplierAccount.FIELD.supplierId.name(), supplierId));
		}
		sDc.add(Restrictions.eq(SupplierAccount.FIELD.active.name(), true));
		if(companyName != null && !companyName.trim().isEmpty()) {
			cDc.add(Restrictions.or(Restrictions.like(Company.Field.name.name(), "%"+companyName+"%"),
					Restrictions.like(Company.Field.companyNumber.name(), "%"+companyName+"%")));
		}
		cDc.add(Restrictions.eq(Company.Field.active.name(), true));
		cDc.add(Subqueries.propertyIn("id", sDc));
		return getHibernateTemplate().findByCriteria(cDc);
	}

	@Override
	public boolean isUniqueTin(Supplier supplier) {
		DetachedCriteria dc = getDetachedCriteria();
		if(!supplier.getTin().isEmpty()) {
			dc.add(Restrictions.eq(Supplier.FIELD.tin.name(), supplier.getTin()));
			if(supplier.getId() != 0){
				dc.add(Restrictions.ne(Supplier.FIELD.id.name(), supplier.getId()));
			}
		}
		return getAll(dc).size() < 1;
	}
}
