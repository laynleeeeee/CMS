package eulap.eb.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.springframework.orm.hibernate3.HibernateCallback;

import eulap.common.dao.BaseDao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.DirectPaymentDao;
import eulap.eb.domain.hibernate.ApPayment;
import eulap.eb.domain.hibernate.DirectPayment;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.PaymentType;
import eulap.eb.domain.hibernate.Supplier;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.ApprovalSearchParam;

/**
 * DAO Implementation class of {@link DirectPaymentDao}

 *
 */
public class DirectPaymentDaoImpl extends BaseDao<DirectPayment> implements DirectPaymentDao{

	@Override
	protected Class<DirectPayment> getDomainClass() {
		return DirectPayment.class;
	}

	@Override
	public Page<DirectPayment> getAllDirectPayments(final ApprovalSearchParam searchParam, final List<Integer> formStatusIds,
			final PageSetting pageSetting) {
		HibernateCallback<Page<DirectPayment>> hibernateCallback = new HibernateCallback<Page<DirectPayment>>() {
			@Override
			public Page<DirectPayment> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria dpCriteria = session.createCriteria(DirectPayment.class);
				DetachedCriteria dcApPayment = DetachedCriteria.forClass(ApPayment.class);
				dcApPayment.add(Restrictions.eq(ApPayment.FIELD.paymentTypeId.name(), PaymentType.TYPE_DIRECT_PAYMENT));

				User user = searchParam.getUser();
				List<Integer> userCompanyIds = user.getCompanyIds();
				if (!userCompanyIds.isEmpty()) {
					// The user has now company assignment.
					// No company assignment is assume to have access to all companies
					dcApPayment.add(Restrictions.in(ApPayment.FIELD.companyId.name(), userCompanyIds));
				}

				String criteria = searchParam.getSearchCriteria();
				if(!criteria.isEmpty()) {
					if (StringFormatUtil.isNumeric(criteria)) {
						Criterion checkNo = Restrictions.sqlRestriction("CHECK_NUMBER LIKE ?", "%" + criteria.trim() + "%", Hibernate.STRING);
						Criterion voucherNo = Restrictions.sqlRestriction("VOUCHER_NO LIKE ?", "%" + criteria.trim() + "%", Hibernate.STRING);
						Criterion checkAndVoucherCriteria = Restrictions.or(checkNo, voucherNo);
						Criterion invoiceNoCriteria = Restrictions.like(DirectPayment.FIELD.invoiceNo.name(), "%" + criteria.trim() + "%");
						dpCriteria.add(Restrictions.or(checkAndVoucherCriteria, invoiceNoCriteria));
					} else {
						DetachedCriteria dcSupplier = DetachedCriteria.forClass(Supplier.class);
						dcSupplier.setProjection(Projections.property(Supplier.FIELD.id.name()));
						dcSupplier.add(Restrictions.like(Supplier.FIELD.name.name(), "%" + criteria.trim() + "%"));
						dcApPayment.add(Subqueries.propertyIn(ApPayment.FIELD.supplierId.name(), dcSupplier));
					}
				}

				// Workflow status
				DetachedCriteria dcWorkFlow = DetachedCriteria.forClass(FormWorkflow.class);
				if (formStatusIds.size() > 0)
					addAsOrInCritiria(dcWorkFlow, FormWorkflow.FIELD.currentStatusId.name(), formStatusIds);
				dcWorkFlow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
				dcApPayment.add(Subqueries.propertyIn(ApPayment.FIELD.formWorkflowId.name(), dcWorkFlow));
				dcApPayment.setProjection(Projections.property(ApPayment.FIELD.id.name()));
				dpCriteria.add(Subqueries.propertyIn(DirectPayment.FIELD.apPaymentId.name(), dcApPayment));

				dpCriteria.createAlias("apPayment", "app");
				dpCriteria.addOrder(Order.desc("app.paymentDate"));
				dpCriteria.addOrder(Order.desc("app.checkDate"));
				return getAll(dpCriteria, pageSetting);
			}
		};
		return getHibernateTemplate().execute(hibernateCallback);
	}

	@Override
	public DirectPayment getDirectPaymentByPaymentId(Integer apPaymentId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(DirectPayment.FIELD.apPaymentId.name(), apPaymentId));
		return get(dc);
	}

	@Override
	public boolean isExistingInvoiceNo(String invoiceNo, Integer directPaymentId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(DirectPayment.FIELD.invoiceNo.name(), StringFormatUtil.removeExtraWhiteSpaces(invoiceNo)));
		if(directPaymentId != null) {
			dc.add(Restrictions.ne(DirectPayment.FIELD.id.name(), directPaymentId));
		}
		return getAll(dc).size() >= 1;
	}


}
