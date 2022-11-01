package eulap.eb.dao.impl;

import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.springframework.orm.hibernate3.HibernateCallback;

import eulap.common.dao.BaseDao;
import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.CashSaleDao;
import eulap.eb.domain.hibernate.ArCustomer;
import eulap.eb.domain.hibernate.CashSale;
import eulap.eb.domain.hibernate.CashSaleItem;
import eulap.eb.domain.hibernate.CashSaleReturn;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.DailyCashCollection;

/**
 * Implementing class of {@link CashSaleDao}

 *
 */
public class CashSaleDaoImpl extends BaseDao<CashSale> implements CashSaleDao{

	@Override
	protected Class<CashSale> getDomainClass() {
		return CashSale.class;
	}

	@Override
	public Integer generateCsNumber(Integer companyId, Integer cashSaleTypeId) {
		DetachedCriteria cashSaleDC = getDetachedCriteria();
		cashSaleDC.setProjection(Projections.max(CashSale.FIELD.csNumber.name()));
		if(companyId != null) {
			cashSaleDC.add(Restrictions.eq(CashSale.FIELD.companyId.name(), companyId));
		}

		if(cashSaleTypeId != null) {
			cashSaleDC.add(Restrictions.eq(CashSale.FIELD.cashSaleTypeId.name(), cashSaleTypeId));
		}

		return generateSeqNo(cashSaleDC);
	}

	@Override
	public Page<CashSale> searchCashSales(String criteria, int typeId, PageSetting pageSetting) {
		DetachedCriteria csCriteria = getDetachedCriteria();
		if(StringFormatUtil.isNumeric(criteria)) {
			csCriteria.add(Restrictions.sqlRestriction("CS_NUMBER LIKE ?", criteria.trim(), Hibernate.STRING));
		}
		csCriteria.add(Restrictions.eq(CashSale.FIELD.cashSaleTypeId.name(), typeId));
		csCriteria.addOrder(Order.asc(CashSale.FIELD.csNumber.name()));
		return getAll(csCriteria, pageSetting);
	}

