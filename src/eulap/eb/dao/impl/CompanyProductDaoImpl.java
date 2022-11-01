package eulap.eb.dao.impl;

import java.util.Collection;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.CompanyProductDao;
import eulap.eb.domain.hibernate.CompanyProduct;

/**
 * Implementation class of {@link CompanyProduct} interface.

 *
 */
public class CompanyProductDaoImpl extends BaseDao<CompanyProduct> implements CompanyProductDao {
	
	@Override
	protected Class<CompanyProduct> getDomainClass() {
		return CompanyProduct.class;
	}
	
	@Override
	public CompanyProduct getCompanyProduct(int companyId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(CompanyProductField.companyId.name(), companyId));
		Collection<CompanyProduct> ret = getAll(dc);
		if (ret.size() < 1)
			return null;
		return ret.iterator().next();
	}
	
}
