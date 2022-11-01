package eulap.eb.dao.impl;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.ApPaymentDao;
import eulap.eb.dao.ApPaymentInvoiceDao;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.ApPayment;
import eulap.eb.domain.hibernate.ApPaymentInvoice;
import eulap.eb.domain.hibernate.BankAccount;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.PaymentType;
import eulap.eb.domain.hibernate.SupplierAccount;

/**
 * DAO implementation of {@link ApPaymentDao}

 */
public class ApPaymentInvoiceDaoImpl extends BaseDao<ApPaymentInvoice> implements ApPaymentInvoiceDao{

	@Override
	protected Class<ApPaymentInvoice> getDomainClass() {
		return ApPaymentInvoice.class;
	}

	@Override
	public Collection<ApPaymentInvoice> getPaidInvoices(Integer apInvoiceId) {
		DetachedCriteria dc = getDetachedCriteria();

		//Subquery for AP Payment
		DetachedCriteria paymentCriteria =  DetachedCriteria.forClass(ApPayment.class);
		paymentCriteria.setProjection(Projections.property(ApPayment.FIELD.id.name()));
		paymentCriteria.add(Restrictions.eq(ApPayment.FIELD.paymentTypeId.name(), PaymentType.TYPE_AP_PAYMENT));

		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		paymentCriteria.add(Subqueries.propertyIn(ApPayment.FIELD.formWorkflowId.name(), dcWorkflow));
		dc.add(Subqueries.propertyIn(ApPaymentInvoice.FIELD.apPaymentId.name(), paymentCriteria));

		dc.add(Restrictions.eq(ApPaymentInvoice.FIELD.invoiceId.name(), apInvoiceId));
		return getAll(dc);
	}

