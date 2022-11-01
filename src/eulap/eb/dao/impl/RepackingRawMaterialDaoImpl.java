package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.RepackingRawMaterialDao;
import eulap.eb.domain.hibernate.RepackingRawMaterial;

/**
 * DAO implementation class for {@link RepackingRawMaterialDao}

 */

public class RepackingRawMaterialDaoImpl extends BaseDao<RepackingRawMaterial> implements RepackingRawMaterialDao {

	@Override
	protected Class<RepackingRawMaterial> getDomainClass() {
		return RepackingRawMaterial.class;
	}

	@Override
	public List<RepackingRawMaterial> getRawMaterialItems(int repackingId, int itemId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(RepackingRawMaterial.FIELD.repackingId.name(), repackingId));
		dc.add(Restrictions.eq(RepackingRawMaterial.FIELD.itemId.name(), itemId));
		return getAll(dc);
	}

}
