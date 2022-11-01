package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.common.dao.DaoUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.TypeOfLeaveDao;
import eulap.eb.domain.hibernate.TypeOfLeave;
import eulap.eb.domain.hibernate.User;

/**
 * Implementation class for {@link TypeOfLeave}

 *
 */
public class TypeOfLeaveDaoImpl extends BaseDao<TypeOfLeave> implements TypeOfLeaveDao{

	@Override
	public Page<TypeOfLeave> searchLeaves(User user, String name, SearchStatus status, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		if(!name.trim().isEmpty()) {
			dc.add(Restrictions.like(TypeOfLeave.FIELD.name.name(),
					DaoUtil.handleMysqlSpecialCharacter(StringFormatUtil.removeExtraWhiteSpaces(name))+"%"));
		}
		dc = DaoUtil.setSearchStatus(dc, TypeOfLeave.FIELD.active.name(), status);
		dc.addOrder(Order.asc(TypeOfLeave.FIELD.name.name()));
		return getAll(dc, pageSetting);
	}

	@Override
	public boolean isDuplicate(TypeOfLeave typeOfLeave) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.like(TypeOfLeave.FIELD.name.name(),
				StringFormatUtil.removeExtraWhiteSpaces(typeOfLeave.getName())));
		return getAll(dc).size() >= 1;
	}

	@Override
	protected Class<TypeOfLeave> getDomainClass() {
		return TypeOfLeave.class;
	}

	@Override
	public List<TypeOfLeave> getTypeOfLeaves(Integer leaveTypeId) {
		DetachedCriteria dc = getDetachedCriteria();
		Criterion criterion = Restrictions.eq(TypeOfLeave.FIELD.active.name(), true);
		if(leaveTypeId != null) {
			criterion = Restrictions.or(criterion,
					Restrictions.and(Restrictions.eq(TypeOfLeave.FIELD.id.name(), leaveTypeId),
							Restrictions.eq(TypeOfLeave.FIELD.active.name(), false)));
		}
		dc.add(criterion);
		dc.addOrder(Order.asc(TypeOfLeave.FIELD.name.name()));
		return getAll(dc);
	}
}
