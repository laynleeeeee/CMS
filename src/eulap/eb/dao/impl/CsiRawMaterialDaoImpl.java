package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.CsiRawMaterialDao;
import eulap.eb.domain.hibernate.CsiFinishedProduct;
import eulap.eb.domain.hibernate.CsiRawMaterial;

/**
 * Implementing class of {@link CsiRawMaterialDao}

 *
 */
public class CsiRawMaterialDaoImpl extends BaseDao<CsiRawMaterial> implements CsiRawMaterialDao {

	@Override
	protected Class<CsiRawMaterial> getDomainClass() {
		return CsiRawMaterial.class;
	}

	@Override
	public List<CsiRawMaterial> getRawMaterials(int cashSaleId, int finishedProductId) {
		DetachedCriteria rawMaterialDc = getDetachedCriteria();
		rawMaterialDc.add(Restrictions.eq(CsiRawMaterial.FIELD.cashSaleId.name(), cashSaleId));
		// Subquery to Finished Product
		DetachedCriteria finishedProdDc = DetachedCriteria.forClass(CsiFinishedProduct.class);
		finishedProdDc.setProjection(Projections.property(CsiFinishedProduct.FIELD.id.name()));
		finishedProdDc.add(Restrictions.eq(CsiFinishedProduct.FIELD.itemId.name(), finishedProductId));
		rawMaterialDc.add(Subqueries.propertyIn(CsiRawMaterial.FIELD.csiFinishedProdId.name(), finishedProdDc));
		return getAll(rawMaterialDc);
	}

	@Override
	public List<CsiRawMaterial> getCsRawMaterialItems(int cashSaleId, int itemId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(CsiRawMaterial.FIELD.cashSaleId.name(), cashSaleId));
		if (itemId > 0) {
			dc.add(Restrictions.eq(CsiRawMaterial.FIELD.itemId.name(), itemId));
		}
		return getAll(dc);
	}

}
