package eulap.eb.dao.impl;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.CsiRFinishedProductDao;
import eulap.eb.domain.hibernate.CsiFinishedProduct;

/**
 * Implementing class of {@link CsiRFinishedProductDao}

 *
 */
public class CsiFinishedProductDaoImpl extends BaseDao<CsiFinishedProduct> implements CsiRFinishedProductDao {

	@Override
	protected Class<CsiFinishedProduct> getDomainClass() {
		return CsiFinishedProduct.class;
	}
}
