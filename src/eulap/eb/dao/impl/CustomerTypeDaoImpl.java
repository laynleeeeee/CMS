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
import eulap.eb.dao.CustomerTypeDao;
import eulap.eb.domain.hibernate.ArCustomer;
import eulap.eb.domain.hibernate.CustomerType;

/**
 * DAO Implementation of {@link CustomerTypeDao}

 *
 */

public class CustomerTypeDaoImpl extends BaseDao<CustomerType> implements CustomerTypeDao{

	@Override
	protected Class<CustomerType> getDomainClass() {
		return CustomerType.class;
	}

	@Override
	public Page<CustomerType> searchCustomerTypes(String name,
			String description, SearchStatus status, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		if(!name.trim().isEmpty()) {
			dc.add(Restrictions.like(CustomerType.FIELD.name.name(), "%"+name.trim()+"%"));
		}
		if(!description.trim().isEmpty()) {
			dc.add(Restrictions.like(CustomerType.FIELD.description.name(), "%"+description.trim()+"%"));
		}
		dc = DaoUtil.setSearchStatus(dc, CustomerType.FIELD.active.name(), status);
		dc.addOrder(Order.asc(CustomerType.FIELD.name.name()));
		return getAll(dc, pageSetting);
	}

	@Override
	public boolean isUniqueCustomerType(CustomerType customerType) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.like(CustomerType.FIELD.name.name(), customerType.getName().trim()));
		if(customerType.getId() != 0) {
			dc.add(Restrictions.ne(CustomerType.FIELD.id.name(), customerType.getId()));
		}
		return getAll(dc).isEmpty();
	}

	@Override
	public List<CustomerType> getCustomerTypesWithInactive(Integer customerTypeId, Integer customerId) {
		DetachedCriteria dc = getDetachedCriteria();
		Criterion criterion = Restrictions.eq(CustomerType.FIELD.active.name(), true);
		if (customerTypeId != null && customerTypeId > 0) {
			criterion = Restrictions.or(criterion,
					Restrictions.and(Restrictions.eq(CustomerType.FIELD.id.name(), customerTypeId),
							Restrictions.eq(CustomerType.FIELD.active.name(), false)));
		}
		if (customerId != null) {
			DetachedCriteria csDc = DetachedCriteria.forClass(ArCustomer.class);
			csDc.setProjection(Projections.property(ArCustomer.FIELD.customerTypeId.name()));
			dc.add(Subqueries.propertyIn(CustomerType.FIELD.id.name(), csDc));
		}
		dc.add(criterion);
		dc.addOrder(Order.asc(CustomerType.FIELD.name.name()));
		return getAll(dc);
	}
}
