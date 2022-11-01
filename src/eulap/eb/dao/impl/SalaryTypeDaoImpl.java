package eulap.eb.dao.impl;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.SalaryTypeDao;
import eulap.eb.domain.hibernate.SalaryType;

/**
 * Implementing class of {@code SalaryTypeDao}

 *
 */
public class SalaryTypeDaoImpl extends BaseDao<SalaryType> implements SalaryTypeDao{

	@Override
	protected Class<SalaryType> getDomainClass() {
		return SalaryType.class;
	}

}
