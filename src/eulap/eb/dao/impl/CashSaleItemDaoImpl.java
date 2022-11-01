package eulap.eb.dao.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.CashSaleItemDao;
import eulap.eb.domain.hibernate.CashSale;
import eulap.eb.domain.hibernate.CashSaleItem;
import eulap.eb.domain.hibernate.CashSaleReturn;
import eulap.eb.domain.hibernate.CashSaleReturnItem;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.web.dto.CSItemDto;

/**
 * Implementing class of {@link CashSaleItemDao}

 *
 */
public class CashSaleItemDaoImpl extends BaseDao<CashSaleItem> implements CashSaleItemDao{

	@Override
	protected Class<CashSaleItem> getDomainClass() {
		return CashSaleItem.class;
	}

	@Override
	public List<CashSaleItem> getCashSaleItems(Integer cashSalesId, Integer itemId, Integer warehouseId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(CashSaleItem.FIELD.cashSaleId.name(), cashSalesId));
		if(itemId != null) {
			dc.add(Restrictions.eq(CashSaleItem.FIELD.itemId.name(), itemId));
		}
		if(warehouseId != null) {
			dc.add(Restrictions.eq(CashSaleItem.FIELD.warehouseId.name(), warehouseId));
		}
		return getAll(dc);
	}

	@Override
	public double getTotalCSAmount(Integer cashSaleId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(CashSaleItem.FIELD.cashSaleId.name(), cashSaleId));
		dc.setProjection(Projections.sum(CashSaleItem.FIELD.amount.name()));
		return getBySumProjection(dc);
	}

	@Override
	public List<CashSaleItem> getCSItemsWithLimit(Integer cashSaleId,
			int maxResult) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(CashSaleItem.FIELD.cashSaleId.name(), cashSaleId));
		dc.getExecutableCriteria(getSession()).setMaxResults(maxResult);
		return getAll(dc);
	}

	@Override
	public int getCSItemSize(Integer cashSaleId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(CashSaleItem.FIELD.cashSaleId.name(), cashSaleId));
		dc.setProjection(Projections.count(CashSaleItem.FIELD.id.name()));
		List<Object> ret = getByProjection(dc);
		if (ret != null && ret.size() > 0) {
			Object retObj = ret.iterator().next();
			if (retObj == null)
				return 0;
			return (Integer) retObj;
		}
		return 0;
	}

	@Override
	public boolean isReferenceId(List<Integer> cashSalesItemIds) {
		DetachedCriteria csItemDc = getDetachedCriteria();
		DetachedCriteria csrItemDc = DetachedCriteria.forClass(CashSaleReturnItem.class);
		csrItemDc.setProjection(Projections.property(CashSaleReturnItem.FIELD.cashSaleItemId.name()));
		DetachedCriteria csReturnDc = DetachedCriteria.forClass(CashSaleReturn.class);
		csReturnDc.setProjection(Projections.property(CashSaleReturn.FIELD.id.name()));
		csReturnDc.createAlias("formWorkflow", "fw");
		csReturnDc.add(Restrictions.ne("fw.currentStatusId", FormStatus.CANCELLED_ID));
		csrItemDc.add(Subqueries.propertyIn(CashSaleReturnItem.FIELD.cashSaleReturnId.name(), csReturnDc));
		for (Integer id : cashSalesItemIds) {
			//loop through all the ids.
			csrItemDc.add(Restrictions.eq(CashSaleReturnItem.FIELD.cashSaleItemId.name(), id));
		}
		csItemDc.add(Subqueries.propertyIn(CashSaleItem.FIELD.id.name(), csrItemDc));
		return !getAll(csItemDc).isEmpty();
	}
		
	public boolean hasDuplicate(int cashSaleId) {
		List<Object> checkDuplicate = executeSP("CHECK_HAS_DUPLICATE", cashSaleId);
		if (checkDuplicate != null) {
			for (Object obj : checkDuplicate) {
				// Type casting
				BigInteger bi = (BigInteger) obj;
				if (bi.intValue() == 1)
					return true;
			}
		}
		return false;
	}

	@Override
	public List<CSItemDto> getCsItemDtos(int cashSaleId) {
		List<CSItemDto> csItemDtos = new ArrayList<CSItemDto>();
		List<Object> csiList = executeSP("GET_DISTINCT_CS_ITEMS", cashSaleId);
		if (csiList != null && !csiList.isEmpty()) {
			for (Object obj : csiList) {
				// Type casting
				Object[] row = (Object[]) obj;
				int colNum = 0;
				Integer itemId = (Integer)row[colNum++]; //0
				Integer warehouseId = (Integer)row[colNum++]; //1
				Double quantity = (Double)row[colNum++]; //2
				Double unitCost = (Double)row[colNum++]; //3
				CSItemDto csItemDto = CSItemDto.getInstanceOf(itemId, warehouseId, quantity, unitCost);
				csItemDtos.add(csItemDto);
			}
			return csItemDtos;
		}
		return null;
	}

	@Override
	public double getTotalAmountCSIByDate(Date date) {
		DetachedCriteria dc = getDetachedCriteria();
		DetachedCriteria csDc = DetachedCriteria.forClass(CashSale.class);
		csDc.setProjection(Projections.property(CashSale.FIELD.id.name()));
		csDc.createAlias("formWorkflow", "fw");
		csDc.add(Restrictions.eq(CashSale.FIELD.receiptDate.name(), date));
		csDc.add(Restrictions.ne("fw.currentStatusId", FormStatus.CANCELLED_ID));
		dc.add(Subqueries.propertyIn(CashSaleItem.FIELD.cashSaleId.name(), csDc));
		dc.setProjection(Projections.sum(CashSaleItem.FIELD.amount.name()));
		return getBySumProjection(dc);
	}

	@Override
	public double getTotalCSIAmtWithTax(int cashSaleId) {
		double totalAmount = 0;
		String sql = "SELECT CSI.CASH_SALE_ID, SUM(CSI.QUANTITY * CSI.SRP) AS TOTAL_AMOUNT  "
				+ "FROM CASH_SALE_ITEM CSI "
				+ "WHERE CSI.CASH_SALE_ID = ?";
		Session session = null;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(0, cashSaleId);

			List<Object[]> list = query.list();
			if (list != null && !list.isEmpty()) {
				for (Object[] row : list) {
					totalAmount = (Double) row[1];
					break; // expecting 1 line only
				}
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return totalAmount;
	}
}
