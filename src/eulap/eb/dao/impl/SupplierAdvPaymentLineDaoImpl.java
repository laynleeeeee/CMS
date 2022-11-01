package eulap.eb.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.SupplierAdvPaymentLineDao;
import eulap.eb.domain.hibernate.SupplierAdvancePaymentLine;

/**
 * DAO implementation class for {@link SupplierAdvPaymentLineDao}

 */

public class SupplierAdvPaymentLineDaoImpl extends BaseDao<SupplierAdvancePaymentLine> implements SupplierAdvPaymentLineDao {

	@Override
	protected Class<SupplierAdvancePaymentLine> getDomainClass() {
		return SupplierAdvancePaymentLine.class;
	}

	@Override
	public List<SupplierAdvancePaymentLine> getAdvancePaymentLines(Integer advPaymentId) {
		List<SupplierAdvancePaymentLine> advPaymentLines = new ArrayList<SupplierAdvancePaymentLine>();
		String sql = "SELECT SAPL.SUPPLIER_ADVANCE_PAYMENT_LINE_ID, SAPL.SUPPLIER_ADVANCE_PAYMENT_ID, "
				+ "SAPL.EB_OBJECT_ID, SAPL.AMOUNT, PO.EB_OBJECT_ID AS REFERENCE_OBJECT_ID, "
				+ "CONCAT('PO-', PO.PO_NUMBER, IF((PO.REMARKS != '' OR PO.REMARKS IS NOT NULL), "
				+ "CONCAT('; ', PO.REMARKS), ''), '; ', DATE(PO.PO_DATE)) AS REFERENCE_NO "
				+ "FROM SUPPLIER_ADVANCE_PAYMENT_LINE SAPL "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = SAPL.EB_OBJECT_ID "
				+ "INNER JOIN R_PURCHASE_ORDER PO ON PO.EB_OBJECT_ID = OTO.FROM_OBJECT_ID "
				+ "WHERE OTO.OR_TYPE_ID = 24000 "
				+ "AND SAPL.SUPPLIER_ADVANCE_PAYMENT_ID = ? "
				+ "UNION ALL "
				+ "SELECT SAPL.SUPPLIER_ADVANCE_PAYMENT_LINE_ID, SAPL.SUPPLIER_ADVANCE_PAYMENT_ID, "
				+ "SAPL.EB_OBJECT_ID, SAPL.AMOUNT, SAP.EB_OBJECT_ID AS REFERENCE_OBJECT_ID, "
				+ "CONCAT('SAP-', SAP.SEQUENCE_NO, IF((SAP.REMARKS != '' OR SAP.REMARKS IS NOT NULL), "
				+ "CONCAT('; ', SAP.REMARKS), ''), '; ', DATE(SAP.DATE)) AS REFERENCE_NO "
				+ "FROM SUPPLIER_ADVANCE_PAYMENT_LINE SAPL "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = SAPL.EB_OBJECT_ID "
				+ "INNER JOIN SUPPLIER_ADVANCE_PAYMENT SAP ON SAP.EB_OBJECT_ID = OTO.FROM_OBJECT_ID "
				+ "WHERE OTO.OR_TYPE_ID = 24000 "
				+ "AND SAPL.SUPPLIER_ADVANCE_PAYMENT_ID = ? ";
		Session session = null;
		SupplierAdvancePaymentLine advPaymentLine = null;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			Integer index = 0;
			int totalNoOfTables = 2;
			for (int i = 0; i < totalNoOfTables; i++) {
				query.setParameter(index, advPaymentId);
				if (i < (totalNoOfTables-1)) {
					++index;
				}
			}
			query.addScalar("SUPPLIER_ADVANCE_PAYMENT_LINE_ID", Hibernate.INTEGER);
			query.addScalar("SUPPLIER_ADVANCE_PAYMENT_ID", Hibernate.INTEGER);
			query.addScalar("EB_OBJECT_ID", Hibernate.INTEGER);
			query.addScalar("AMOUNT", Hibernate.DOUBLE);
			query.addScalar("REFERENCE_OBJECT_ID", Hibernate.INTEGER);
			query.addScalar("REFERENCE_NO", Hibernate.STRING);
			List<Object[]> list = query.list();
			if (list != null && !list.isEmpty()) {
				for (Object[] row : list) {
					advPaymentLine = new SupplierAdvancePaymentLine();
					advPaymentLine.setId((Integer) row[0]);
					advPaymentLine.setSupplierAdvPaymentId((Integer) row[1]);
					advPaymentLine.setEbObjectId((Integer) row[2]);
					advPaymentLine.setAmount((Double) row[3]);
					advPaymentLine.setReferenceObjectId((Integer) row[4]);
					advPaymentLine.setReferenceNo((String) row[5]);
					advPaymentLines.add(advPaymentLine);
					// Free up memory
					advPaymentLine = null;
				}
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return advPaymentLines;
	}
}
