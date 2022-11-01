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
import eulap.eb.dao.RatioDao;
import eulap.eb.domain.hibernate.Ratio;
import eulap.eb.domain.hibernate.User;

/**
 * Implementation class for {@link RatioDao}

 */
public class RatioDaoImpl extends BaseDao<Ratio> implements RatioDao {

	@Override
	protected Class<Ratio> getDomainClass() {
		return Ratio.class;
	}

	@Override
	public boolean isDuplicate(Ratio ratio) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.ne(Ratio.FIELD.id.name(), ratio.getId()));
		dc.add(Restrictions.eq(Ratio.FIELD.name.name(), ratio.getName()));
		return getAll(dc).size() > 0;
	}

	@Override
	public Page<Ratio> searchRatio(User user, String name, SearchStatus status, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		if(!name.trim().isEmpty()) {
			dc.add(Restrictions.like(Ratio.FIELD.name.name(),
					DaoUtil.handleMysqlSpecialCharacter(StringFormatUtil.removeExtraWhiteSpaces(name))+"%"));
		}
		dc = DaoUtil.setSearchStatus(dc, Ratio.FIELD.active.name(), status);
		dc.addOrder(Order.asc(Ratio.FIELD.name.name()));
		return getAll(dc, pageSetting);
	}

	@Override
	public List<Ratio> getRatios(Integer ratioId) {
		DetachedCriteria dc = getDetachedCriteria();
		Criterion criterion = Restrictions.eq(Ratio.FIELD.active.name(), true);
		if(ratioId != null) {
			criterion = Restrictions.or(criterion,
					Restrictions.and(Restrictions.eq(Ratio.FIELD.id.name(), ratioId),
							Restrictions.eq(Ratio.FIELD.active.name(), false)));
		}
		dc.add(criterion);
		dc.addOrder(Order.asc(Ratio.FIELD.name.name()));
		return getAll(dc);
	}

}