	@Override
	public Collection<ApPaymentInvoice> getPaidInvoicesByPaymentId(Integer apPaymentId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ApPaymentInvoice.FIELD.apPaymentId.name(), apPaymentId));

		//Subquery for AP Payment
		DetachedCriteria paymentCriteria =  DetachedCriteria.forClass(ApPayment.class);
		paymentCriteria.setProjection(Projections.property(ApPayment.FIELD.id.name()));

		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		paymentCriteria.add(Subqueries.propertyIn(ApPayment.FIELD.formWorkflowId.name(), dcWorkflow));
		dc.add(Subqueries.propertyIn(ApPaymentInvoice.FIELD.apPaymentId.name(), paymentCriteria));
		return getAll(dc);
	}

	@Override
	public double getTotalDebit(int companyId, Date asOfDate, int accountId) {
		return getTotalAccount(true, companyId, asOfDate, accountId);
	}

	@Override
	public double getTotalCredit(int companyId, Date asOfDate, int accountId) {
		return Math.abs(getTotalAccount(false, companyId, asOfDate, accountId));
	}

	private double getTotalAccount (boolean isDebit, int companyId, Date asOfDate, int accountId) {
		DetachedCriteria apPInvoiceCriteria = getDetachedCriteria();
		if (isDebit)
			apPInvoiceCriteria.add(Restrictions.gt(ApPaymentInvoice.FIELD.paidAmount.name(), 0.0));
		else
			apPInvoiceCriteria.add(Restrictions.lt(ApPaymentInvoice.FIELD.paidAmount.name(), 0.0));
		//Subquery for AP Payment
		DetachedCriteria apPaymentCriteria = DetachedCriteria.forClass(ApPayment.class);
		apPaymentCriteria.add(Restrictions.le(ApPayment.FIELD.paymentDate.name(), asOfDate));
		filterByComplete(apPaymentCriteria);

		apPaymentCriteria.setProjection(Projections.property(ApPayment.FIELD.id.name()));
		//Subquery for Account Combination
		DetachedCriteria acctCombiCriteria = DetachedCriteria.forClass(AccountCombination.class);
		acctCombiCriteria.add(Restrictions.eq(AccountCombination.FIELD.companyId.name(), companyId));
		acctCombiCriteria.add(Restrictions.eq(AccountCombination.FIELD.accountId.name(), accountId));
		acctCombiCriteria.setProjection(Projections.property(AccountCombination.FIELD.id.name()));
		//Subquery for Supplier Account
		DetachedCriteria supplierAcctCriteria =  DetachedCriteria.forClass(SupplierAccount.class);
		supplierAcctCriteria.add(Subqueries.propertyIn(SupplierAccount.FIELD.defaultCreditACId.name(), acctCombiCriteria));
		supplierAcctCriteria.setProjection(Projections.property(SupplierAccount.FIELD.id.name()));
		apPaymentCriteria.add(Subqueries.propertyIn(ApPayment.FIELD.supplierAccountId.name(), supplierAcctCriteria));
		apPInvoiceCriteria.add(Subqueries.propertyIn(ApPaymentInvoice.FIELD.apPaymentId.name(), apPaymentCriteria));

		apPInvoiceCriteria.setProjection(Projections.sum(ApPaymentInvoice.FIELD.paidAmount.name()));
		return getBySumProjection(apPInvoiceCriteria);
	}

	@Override
	public List<ApPaymentInvoice> getBCApPaymentInvoices(int companyId,
			int accountId, Collection<Division> divisions, Date dateFrom,
			Date dateTo) {
		DetachedCriteria apPInvoiceCriteria = getDetachedCriteria();
		//Subquery for AP Payment
		DetachedCriteria apPaymentCriteria = DetachedCriteria.forClass(ApPayment.class);
		if (dateTo == null)
			apPaymentCriteria.add(Restrictions.lt(ApPayment.FIELD.paymentDate.name(), dateFrom));
		else
			apPaymentCriteria.add(Restrictions.between(ApPayment.FIELD.paymentDate.name(), dateFrom, dateTo));
		apPaymentCriteria.add(Restrictions.between(ApPayment.FIELD.paymentDate.name(), dateFrom, dateTo));
		apPaymentCriteria.setProjection(Projections.property(ApPayment.FIELD.id.name()));
		filterByComplete(apPaymentCriteria);

		//Subquery for Account Combination
		DetachedCriteria acctCombiCriteria = DetachedCriteria.forClass(AccountCombination.class);
		acctCombiCriteria.add(Restrictions.eq(AccountCombination.FIELD.companyId.name(), companyId));
		acctCombiCriteria.add(Restrictions.eq(AccountCombination.FIELD.accountId.name(), accountId));
		acctCombiCriteria = DaoDivisionHandler.setDivisionCriteria(acctCombiCriteria, divisions);
		acctCombiCriteria.setProjection(Projections.property(AccountCombination.FIELD.id.name()));
		//Subquery for Bank Account
		DetachedCriteria bankAcctCriteria = DetachedCriteria.forClass(BankAccount.class);
		bankAcctCriteria.add(Subqueries.propertyIn(BankAccount.FIELD.cashPaymentClearingAcctId.name(), acctCombiCriteria));
		bankAcctCriteria.setProjection(Projections.property(BankAccount.FIELD.id.name()));
		apPaymentCriteria.add(Subqueries.propertyIn(ApPayment.FIELD.bankAccountId.name(), bankAcctCriteria));

		apPInvoiceCriteria.add(Subqueries.propertyIn(ApPaymentInvoice.FIELD.apPaymentId.name(), apPaymentCriteria));		
		return getAll(apPInvoiceCriteria);
	}

	@Override
	public double totalPerAccount(int companyId, Date asOfDate, int accountId) {
		DetachedCriteria dc = getDetachedCriteria();
		//Subquery for AP Payment
		DetachedCriteria apPaymentCriteria = DetachedCriteria.forClass(ApPayment.class);
		apPaymentCriteria.add(Restrictions.le(ApPayment.FIELD.paymentDate.name(), asOfDate));

		apPaymentCriteria.setProjection(Projections.property(ApPayment.FIELD.id.name()));
		filterByComplete(apPaymentCriteria);
		//Subquery for Account Combination
		DetachedCriteria acctCombiCriteria = DetachedCriteria.forClass(AccountCombination.class);
		acctCombiCriteria.add(Restrictions.eq(AccountCombination.FIELD.companyId.name(), companyId));
		acctCombiCriteria.add(Restrictions.eq(AccountCombination.FIELD.accountId.name(), accountId));
		acctCombiCriteria.setProjection(Projections.property(AccountCombination.FIELD.id.name()));
		//Subquery for Bank Account
		DetachedCriteria bankAcctCriteria = DetachedCriteria.forClass(BankAccount.class);
		bankAcctCriteria.setProjection(Projections.property(BankAccount.FIELD.id.name()));
		bankAcctCriteria.add(Subqueries.propertyIn(BankAccount.FIELD.cashPaymentClearingAcctId.name(), acctCombiCriteria));
		apPaymentCriteria.add(Subqueries.propertyIn(ApPayment.FIELD.bankAccountId.name(), bankAcctCriteria));
		dc.add(Subqueries.propertyIn(ApPaymentInvoice.FIELD.apPaymentId.name(), apPaymentCriteria));
		return getBySumProjection(dc);
	}

	public double getBCTotalDebit(int companyId, Date asOfDate, int accountId) {
		return Math.abs(getBCTotalAccount(true, companyId, asOfDate, accountId));
	}

	@Override
	public double getBCTotalCredit(int companyId, Date asOfDate, int accountId) {
		return getBCTotalAccount(false, companyId, asOfDate, accountId);
	}

	private double getBCTotalAccount (boolean isDebit, int companyId, Date asOfDate, int accountId) {
		DetachedCriteria apPInvoiceCriteria = getDetachedCriteria();
		if (isDebit)
			apPInvoiceCriteria.add(Restrictions.lt(ApPaymentInvoice.FIELD.paidAmount.name(), 0.0));
		else
			apPInvoiceCriteria.add(Restrictions.gt(ApPaymentInvoice.FIELD.paidAmount.name(), 0.0));
		//Subquery for AP Payment
		DetachedCriteria apPaymentCriteria = DetachedCriteria.forClass(ApPayment.class);
		apPaymentCriteria.add(Restrictions.le(ApPayment.FIELD.paymentDate.name(), asOfDate));
		filterByComplete(apPaymentCriteria);
		apPaymentCriteria.setProjection(Projections.property(ApPayment.FIELD.id.name()));
		//Subquery for Account Combination
		DetachedCriteria acctCombiCriteria = DetachedCriteria.forClass(AccountCombination.class);
		acctCombiCriteria.add(Restrictions.eq(AccountCombination.FIELD.companyId.name(), companyId));
		acctCombiCriteria.add(Restrictions.eq(AccountCombination.FIELD.accountId.name(), accountId));
		acctCombiCriteria.setProjection(Projections.property(AccountCombination.FIELD.id.name()));
		//Subquery for Bank Account
		DetachedCriteria bankAcctCriteria = DetachedCriteria.forClass(BankAccount.class);
		bankAcctCriteria.add(Subqueries.propertyIn(BankAccount.FIELD.cashPaymentClearingAcctId.name(), acctCombiCriteria));
		bankAcctCriteria.setProjection(Projections.property(BankAccount.FIELD.id.name()));
		apPaymentCriteria.add(Subqueries.propertyIn(ApPayment.FIELD.bankAccountId.name(), bankAcctCriteria));
		apPInvoiceCriteria.add(Subqueries.propertyIn(ApPaymentInvoice.FIELD.apPaymentId.name(), apPaymentCriteria));

		apPInvoiceCriteria.setProjection(Projections.sum(ApPaymentInvoice.FIELD.paidAmount.name()));
		return getBySumProjection(apPInvoiceCriteria);
	}

	@Override
	public ApPaymentInvoice getApPaymentCheckBookNumber(int invoiceId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ApPaymentInvoice.FIELD.apPaymentId.name(), invoiceId));
		return get(dc);
	}

	@Override
	public List<ApPaymentInvoice> getPaidInvoices(Integer invoiceId, Date asOfDate) {
		DetachedCriteria dc = getDetachedCriteria();
		//Subquery for AP Payment
		DetachedCriteria paymentCriteria =  DetachedCriteria.forClass(ApPayment.class);
		paymentCriteria.setProjection(Projections.property(ApPayment.FIELD.id.name()));

		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		paymentCriteria.add(Subqueries.propertyIn(ApPayment.FIELD.formWorkflowId.name(), dcWorkflow));
		if(asOfDate != null) {
			paymentCriteria.add(Restrictions.le(ApPayment.FIELD.checkDate.name(), asOfDate));
		}
		dc.add(Subqueries.propertyIn(ApPaymentInvoice.FIELD.apPaymentId.name(), paymentCriteria));
		if(invoiceId != null) {
			dc.add(Restrictions.eq(ApPaymentInvoice.FIELD.invoiceId.name(), invoiceId));
		}
		return getAll(dc);
	}

	@Override
	public Collection<ApPaymentInvoice> getPaidInvoicesExcludeInPaymentId(Integer apInvoiceId, Integer paymentId) {
		DetachedCriteria dc = getDetachedCriteria();

		//Subquery for AP Payment
		DetachedCriteria paymentCriteria =  DetachedCriteria.forClass(ApPayment.class);
		paymentCriteria.setProjection(Projections.property(ApPayment.FIELD.id.name()));
		if(paymentId != null && paymentId != 0){
			paymentCriteria.add(Restrictions.ne(ApPayment.FIELD.id.name(), paymentId));
		}

		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		paymentCriteria.add(Subqueries.propertyIn(ApPayment.FIELD.formWorkflowId.name(), dcWorkflow));
		dc.add(Subqueries.propertyIn(ApPaymentInvoice.FIELD.apPaymentId.name(), paymentCriteria));

		dc.add(Restrictions.eq(ApPaymentInvoice.FIELD.invoiceId.name(), apInvoiceId));
		return getAll(dc);
	}

}
