package eulap.eb.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.FleetCaptainMdmDao;
import eulap.eb.domain.hibernate.FleetCaptainMdm;

/**
 * Implementation class {@link FleetCaptainMdmDao}

 *
 */
public class FleetCaptainMdmDaoImpl extends BaseDao<FleetCaptainMdm> implements FleetCaptainMdmDao{

	@Override
	protected Class<FleetCaptainMdm> getDomainClass() {
		return FleetCaptainMdm.class;
	}

	@Override
	public List<FleetCaptainMdm> getFleetCaptainMdms(Integer refObjectId, Boolean isActiveOnly) {
		String sql = "SELECT FCM.FLEET_CAPTAIN_MDM_ID, FCM.DATE, FCM.NAME, FCM.POSITION, FCM.EB_OBJECT_ID, "
				+ "FCM.LICENSE_NO, FCM.LN_EXPIRATION_DATE, FCM.SEAMANS_BOOK, FCM.SB_EXPIRATION_DATE, FCM.FISHERIES, FCM.FL_EXPIRATION_DATE, "
				+ "FCM.PASSPORT, FCM.P_EXPIRATION_DATE, FCM.REMARKS, OTO.OR_TYPE_ID, FCM.CREATED_BY, FCM.UPDATED_BY "
				+ "FROM FLEET_CAPTAIN_MDM FCM "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = FCM.EB_OBJECT_ID "
				+ "WHERE OTO.FROM_OBJECT_ID = ? "
				+ "AND FCM.ACTIVE = ? ";
		return new ArrayList<FleetCaptainMdm>(get(sql, new FleetCaptainMdmsHandler(refObjectId, isActiveOnly)));
	}

	private static class FleetCaptainMdmsHandler implements QueryResultHandler<FleetCaptainMdm>{

		private final Integer refObjectId;
		private final Boolean isActiveOnly;
	
		private FleetCaptainMdmsHandler(final Integer refObjectId, final Boolean isActiveOnly) {
			this.refObjectId = refObjectId;
			this.isActiveOnly = isActiveOnly;
		}
		@Override
		public List<FleetCaptainMdm> convert(List<Object[]> queryResult) {
			List<FleetCaptainMdm> fleetCaptainMdms = new ArrayList<>();
			FleetCaptainMdm fleetCaptainMdm = null;
			for(Object[] row : queryResult) {
				int index = 0;
				fleetCaptainMdm = new FleetCaptainMdm();
				fleetCaptainMdm.setId((int) row[index++]);
				fleetCaptainMdm.setDate((Date) row[index++]);
				fleetCaptainMdm.setName((String) row[index++]);
				fleetCaptainMdm.setPosition((String) row[index++]);
				fleetCaptainMdm.setEbObjectId((Integer) row[index++]);
				fleetCaptainMdm.setLicenseNo((String) row[index++]);
				fleetCaptainMdm.setLicenseNoExpirationDate((Date) row[index++]);
				fleetCaptainMdm.setSeamansBook((String) row[index++]);
				fleetCaptainMdm.setSeamansBookExpirationDate((Date) row[index++]);
				fleetCaptainMdm.setFisheries((String) row[index++]);
				fleetCaptainMdm.setFishesriesExpirationDate((Date) row[index++]);
				fleetCaptainMdm.setPassport((String) row[index++]);
				fleetCaptainMdm.setPassportExpirationDate((Date) row[index++]);
				fleetCaptainMdm.setRemarks((String) row[index++]);
				fleetCaptainMdm.setOrTypeId((Integer) row[index++]);
				fleetCaptainMdm.setCreatedBy((int) row[index++]);
				fleetCaptainMdm.setUpdatedBy((int) row[index++]);
				fleetCaptainMdms.add(fleetCaptainMdm);
			}
			return fleetCaptainMdms;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			query.setParameter(index++, refObjectId);
			query.setParameter(index++, isActiveOnly);
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("FLEET_CAPTAIN_MDM_ID", Hibernate.INTEGER);
			query.addScalar("DATE", Hibernate.DATE);
			query.addScalar("NAME", Hibernate.STRING);
			query.addScalar("POSITION", Hibernate.STRING);
			query.addScalar("EB_OBJECT_ID", Hibernate.INTEGER);
			query.addScalar("LICENSE_NO", Hibernate.STRING);
			query.addScalar("LN_EXPIRATION_DATE", Hibernate.DATE);
			query.addScalar("SEAMANS_BOOK", Hibernate.STRING);
			query.addScalar("SB_EXPIRATION_DATE", Hibernate.DATE);
			query.addScalar("FISHERIES", Hibernate.STRING);
			query.addScalar("FL_EXPIRATION_DATE", Hibernate.DATE);
			query.addScalar("PASSPORT", Hibernate.STRING);
			query.addScalar("P_EXPIRATION_DATE", Hibernate.DATE);
			query.addScalar("REMARKS", Hibernate.STRING);
			query.addScalar("OR_TYPE_ID", Hibernate.INTEGER);
			query.addScalar("CREATED_BY", Hibernate.INTEGER);
			query.addScalar("UPDATED_BY", Hibernate.INTEGER);
		}
	}

}
