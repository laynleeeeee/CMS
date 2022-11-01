package eulap.eb.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.UomConversionDao;
import eulap.eb.domain.hibernate.UomConversion;

/**
 * Implementing class of {@link UomConversionDao}

 *
 */
public class UomConversionDaoImpl extends BaseDao<UomConversion> implements UomConversionDao {

	@Override
	protected Class<UomConversion> getDomainClass() {
		return UomConversion.class;
	}

	@Override
	public UomConversion getConvByFromAndTo(Integer uomFrom, Integer uomTo) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(UomConversion.FIELD.uomFrom.name(), uomFrom));
		dc.add(Restrictions.eq(UomConversion.FIELD.uomTo.name(), uomTo));
		return get(dc);
	}

}
