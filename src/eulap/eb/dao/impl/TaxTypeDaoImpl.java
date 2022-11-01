package eulap.eb.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.TaxTypeDao;
import eulap.eb.domain.hibernate.TaxType;

/**
 * DAO implementation class for {@link TaxTypeDao}

 */

public class TaxTypeDaoImpl extends BaseDao<TaxType> implements TaxTypeDao {

	@Override
	protected Class<TaxType> getDomainClass() {
		return TaxType.class;
	}

	@Override
	public List<TaxType> geTaxTypes(Integer taxTypeId) {
		return getAll(getTaxTypeDetachedCriteria(taxTypeId));
	}

	private DetachedCriteria getTaxTypeDetachedCriteria(Integer taxTypeId) {
		DetachedCriteria dc = getDetachedCriteria();
		Criterion criterion = Restrictions.eq(TaxType.FIELD.active.name(), true);
		if (taxTypeId != null) {
			criterion = Restrictions.or(criterion, Restrictions.and(Restrictions.eq(TaxType.FIELD.id.name(), taxTypeId),
					Restrictions.eq(TaxType.FIELD.active.name(), false)));
		}
		dc.add(criterion);
		return dc;
	}

	@Override
	public List<TaxType> getAcctReceivableTaxTypes(Integer taxTypeId) {
		DetachedCriteria dc = getTaxTypeDetachedCriteria(taxTypeId);
		List<Integer> arTaxTypeIds = new ArrayList<Integer>();
		arTaxTypeIds = Arrays.asList(TaxType.VAT_EXEMPTED, TaxType.ZERO_RATED, TaxType.PRIVATE,
				TaxType.GOVERNMENT);
		addAsOrInCritiria(dc, TaxType.FIELD.id.name(), arTaxTypeIds);
		return getAll(dc);
	}

	@Override
	public List<TaxType> getAcctPayableTaxTypes(Integer taxTypeId, boolean isImportation) {
		DetachedCriteria dc = getTaxTypeDetachedCriteria(taxTypeId);
		List<Integer> apTaxTypeIds = new ArrayList<Integer>();
		if (isImportation) {
			apTaxTypeIds = Arrays.asList(TaxType.IMPORTATION);
		} else {
			apTaxTypeIds = Arrays.asList(TaxType.VAT_EXEMPTED, TaxType.ZERO_RATED, TaxType.GOODS,
					TaxType.SERVICES, TaxType.CAPITAL, TaxType.NRA, TaxType.IMPORTATION);
		}
		addAsOrInCritiria(dc, TaxType.FIELD.id.name(), apTaxTypeIds);
		return getAll(dc);
	}
}
