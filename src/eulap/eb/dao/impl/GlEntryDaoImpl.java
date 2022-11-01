package eulap.eb.dao.impl;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.GlEntryDao;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.GeneralLedger;
import eulap.eb.domain.hibernate.GlEntry;
import eulap.eb.domain.hibernate.TimePeriod;

/**
 * Implementation class of {@link GlEntryDao}


 */
public class GlEntryDaoImpl extends BaseDao<GlEntry> implements GlEntryDao {
	@Override
	protected Class<GlEntry> getDomainClass() {
		return GlEntry.class;
	}
	
	@Override
	public Page<GlEntry> getGlEntries(PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		return getAll(dc, pageSetting);
	}

	/**
	 * Get the total credit amount.
	 * @param companyId The company id.
	 * @param accountId The account id.
	 */
	@Override
	public double getTotalCredit(int companyId, Date asOfDate, int accountId) {
		return getTotalAccount(false, companyId, asOfDate, accountId);
	}

	/**
	 * Get the total debit amount.
	 * @param companyId The company id.
	 * @param accountId The account id.
	 */
	@Override
	public double getTotalDebit(int companyId, Date asOfDate, int accountId) {
		return getTotalAccount(true, companyId, asOfDate,accountId);
	}

	private double getTotalAccount (boolean isDebit, int companyId, Date asOfDate, int accountId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(GlEntry.FIELD.debit.name(), isDebit));

		//Retrieves the posted entries only.
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.add(Restrictions.eq(FormWorkflow.FIELD.complete.name(), true));
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		
		DetachedCriteria glCriteria = DetachedCriteria.forClass(GeneralLedger.class);
		glCriteria.add(Subqueries.propertyIn(GeneralLedger.FIELD.formWorkflowId.name(), dcWorkflow));
		glCriteria.add(Restrictions.le(GeneralLedger.FIELD.glDate.name(), asOfDate));
		glCriteria.setProjection(Projections.property(GeneralLedger.FIELD.id.name()));
		dc.add(Subqueries.propertyIn(GlEntry.FIELD.generalLedgerId.name(), glCriteria));

		// To retrieve the selected account only
		DetachedCriteria acctCombinationCriteria = DetachedCriteria.forClass(AccountCombination.class);
		acctCombinationCriteria.add(Restrictions.eq(AccountCombination.FIELD.companyId.name(), companyId));
		acctCombinationCriteria.add(Restrictions.eq(AccountCombination.FIELD.accountId.name(), accountId));
		acctCombinationCriteria.setProjection(Projections.property(AccountCombination.FIELD.id.name()));
		dc.add(Subqueries.propertyIn(GlEntry.FIELD.accountCombinationId.name(), acctCombinationCriteria));

