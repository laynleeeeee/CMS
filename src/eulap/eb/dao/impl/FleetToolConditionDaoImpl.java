package eulap.eb.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.FleetToolConditionDao;
import eulap.eb.domain.hibernate.FleetToolCondition;
import eulap.eb.domain.hibernate.ObjectToObject;

/**
 * Implementation class of {@link FleetToolCondition}

 *
 */
public class FleetToolConditionDaoImpl extends BaseDao<FleetToolCondition> implements FleetToolConditionDao{

	@Override
	protected Class<FleetToolCondition> getDomainClass() {
		return FleetToolCondition.class;
	}

	@Override
	public FleetToolCondition getByWSItem(int wsiEbObjectId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(FleetToolCondition.FIELD.active.name(), true));

		DetachedCriteria ooDc = DetachedCriteria.forClass(ObjectToObject.class);
		ooDc.setProjection(Projections.property(ObjectToObject.FIELDS.toObjectId.name()));
		ooDc.add(Restrictions.eq(ObjectToObject.FIELDS.fromObjectId.name(), wsiEbObjectId));
		ooDc.add(Restrictions.eq(ObjectToObject.FIELDS.orTypeId.name(), FleetToolCondition.OR_TYPE_ID));

		dc.add(Subqueries.propertyIn(FleetToolCondition.FIELD.ebObjectId.name(), ooDc));
		return get(dc);
	}


	@Override
	public List<FleetToolCondition> getByRefObject(Integer refObjectId) {
		String sql = "SELECT FTC.FLEET_TOOL_CONDITION_ID, FTC.ITEM_ID, FTC.EB_OBJECT_ID, FTC.STATUS, FTC.ACTIVE, WSI.EB_OBJECT_ID AS REF_EB_OBJECT_ID, "
				+ "FTC.CREATED_BY, FTC.CREATED_DATE "
				+ "FROM FLEET_TOOL_CONDITION FTC "
				+ "INNER JOIN OBJECT_TO_OBJECT FTC_OTO ON FTC_OTO.TO_OBJECT_ID = FTC.EB_OBJECT_ID "
				+ "INNER JOIN FLEET_PROFILE FP ON FP.EB_OBJECT_ID = FTC_OTO.FROM_OBJECT_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT WSI_OTO ON WSI_OTO.TO_OBJECT_ID = FTC.EB_OBJECT_ID "
				+ "INNER JOIN WITHDRAWAL_SLIP_ITEM WSI ON WSI.EB_OBJECT_ID = WSI_OTO.FROM_OBJECT_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT WS_OTO ON WS_OTO.TO_OBJECT_ID = WSI.EB_OBJECT_ID "
				+ "INNER JOIN WITHDRAWAL_SLIP WS ON WS.EB_OBJECT_ID = WS_OTO.FROM_OBJECT_ID "
				+ "WHERE FTC.ACTIVE = 1 "
				+ "AND FP.EB_OBJECT_ID = ? "
				+ "ORDER BY WSI.WITHDRAWAL_SLIP_ITEM_ID, WS.DATE DESC";
		return (List<FleetToolCondition>) get(sql, new FleetToolConditionHandler(refObjectId));
	}

	private static class FleetToolConditionHandler implements QueryResultHandler<FleetToolCondition>{

		private final Integer refObjectId;

		private FleetToolConditionHandler(final Integer refObjectId) {
			this.refObjectId = refObjectId;
		}

		@Override
		public List<FleetToolCondition> convert(List<Object[]> queryResult) {
			List<FleetToolCondition> fleetToolConditions = new ArrayList<>();
			FleetToolCondition ftc = null;
			for(Object[] row : queryResult) {
				int index = 0;
				ftc = new FleetToolCondition();
				ftc.setId((int) row[index++]);
				ftc.setItemId((Integer) row[index++]);
				ftc.setEbObjectId((Integer) row[index++]);
				ftc.setStatus((Integer) row[index++] == 1);
				ftc.setActive((Integer) row[index++] == 1);
				ftc.setRefObjectId((Integer) row[index++]);
				ftc.setCreatedBy((int) row[index++]);
				ftc.setCreatedDate((Date) row[index]);
				fleetToolConditions.add(ftc);
			}
			return fleetToolConditions;
		}

		@Override
		public int setParamater(SQLQuery query) {
			query.setParameter(0, refObjectId);
			return 0;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("FLEET_TOOL_CONDITION_ID", Hibernate.INTEGER);
			query.addScalar("ITEM_ID", Hibernate.INTEGER);
			query.addScalar("EB_OBJECT_ID", Hibernate.INTEGER);
			query.addScalar("STATUS", Hibernate.INTEGER);
			query.addScalar("ACTIVE", Hibernate.INTEGER);
			query.addScalar("REF_EB_OBJECT_ID", Hibernate.INTEGER);
			query.addScalar("CREATED_BY", Hibernate.INTEGER);
			query.addScalar("CREATED_DATE", Hibernate.DATE);
		}
	}

}
