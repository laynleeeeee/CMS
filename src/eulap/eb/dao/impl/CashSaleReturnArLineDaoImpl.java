package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.CashSaleReturnArLineDao;
import eulap.eb.domain.hibernate.CashSaleReturnArLine;

/**
 * DAO Implementation class of {@link CashSaleReturnArLineDao}

 *
 */
public class CashSaleReturnArLineDaoImpl extends BaseDao<CashSaleReturnArLine> implements CashSaleReturnArLineDao{

	@Override
	protected Class<CashSaleReturnArLine> getDomainClass() {
		return CashSaleReturnArLine.class;
	}

	@Override
	public List<CashSaleReturnArLine> getCsrArLine(int cashSaleReturnId) {
		DetachedCriteria csArLineDc = getDetachedCriteria();
		csArLineDc.add(Restrictions.eq(CashSaleReturnArLine.FIELD.cashSaleReturnId.name(), cashSaleReturnId));
		return getAll(csArLineDc);
	}
}
