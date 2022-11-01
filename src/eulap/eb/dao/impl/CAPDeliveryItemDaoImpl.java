package eulap.eb.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.CAPDeliveryItemDao;
import eulap.eb.domain.hibernate.CAPDelivery;
import eulap.eb.domain.hibernate.CAPDeliveryItem;
import eulap.eb.domain.hibernate.FormStatus;

/**
 * DAO Implementation layer of {@link CAPDeliveryItemDao}

 *
 */
public class CAPDeliveryItemDaoImpl extends BaseDao<CAPDeliveryItem> implements CAPDeliveryItemDao {

	@Override
	protected Class<CAPDeliveryItem> getDomainClass() {
		return CAPDeliveryItem.class;
	}

	@Override
	public List<CAPDeliveryItem> getDeliveryItems(int capDeliveryId, int itemId, int warehouseId) {
		DetachedCriteria deliveryItemDc = getDetachedCriteria();
		deliveryItemDc.add(Restrictions.eq(CAPDeliveryItem.FIELD.capDeliveryId.name(), capDeliveryId));
		deliveryItemDc.add(Restrictions.eq(CAPDeliveryItem.FIELD.itemId.name(), itemId));
		deliveryItemDc.add(Restrictions.eq(CAPDeliveryItem.FIELD.warehouseId.name(), warehouseId));
		return getAll(deliveryItemDc);
	}

	public List<CAPDeliveryItem> getDeliveryItemsByCapId(int customerAdvPaymentId) {
		DetachedCriteria deliveryItemDc = getDetachedCriteria();
		DetachedCriteria capDeliveryDc = DetachedCriteria.forClass(CAPDelivery.class);
		capDeliveryDc.setProjection(Projections.property(CAPDelivery.FIELD.id.name()));
		capDeliveryDc.createAlias("formWorkflow", "fw");
		capDeliveryDc.add(Restrictions.ne("fw.currentStatusId", FormStatus.CANCELLED_ID));
		capDeliveryDc.add(Restrictions.eq(CAPDelivery.FIELD.customerAdvancePaymentId.name(), customerAdvPaymentId));
		deliveryItemDc.add(Subqueries.propertyIn(CAPDeliveryItem.FIELD.capDeliveryId.name(), capDeliveryDc));
		return getAll(deliveryItemDc);
	}

	@Override
	public double getTotalDeliveredAmt(int capDeliveryId, int customerAdvPaymentId) {
		StringBuffer sql = new StringBuffer("SELECT ID, COALESCE(SUM(AMOUNT), 0) AS AMOUNT FROM ("
					+ "SELECT CAPD.CAP_DELIVERY_ID AS ID, CAPDI.AMOUNT FROM CAP_DELIVERY_ITEM CAPDI "
					+ "INNER JOIN CAP_DELIVERY CAPD ON CAPD.CAP_DELIVERY_ID = CAPDI.CAP_DELIVERY_ID "
					+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = CAPD.FORM_WORKFLOW_ID "
					+ "WHERE CAPD.CUSTOMER_ADVANCE_PAYMENT_ID = ? AND FW.CURRENT_STATUS_ID != 4 ");
		if(capDeliveryId != 0) {
			sql.append("AND CAPD.CAP_DELIVERY_ID != ? ");
		}
		sql.append("UNION ALL "
					+ "SELECT CAPD.CAP_DELIVERY_ID AS ID, OC.AMOUNT FROM CAP_DELIVERY_AR_LINE OC "
					+ "INNER JOIN CAP_DELIVERY CAPD ON CAPD.CAP_DELIVERY_ID = OC.CAP_DELIVERY_ID "
					+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = CAPD.FORM_WORKFLOW_ID "
					+ "WHERE CAPD.CUSTOMER_ADVANCE_PAYMENT_ID = ? AND FW.CURRENT_STATUS_ID != 4 ");
		if(capDeliveryId != 0) {
			sql.append("AND CAPD.CAP_DELIVERY_ID != ? ");
		}
		sql.append(") AS TOTAL_SALES ");
		List<Double> totalAmt = getTotalAmt(sql.toString(), capDeliveryId, customerAdvPaymentId);
		return totalAmt.get(0);
	}

	private List<Double> getTotalAmt(String sql, final int capDeliveryId, final int customerAdvPaymentId) {
		Collection<Double> capDeliveryAmt = get(sql, new QueryResultHandler<Double>() {

			@Override
			public List<Double> convert(List<Object[]> queryResult) {
				List<Double> ret = new ArrayList<Double>();
				for (Object[] row : queryResult) {
					ret.add((Double) row[1]); //Expecting only 1 row.
					break;
				}
				return ret;
			}

			@Override
			public int setParamater(SQLQuery query) {
				int index = 0;
				query.setParameter(index, customerAdvPaymentId);
				if(capDeliveryId != 0) {
					query.setParameter(++index, capDeliveryId);
				}
				query.setParameter(++index, customerAdvPaymentId);
				if(capDeliveryId != 0) {
					query.setParameter(++index, capDeliveryId);
				}
				return index;
			}

			@Override
			public void setScalars(SQLQuery query) {
				query.addScalar("ID", Hibernate.INTEGER);
				query.addScalar("AMOUNT", Hibernate.DOUBLE);
			}
		});

		return (List<Double>) capDeliveryAmt;
	}
}
