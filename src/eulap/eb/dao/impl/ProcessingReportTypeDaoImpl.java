package eulap.eb.dao.impl;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.ProcessingReportTypeDao;
import eulap.eb.domain.hibernate.ProcessingReportType;

/**
 * Implementation class for {@link ProcessingReportTypeDao}

 *
 */
public class ProcessingReportTypeDaoImpl extends BaseDao<ProcessingReportType> implements ProcessingReportTypeDao {

	@Override
	protected Class<ProcessingReportType> getDomainClass() {
		return ProcessingReportType.class;
	}

}
