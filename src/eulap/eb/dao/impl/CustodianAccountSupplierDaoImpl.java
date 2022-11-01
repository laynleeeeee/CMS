

package eulap.eb.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.CustodianAccountSupplierDao;
import eulap.eb.domain.hibernate.CustodianAccountSupplier;

/**
 * DAO implementation class of {@link CustodianAccountSupplierDao}

 */

public class CustodianAccountSupplierDaoImpl extends BaseDao<CustodianAccountSupplier> implements CustodianAccountSupplierDao {
	@Override
	protected Class<CustodianAccountSupplier> getDomainClass() {
		return CustodianAccountSupplier.class;
	}

	@Override
	public boolean hasExistingCustodianAccountSupplier(Integer custodianAccountId) {
		DetachedCriteria dc = getCAS(custodianAccountId, null, null);
		return getAll(dc).size() > 0;
	}

	private DetachedCriteria getCAS(Integer custodianAccountId, Integer supplierId, Integer supplierAccountId) {
		DetachedCriteria dc = getDetachedCriteria();
		if(custodianAccountId != null) {
			dc.add(Restrictions.eq(CustodianAccountSupplier.FIELD.custodianAccountId.name(), custodianAccountId));
		}
		if(supplierId != null) {
			dc.add(Restrictions.eq(CustodianAccountSupplier.FIELD.supplierId.name(), supplierId));
		}
		if(supplierAccountId != null) {
			dc.add(Restrictions.eq(CustodianAccountSupplier.FIELD.supplierAccountId.name(), supplierAccountId));
		}
		return dc;
	}

	@Override
	public CustodianAccountSupplier getCustodianAccountSupplier(Integer custodianAccountId, Integer supplierId, Integer supplierAccountId) {
		DetachedCriteria dc = getCAS(custodianAccountId, supplierId, supplierAccountId);
		return get(dc);
	}

}
