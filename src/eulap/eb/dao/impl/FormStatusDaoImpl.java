package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.springframework.stereotype.Service;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.FormStatusDao;
import eulap.eb.domain.hibernate.FormStatus;

/**
 * A hibernate implementation class for FormStatsDao

 *
 */
@Service
public class FormStatusDaoImpl extends BaseDao<FormStatus> implements FormStatusDao{

	@Override
	protected Class<FormStatus> getDomainClass() {
		return FormStatus.class;
	}

	@Override
	public List<FormStatus> getPaymentStatuses() {
		DetachedCriteria dc = getDetachedCriteria();
		addAsOrInCritiria(dc, "id", FormStatus.ISSUED_ID, 
				FormStatus.NEGOTIABLE_ID,
				FormStatus.CLEARED_ID, FormStatus.CANCELLED_ID);
		return getAll(dc);
	}	
}