	@Override
	public Page<CashSale> getCashSales(final ApprovalSearchParam searchParam, final List<Integer> formStatusIds,
			final Integer typeId, final PageSetting pageSetting) {
		HibernateCallback<Page<CashSale>> hibernateCallback = new HibernateCallback<Page<CashSale>>() {

			@Override
			public Page<CashSale> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria dc = session.createCriteria(CashSale.class);
				dc.add(Restrictions.eq(CashSale.FIELD.cashSaleTypeId.name(), typeId));
				SearchCommonUtil.searchCommonParams(dc, null, "companyId",
						CashSale.FIELD.receiptDate.name(), CashSale.FIELD.receiptDate.name(),
						CashSale.FIELD.receiptDate.name(), searchParam.getUser().getCompanyIds(), searchParam);
				dc.add(Restrictions.or(Restrictions.like(CashSale.FIELD.salesInvoiceNo.name(), "%" + searchParam.getSearchCriteria().trim() + "%"),
						Restrictions.sqlRestriction("CS_NUMBER LIKE ?", searchParam.getSearchCriteria().trim(), Hibernate.STRING)));
				// Workflow status
				DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
				if (formStatusIds.size() > 0)
					addAsOrInCritiria(dcWorkflow, FormWorkflow.FIELD.currentStatusId.name(), formStatusIds);
				dcWorkflow.addOrder(Order.asc(FormWorkflow.FIELD.currentStatusId.name()));
				dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
				dc.add(Subqueries.propertyIn(CashSale.FIELD.formWorkflowId.name(), dcWorkflow));
				dc.addOrder(Order.desc(CashSale.FIELD.receiptDate.name()));
				dc.addOrder(Order.desc(CashSale.FIELD.createdDate.name()));
				return getAll(dc, pageSetting);
			}
		};
		return getHibernateTemplate().execute(hibernateCallback);
	}

	@Override
	public List<CashSale> getCashSales(Integer companyId,
			String salesInvoiceNo, String customerName, Date date) {
		DetachedCriteria dc = getDetachedCriteria();
		if (companyId != null)
			dc.add(Restrictions.eq(CashSale.FIELD.companyId.name(), companyId));
		if (salesInvoiceNo != null && !salesInvoiceNo.trim().isEmpty())
			dc.add(Restrictions.like(CashSale.FIELD.salesInvoiceNo.name(), "%" + salesInvoiceNo.trim() + "%"));
		if (customerName != null && !customerName.trim().isEmpty()) {
			DetachedCriteria custDc = DetachedCriteria.forClass(ArCustomer.class);
			custDc.setProjection(Projections.property(ArCustomer.FIELD.id.name()));
			custDc.add(Restrictions.eq(ArCustomer.FIELD.name.name(), customerName.trim()));
			dc.add(Subqueries.propertyIn(CashSale.FIELD.arCustomerId.name(), custDc));
		}
		if (date != null)
			dc.add(Restrictions.eq(CashSale.FIELD.receiptDate.name(), date));
		dc.addOrder(Order.asc(CashSale.FIELD.csNumber.name()));
		
		// Cash sale workflow subquery. 
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		dcWorkflow.add(Restrictions.eq(FormWorkflow.FIELD.complete.name(), true));
		dc.add(Subqueries.propertyIn(CashSale.FIELD.formWorkflowId.name(), dcWorkflow));
		return getAll(dc);
	}

	@Override
	public List<DailyCashCollection> getDailyCashCollections(Integer companyId,
			Integer userId, Date date, String orderType, Integer status) {
		List<DailyCashCollection> dailyCashCollections = new ArrayList<DailyCashCollection>();
		List<Object> dcCollections = executeSP("GET_DAILY_CASH_COLLECTION", companyId, userId, date, orderType, status);
		if (dcCollections != null && !dcCollections.isEmpty()) {
			for (Object obj : dcCollections) {
				// Type casting
				Object[] row = (Object[]) obj;
				Integer currentStatusId = (Integer)row[0]; //0
				Time time = (Time)row[1]; //1
				String referenceNo = (String)row[2]; //2
				String invoiceNo = (String)row[3]; //3
				String customerName = (String)row[4]; //4
				Double amount = (Double)row[5]; //5

				boolean isCancelled = currentStatusId.intValue() == FormStatus.CANCELLED_ID;
				DailyCashCollection dcc = 
						DailyCashCollection.getInstanceOf(DateUtil.getTimeFromDate(time), 
								referenceNo, invoiceNo, isCancelled ? "CANCELLED" : customerName, 
								isCancelled ? 0.00 : amount);
				dailyCashCollections.add(dcc);
			}
			return dailyCashCollections;
		}
		return null;
	}

	@Override
	public Page<CashSale> getCashSales(Integer companyId, Integer arCustomerId, Integer arCustomerAccountId,
			Integer csNumber, Date dateFrom, Date dateTo, Integer status, Integer typeId, PageSetting pageSetting, User user) {
		DetachedCriteria dc = getDetachedCriteria();
		addUserCompany(dc, user);
		dc.add(Restrictions.eq(CashSaleReturn.FIELD.cashSaleTypeId.name(), typeId));
		if (companyId != null)
			dc.add(Restrictions.eq(CashSale.FIELD.companyId.name(), companyId));
		if (arCustomerId != null)
			dc.add(Restrictions.eq(CashSale.FIELD.arCustomerId.name(), arCustomerId));
		if (arCustomerAccountId != null)
			dc.add(Restrictions.eq(CashSale.FIELD.arCustomerAccountId.name(), arCustomerAccountId));
		if (csNumber != null)
			dc.add(Restrictions.eq(CashSale.FIELD.csNumber.name(), csNumber));
		if (dateFrom != null && dateTo != null)
			dc.add(Restrictions.between(CashSale.FIELD.receiptDate.name(), dateFrom, dateTo));
		else if (dateFrom != null)
			dc.add(Restrictions.ge(CashSale.FIELD.receiptDate.name(), dateFrom));
		else if (dateTo != null)
			dc.add(Restrictions.le(CashSale.FIELD.receiptDate.name(), dateTo));
		
		// Cash sale Workflow
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		
		if (status != CashSale.STATUS_ALL) {
			// Cash sale return subquery
			DetachedCriteria csrDc = DetachedCriteria.forClass(CashSaleReturn.class);
			csrDc.setProjection(Projections.property(CashSaleReturn.FIELD.cashSaleId.name()));
			csrDc.add(Restrictions.isNotNull(CashSaleReturn.FIELD.cashSaleId.name()));

			//Cash sale return Workflow - Add cash sale that are used by cancelled cash sale return.
			DetachedCriteria csrWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
			csrWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
			csrWorkflow.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));

			csrDc.add(Subqueries.propertyIn(CashSaleReturn.FIELD.formWorkflowId.name(), csrWorkflow));

			if (status == CashSale.STATUS_USED)
				dc.add(Subqueries.propertyIn(CashSale.FIELD.id.name(), csrDc));
			else if (status == CashSale.STATUS_UNUSED)
				dc.add(Subqueries.propertyNotIn(CashSale.FIELD.id.name(), csrDc));
		}
		dcWorkflow.add(Restrictions.eq(FormWorkflow.FIELD.complete.name(), true));
		dc.add(Subqueries.propertyIn(CashSale.FIELD.formWorkflowId.name(), dcWorkflow));
		dc.addOrder(Order.desc(CashSale.FIELD.receiptDate.name()));
		dc.addOrder(Order.desc(CashSale.FIELD.csNumber.name()));
		return getAll(dc, pageSetting);
	}

	@Override
	public Page<CashSale> getCashSalesAfterDate(int itemId, int warehouseId,
			Date date, PageSetting pageSetting) {
		DetachedCriteria cashSaleDc = getDetachedCriteria();
		cashSaleDc.add(Restrictions.gt(CashSale.FIELD.receiptDate.name(), date));
		restrict(cashSaleDc, itemId, warehouseId);
		return getAll(cashSaleDc, pageSetting);
	}

	private void restrict (DetachedCriteria cashSaleDc, int itemId, int warehouseId) {
		// only the selected item and warehouse. 
		DetachedCriteria csItemsDc = DetachedCriteria.forClass(CashSaleItem.class);
		csItemsDc.setProjection(Projections.property(CashSaleItem.FIELD.cashSaleId.name()));
		csItemsDc.add(Restrictions.eq(CashSaleItem.FIELD.itemId.name(), itemId));
		csItemsDc.add(Restrictions.eq(CashSaleItem.FIELD.warehouseId.name(), warehouseId));
		cashSaleDc.add(Subqueries.propertyIn(CashSale.FIELD.id.name(), csItemsDc));
		cashSaleDc.createAlias("formWorkflow", "fw");
		cashSaleDc.add(Restrictions.ne("fw.currentStatusId", FormStatus.CANCELLED_ID));
	}

	@Override
	public Page<CashSale> getCashSalePerDate(int itemId, int warehouseId,
			Date date, PageSetting pageSetting) {
		DetachedCriteria cashSaleDc = getDetachedCriteria();
		cashSaleDc.add(Restrictions.eq(CashSale.FIELD.receiptDate.name(), date));
		restrict(cashSaleDc, itemId, warehouseId);
		cashSaleDc.addOrder(Order.asc(CashSale.FIELD.updatedDate.name()));
		Page<CashSale> cashSales = getAll(cashSaleDc, pageSetting);
		return cashSales;
	}

	@Override
	public Date getOldestTransactionDate(int itemId, int warehouseId) {
		DetachedCriteria cashSaleDc = getDetachedCriteria();
		cashSaleDc.setProjection(Projections.min(CashSale.FIELD.receiptDate.name()));
		restrict(cashSaleDc, itemId, warehouseId);
		List<Object> ret = getByProjection(cashSaleDc);
		if (ret == null || ret.size() < 1){
			return null;
		}
		return (Date)ret.iterator().next();
	}

	@Override
	public int getCSSize() {
		DetachedCriteria dc = getDetachedCriteria();
		dc.setProjection(Projections.count(CashSale.FIELD.id.name()));

		// Cash sale workflow subquery. 
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		dcWorkflow.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		dc.add(Subqueries.propertyIn(CashSale.FIELD.formWorkflowId.name(), dcWorkflow));
		
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
	public Page<CashSale> getCashSales(PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();

		// Cash sale workflow subquery. 
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		dcWorkflow.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		dc.add(Subqueries.propertyIn(CashSale.FIELD.formWorkflowId.name(), dcWorkflow));

		return getAll(dc, pageSetting);
	}

	@Override
	public CashSale getCashSaleByWorkflow(Integer formWorkflowId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(CashSale.FIELD.formWorkflowId.name(), formWorkflowId));
		return get(dc);
	}

	@Override
	public Boolean isExistingReturn(Integer cashSaleId) {
		DetachedCriteria cashReturn = DetachedCriteria.forClass(CashSaleReturn.class);

		cashReturn.setProjection(Projections.property(CashSaleReturn.FIELD.cashSaleId.name()));
		cashReturn.add(Restrictions.eq(CashSaleReturn.FIELD.cashSaleId.name(), cashSaleId));

		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));

		cashReturn.add(Subqueries.propertyIn(CashSaleReturn.FIELD.formWorkflowId.name(), dcWorkflow));
		return getAll(cashReturn).size() > 0;
	}

	@Override
	public boolean hasExistingSI(String salesInvoiceNo) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(CashSale.FIELD.salesInvoiceNo.name(), salesInvoiceNo));
		return getAll(dc).size() > 0;
	}

	@Override
	public List<CashSale> getCashSalesByCustomer(Integer arCustomerId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(CashSale.FIELD.arCustomerId.name(), arCustomerId));

		// Cash sale workflow subquery.
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		dcWorkflow.add(Restrictions.eq(FormWorkflow.FIELD.complete.name(), true));
		dc.add(Subqueries.propertyIn(CashSale.FIELD.formWorkflowId.name(), dcWorkflow));

		dc.addOrder(Order.desc(CashSale.FIELD.receiptDate.name()));
		return getAll(dc);
	}

	@Override
	public List<CashSale> getCashSales(Integer companyId, Integer typeId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(CashSale.FIELD.companyId.name(), companyId));
		dc.add(Restrictions.eq(CashSale.FIELD.cashSaleTypeId.name(), typeId));

		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		dcWorkflow.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		dc.add(Subqueries.propertyIn(CashSale.FIELD.formWorkflowId.name(), dcWorkflow));
		dc.addOrder(Order.asc(CashSale.FIELD.receiptDate.name()));
		dc.addOrder(Order.asc(CashSale.FIELD.createdDate.name()));
		return getAll(dc);
	}

}