		dc.setProjection(Projections.sum(GlEntry.FIELD.amount.name()));
		return getBySumProjection(dc);
	}
		
	@Override
	public Collection<GlEntry> getGlEntries(Integer companyId,
			Integer accountId, Collection<Division> divisions, Date dateFrom,
			Date dateTo) {
		
		DetachedCriteria glEntryCriteria = DetachedCriteria.forClass(GlEntry.class);
		// For general ledger
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.add(Restrictions.eq(FormWorkflow.FIELD.complete.name(), true));
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		
		DetachedCriteria generalLedgerCriteria = DetachedCriteria.forClass(GeneralLedger.class);
		generalLedgerCriteria.add(Restrictions.between(GeneralLedger.FIELD.glDate.name(), dateFrom, dateTo));
		generalLedgerCriteria.add(Subqueries.propertyIn(GeneralLedger.FIELD.formWorkflowId.name(), dcWorkflow));
		generalLedgerCriteria.setProjection(Projections.property(GeneralLedger.FIELD.id.name()));
		// For account combination
		DetachedCriteria acctCombinationCriteria = DetachedCriteria.forClass(AccountCombination.class);
		acctCombinationCriteria.add(Restrictions.eq(AccountCombination.FIELD.companyId.name(), companyId));
		acctCombinationCriteria.add(Restrictions.eq(AccountCombination.FIELD.accountId.name(), accountId));
		acctCombinationCriteria = DaoDivisionHandler.setDivisionCriteria(acctCombinationCriteria, divisions);
		acctCombinationCriteria.setProjection(Projections.property(AccountCombination.FIELD.id.name()));
		
		glEntryCriteria.add(Subqueries.propertyIn(GlEntry.FIELD.generalLedgerId.name(), generalLedgerCriteria));
		glEntryCriteria.add(Subqueries.propertyIn(GlEntry.FIELD.accountCombinationId.name(), acctCombinationCriteria));
		glEntryCriteria.createAlias("generalLedger", "generalLedger").addOrder(Order.asc("generalLedger.glDate"));
		glEntryCriteria.createAlias("generalLedger.glStatus", "glStatus").addOrder(Order.asc("glStatus.description"));
		return getAll(glEntryCriteria);
	}

	@Override
	public Collection<GlEntry> getGlEntries(Integer companyId,
			Integer accountId, Collection<Division> divisions, Date dateFrom) {
		DetachedCriteria glEntryCriteria = DetachedCriteria.forClass(GlEntry.class);
		// For general ledger
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.add(Restrictions.eq(FormWorkflow.FIELD.complete.name(), true));
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		
		DetachedCriteria generalLedgerCriteria = DetachedCriteria.forClass(GeneralLedger.class);
		generalLedgerCriteria.add(Restrictions.lt(GeneralLedger.FIELD.glDate.name(), dateFrom));
		generalLedgerCriteria.add(Subqueries.propertyIn(GeneralLedger.FIELD.formWorkflowId.name(), dcWorkflow));
		generalLedgerCriteria.setProjection(Projections.property(GeneralLedger.FIELD.id.name()));
		// For account combination
		DetachedCriteria acctCombinationCriteria = DetachedCriteria.forClass(AccountCombination.class);
		acctCombinationCriteria.add(Restrictions.eq(AccountCombination.FIELD.companyId.name(), companyId));
		acctCombinationCriteria.add(Restrictions.eq(AccountCombination.FIELD.accountId.name(), accountId));
		acctCombinationCriteria = DaoDivisionHandler.setDivisionCriteria(acctCombinationCriteria, divisions);
		acctCombinationCriteria.setProjection(Projections.property(AccountCombination.FIELD.id.name()));
		
		glEntryCriteria.add(Subqueries.propertyIn(GlEntry.FIELD.generalLedgerId.name(), generalLedgerCriteria));
		glEntryCriteria.add(Subqueries.propertyIn(GlEntry.FIELD.accountCombinationId.name(), acctCombinationCriteria));
		return getAll(glEntryCriteria);
	}
	
	@Override
	public Collection<GlEntry> getGlEntries(int accountCombinationId, TimePeriod timePeriod) {
		DetachedCriteria glEntryCriteria = DetachedCriteria.forClass(GlEntry.class);
		glEntryCriteria.add(Restrictions.eq(GlEntry.FIELD.accountCombinationId.name(), accountCombinationId));
		
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.add(Restrictions.eq(FormWorkflow.FIELD.complete.name(), true));
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		
		DetachedCriteria generalLedgerCriteria = DetachedCriteria.forClass(GeneralLedger.class);
		generalLedgerCriteria.add(Restrictions.between(
				GeneralLedger.FIELD.glDate.name(), timePeriod.getDateFrom(), timePeriod.getDateTo()));
		generalLedgerCriteria.add(Subqueries.propertyIn(GeneralLedger.FIELD.formWorkflowId.name(), dcWorkflow));
		generalLedgerCriteria.setProjection(Projections.property(GeneralLedger.FIELD.id.name()));
		
		glEntryCriteria.add(Subqueries.propertyIn(GlEntry.FIELD.generalLedgerId.name(), generalLedgerCriteria));
		return getAll(glEntryCriteria);
	}

	@Override
	public double getTotalCredit(int companyId,  int accountId, TimePeriod timePeriod) {
		return getTotalAccount(false, companyId, accountId, timePeriod);
	}

	@Override
	public double getTotalDebit(int companyId,  int accountId, TimePeriod timePeriod) {
		return getTotalAccount(true, companyId, accountId, timePeriod);
	}
	
	private double getTotalAccount (boolean isDebit, int companyId,  int accountId, TimePeriod timePeriod) {
		DetachedCriteria glEntryCriteria = DetachedCriteria.forClass(GlEntry.class);
		
		DetachedCriteria acctCombinationCriteria = DetachedCriteria.forClass(AccountCombination.class);
		acctCombinationCriteria.add(Restrictions.eq(AccountCombination.FIELD.companyId.name(), companyId));
		acctCombinationCriteria.add(Restrictions.eq(AccountCombination.FIELD.accountId.name(), accountId));
		acctCombinationCriteria.setProjection(Projections.property(AccountCombination.FIELD.id.name()));
		
		// For general ledger
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.add(Restrictions.eq(FormWorkflow.FIELD.complete.name(), true));
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		
		DetachedCriteria generalLedgerCriteria = DetachedCriteria.forClass(GeneralLedger.class);
		generalLedgerCriteria.add(
				Restrictions.between(GeneralLedger.FIELD.glDate.name(), timePeriod.getDateFrom(), timePeriod.getDateTo()));
		generalLedgerCriteria.add(Subqueries.propertyIn(GeneralLedger.FIELD.formWorkflowId.name(), dcWorkflow));
		generalLedgerCriteria.setProjection(Projections.property(GeneralLedger.FIELD.id.name()));
		
		
		glEntryCriteria.add(Restrictions.eq(GlEntry.FIELD.debit.name(), isDebit));
		glEntryCriteria.add(Subqueries.propertyIn(GlEntry.FIELD.accountCombinationId.name(), acctCombinationCriteria));
		glEntryCriteria.add(Subqueries.propertyIn(GlEntry.FIELD.generalLedgerId.name(), generalLedgerCriteria));
		glEntryCriteria.setProjection(Projections.sum(GlEntry.FIELD.amount.name()));
		
		return getBySumProjection(glEntryCriteria);
	}

	@Override
	public double totalPerAccount(int companyId,  int accountId, Date asOfDate) {
		DetachedCriteria dc = getDetachedCriteria();
		// For general ledger
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.add(Restrictions.eq(FormWorkflow.FIELD.complete.name(), true));
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		
		DetachedCriteria generalLedgerCriteria = DetachedCriteria.forClass(GeneralLedger.class);
		generalLedgerCriteria.setProjection(Projections.property(GeneralLedger.FIELD.id.name()));
		generalLedgerCriteria.add(Restrictions.le(GeneralLedger.FIELD.glDate.name(), asOfDate));
		generalLedgerCriteria.add(Subqueries.propertyIn(GeneralLedger.FIELD.formWorkflowId.name(), dcWorkflow));
		
		// For account combination
		DetachedCriteria acctCombinationCriteria = DetachedCriteria.forClass(AccountCombination.class);
		acctCombinationCriteria.setProjection(Projections.property(AccountCombination.FIELD.id.name()));
		acctCombinationCriteria.add(Restrictions.eq(AccountCombination.FIELD.companyId.name(), companyId));
		acctCombinationCriteria.add(Restrictions.eq(AccountCombination.FIELD.accountId.name(), accountId));
		
		dc.add(Subqueries.propertyIn(GlEntry.FIELD.generalLedgerId.name(), generalLedgerCriteria));
		dc.add(Subqueries.propertyIn(GlEntry.FIELD.accountCombinationId.name(), acctCombinationCriteria));
		dc.setProjection(Projections.sum(GlEntry.FIELD.amount.name()));
		return getBySumProjection(dc);
	}

	/**
	 * Get all gl entries by generalLedgerId
	 * @param generalLedgerId The unique id of generalLedger.
	 */
	@Override
	public List<GlEntry> getGLEntries(int generalLedgerId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(GlEntry.FIELD.generalLedgerId.name(), generalLedgerId));
		return getAll(dc);
	}

	@Override
	public List<Company> getCompanies(String company,
			boolean isActive, Integer limit) {
		DetachedCriteria cDc = DetachedCriteria.forClass(Company.class);
		DetachedCriteria acDc = DetachedCriteria.forClass(AccountCombination.class);
		acDc.setProjection(Projections.property(AccountCombination.FIELD.companyId.name()));
		if(!company.trim().isEmpty()){
			cDc.add(Restrictions.or(Restrictions.like(Company.Field.name.name(), "%"+company.trim()+"%"),
					Restrictions.like(Company.Field.companyNumber.name(), "%"+company.trim()+"%")));
		}
		if(isActive) {
			cDc.add(Restrictions.eq(Company.Field.active.name(), true));
		}
		if(limit != null) {
			cDc.getExecutableCriteria(getSession()).setMaxResults(limit);
		}
		cDc.add(Subqueries.propertyIn("id", acDc));
		cDc.addOrder(Order.asc(Company.Field.companyNumber.name()));
		return getHibernateTemplate().findByCriteria(cDc);
	}
}
