package eulap.eb.dao.impl;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.FormDeductionTypeDao;
import eulap.eb.domain.hibernate.FormDeductionType;

/**
 * Implementation class of {@link FormDeductionTypeDao}

 *
 */
public class FormDeductionTypeDaoImpl extends BaseDao<FormDeductionType> implements FormDeductionTypeDao{

	@Override
	protected Class<FormDeductionType> getDomainClass() {
		return FormDeductionType.class;
	}

}
