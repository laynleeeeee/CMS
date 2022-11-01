package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import eulap.common.dao.BaseDao;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.AuthorityToWithdrawItemDao;
import eulap.eb.domain.hibernate.AuthorityToWithdrawItem;
import eulap.eb.domain.hibernate.Warehouse;
import eulap.eb.web.dto.AtwItemDto;

/**
 * DAO implementation class for {@link AuthorityToWithdrawItemDao}

 */

public class AuthorityToWithdrawItemDaoImpl extends BaseDao<AuthorityToWithdrawItem> implements AuthorityToWithdrawItemDao {

	@Override
	protected Class<AuthorityToWithdrawItem> getDomainClass() {
		return AuthorityToWithdrawItem.class;
	}

	@Override
	public AtwItemDto getAtwItemByRefItemObjectId(Integer refItemObjectId) {
		String sql = "SELECT ATWI.AUTHORITY_TO_WITHDRAW_ITEM_ID, ATWI.ITEM_ID, COALESCE(SOI.GROSS_AMOUNT, 0) AS GROSS_AMOUNT, "
				+ "COALESCE(SOI.DISCOUNT, 0) AS DISCOUNT, SOI.TAX_TYPE_ID, COALESCE(SOI.VAT_AMOUNT, 0) AS VAT_AMOUNT, "
				+ "SOI.ITEM_DISCOUNT_TYPE_ID, COALESCE(SOI.DISCOUNT_VALUE, 0) AS DISCOUNT_VALUE "
				+ "FROM AUTHORITY_TO_WITHDRAW_ITEM ATWI "
				+ "INNER JOIN AUTHORITY_TO_WITHDRAW ATW ON ATW.AUTHORITY_TO_WITHDRAW_ID = ATWI.AUTHORITY_TO_WITHDRAW_ID "
				+ "INNER JOIN SALES_ORDER SO ON SO.SALES_ORDER_ID = ATW.SALES_ORDER_ID "
				+ "INNER JOIN SALES_ORDER_ITEM SOI ON SOI.SALES_ORDER_ID = SO.SALES_ORDER_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.FROM_OBJECT_ID = SOI.EB_OBJECT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ATW.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND OTO.OR_TYPE_ID = 12001 "
				+ "AND OTO.TO_OBJECT_ID = ? "
				+ "AND ATWI.EB_OBJECT_ID = OTO.TO_OBJECT_ID ";
		Session session = null;
		AtwItemDto atw = null;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(0, refItemObjectId);
			query.addScalar("AUTHORITY_TO_WITHDRAW_ITEM_ID", Hibernate.INTEGER);
			query.addScalar("ITEM_ID", Hibernate.INTEGER);
			query.addScalar("GROSS_AMOUNT", Hibernate.DOUBLE);
			query.addScalar("DISCOUNT", Hibernate.DOUBLE);
			query.addScalar("TAX_TYPE_ID", Hibernate.INTEGER);
			query.addScalar("VAT_AMOUNT", Hibernate.DOUBLE);
			query.addScalar("ITEM_DISCOUNT_TYPE_ID", Hibernate.INTEGER);
			query.addScalar("DISCOUNT_VALUE", Hibernate.DOUBLE);
			List<Object[]> list = query.list();
			if (list != null && !list.isEmpty()) {
				for (Object[] row : list) {
					atw = new AtwItemDto();
					atw.setAtwItemId((Integer) row[0]);
					atw.setItemId((Integer) row[1]);
					atw.setSrp((Double) row[2]);
					atw.setDiscount((Double) row[3]);
					atw.setTaxTypeId((Integer) row[4]);
					atw.setVatAmount((Double) row[5]);
					atw.setItemDiscountTypeId((Integer) row[6]);
					atw.setDiscountValue((Double) row[7]);
					break; // expecting single row only
				}
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return atw;
	}

	@Override
	public double getRemainingQty(Integer authorityToWithdrawItemId, Integer referenceObjectId) {
		String sql = "SELECT SUM(SOI_QTY - AWI_QTY), ID FROM ( "
				+ "SELECT SOI.SALES_ORDER_ITEM_ID AS ID, "
				+ " SOI.QUANTITY AS SOI_QTY, 0 AS AWI_QTY FROM SALES_ORDER_ITEM SOI "
				+ "WHERE SOI.EB_OBJECT_ID = ? "
				+ "UNION ALL "
				+ "SELECT AWI.AUTHORITY_TO_WITHDRAW_ITEM_ID AS ID, "
				+ "0 AS SOI_QTY, SUM(COALESCE(AWI.QUANTITY, 0)) AS AWI_QTY FROM AUTHORITY_TO_WITHDRAW_ITEM AWI "
				+ "INNER JOIN AUTHORITY_TO_WITHDRAW AW ON AW.AUTHORITY_TO_WITHDRAW_ID = AWI.AUTHORITY_TO_WITHDRAW_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = AWI.EB_OBJECT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AW.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND OTO.FROM_OBJECT_ID = ? ";
				if(authorityToWithdrawItemId != 0) {
					sql += "AND AWI.AUTHORITY_TO_WITHDRAW_ITEM_ID != ? ";
				}
				sql += ") AS TBL ";
		Session session = null;
		Double remainingQty = 0.0;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(0, referenceObjectId);
			query.setParameter(1, referenceObjectId);
			if(authorityToWithdrawItemId != 0) {
				query.setParameter(2, authorityToWithdrawItemId);
			}
			List<Object[]> list = query.list();
			if(list != null && !list.isEmpty()) {
				for (Object[] row : list) {
					if(row != null && (Double) row[0] != null) {
						remainingQty = (Double) row[0];
					}
					break; //Expecting single result.
				}
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return remainingQty;
	}
}
