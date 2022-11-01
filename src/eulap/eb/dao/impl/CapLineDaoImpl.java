package eulap.eb.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.CapLineDao;
import eulap.eb.domain.hibernate.CustomerAdvancePaymentLine;

/**
 * Implementing class of {@link CapLineDao}

 *
 */
public class CapLineDaoImpl extends BaseDao<CustomerAdvancePaymentLine> implements CapLineDao{

	@Override
	protected Class<CustomerAdvancePaymentLine> getDomainClass() {
		return CustomerAdvancePaymentLine.class;
	}

	@Override
	public List<CustomerAdvancePaymentLine> getCapLines(Integer capId) {
		List<CustomerAdvancePaymentLine> capLines = new ArrayList<>();
		String sql = "SELECT CAPL.CUSTOMER_ADVANCE_PAYMENT_LINE_ID, CAPL.CUSTOMER_ADVANCE_PAYMENT_ID, CAPL.EB_OBJECT_ID, "
				+ "CAPL.AMOUNT, SO.EB_OBJECT_ID AS REFERENCE_OBJECT_ID, CONCAT('SO-', SO.SEQUENCE_NO, ' ', COALESCE(SO.REMARKS, ''), ' ',  SO.DATE) AS REFERENCE_NO  "
				+ "FROM CUSTOMER_ADVANCE_PAYMENT_LINE CAPL "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = CAPL.EB_OBJECT_ID "
				+ "INNER JOIN SALES_ORDER SO ON SO.EB_OBJECT_ID = OTO.FROM_OBJECT_ID "
				+ "WHERE OTO.OR_TYPE_ID = 12003 "
				+ "AND CAPL.CUSTOMER_ADVANCE_PAYMENT_ID = ? "
				+ "UNION ALL "
				+ "SELECT CAPL.CUSTOMER_ADVANCE_PAYMENT_LINE_ID, CAPL.CUSTOMER_ADVANCE_PAYMENT_ID, CAPL.EB_OBJECT_ID, "
				+ "CAPL.AMOUNT, CAP.EB_OBJECT_ID AS REFERENCE_OBJECT_ID, CONCAT('CAP-', CAP.CAP_NUMBER, ' ',  CAP.RECEIPT_DATE) AS REFERENCE_NO "
				+ "FROM CUSTOMER_ADVANCE_PAYMENT_LINE CAPL "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = CAPL.EB_OBJECT_ID "
				+ "INNER JOIN CUSTOMER_ADVANCE_PAYMENT CAP ON CAP.EB_OBJECT_ID = OTO.FROM_OBJECT_ID "
				+ "WHERE OTO.OR_TYPE_ID = 12003 "
				+ "AND CAPL.CUSTOMER_ADVANCE_PAYMENT_ID = ?";
		Session session = null;
		CustomerAdvancePaymentLine capLine = null;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			Integer index = 0;
			int totalNoOfTables = 2;
			for (int i = 0; i < totalNoOfTables; i++) {
				query.setParameter(index, capId);
				if (i < (totalNoOfTables-1)) {
					++index;
				}
			}
			query.addScalar("CUSTOMER_ADVANCE_PAYMENT_LINE_ID", Hibernate.INTEGER);
			query.addScalar("CUSTOMER_ADVANCE_PAYMENT_ID", Hibernate.INTEGER);
			query.addScalar("EB_OBJECT_ID", Hibernate.INTEGER);
			query.addScalar("AMOUNT", Hibernate.DOUBLE);
			query.addScalar("REFERENCE_OBJECT_ID", Hibernate.INTEGER);
			query.addScalar("REFERENCE_NO", Hibernate.STRING);
			List<Object[]> list = query.list();
			if (list != null && !list.isEmpty()) {
				for (Object[] row : list) {
					capLine = new CustomerAdvancePaymentLine();
					capLine.setId((Integer) row[0]);
					capLine.setCustomerAdvPaymentId((Integer) row[1]);
					capLine.setEbObjectId((Integer) row[2]);
					capLine.setAmount((Double) row[3]);
					capLine.setReferenceObjectId((Integer) row[4]);
					capLine.setReferenceNo((String) row[5]);
					capLines.add(capLine);
					// Free up memory
					capLine = null;
				}
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return capLines;
	}
}
