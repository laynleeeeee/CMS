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
import eulap.eb.dao.ActionNoticeDao;
import eulap.eb.domain.hibernate.ActionNotice;

/**
 * Implementation class of {@link ActionNoticeDao}

 *
 */
public class ActionNoticeDaoImpl extends BaseDao<ActionNotice> implements ActionNoticeDao{

	@Override
	protected Class<ActionNotice> getDomainClass() {
		return ActionNotice.class;
	}

	@Override
	public boolean isDuplicate(ActionNotice actionNotice) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.ne(ActionNotice.FIELD.id.name(), actionNotice.getId()));
		dc.add(Restrictions.eq(ActionNotice.FIELD.name.name(), actionNotice.getName()));
		return getAll(dc).size() > 0;
	}

	@Override
	public Page<ActionNotice> searchActionNotice(String actionNoticeName, Integer status, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		if(!actionNoticeName.isEmpty()){
			dc.add(Restrictions.like(ActionNotice.FIELD.name.name(),
					DaoUtil.handleMysqlSpecialCharacter(actionNoticeName) + "%"));
		}
		if(status != -1){
			boolean isActive = status == 1 ? true : false;
			dc.add(Restrictions.eq(ActionNotice.FIELD.active.name(), isActive));
		}
		dc.addOrder(Order.asc(ActionNotice.FIELD.name.name()));
		return getAll(dc, pageSetting);
	}

	@Override
	public List<ActionNotice> getActionNotices(Integer actionNoticeId) {
		DetachedCriteria dc = getDetachedCriteria();
		Criterion criterion = Restrictions.eq(ActionNotice.FIELD.active.name(), true);
		if(actionNoticeId != null) {
			criterion = Restrictions.or(criterion,
					Restrictions.and(Restrictions.eq(ActionNotice.FIELD.id.name(), actionNoticeId),
							Restrictions.eq(ActionNotice.FIELD.active.name(), false)));
		}
		dc.add(criterion);
		dc.addOrder(Order.asc(ActionNotice.FIELD.name.name()));
		return getAll(dc);
	}
}
