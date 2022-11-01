package eulap.eb.dao.impl;

import java.sql.SQLException;
import java.util.Date;
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
import eulap.eb.dao.GeneralLedgerDao;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.GeneralLedger;
import eulap.eb.domain.hibernate.GlEntry;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.DivisionDto;

/**
 * Implementation class of {@link GeneralLedgerDao}

 *
 */
public class GeneralLedgerDaoImpl extends BaseDao<GeneralLedger> implements GeneralLedgerDao{

	@Override
	protected Class<GeneralLedger> getDomainClass() {
		return GeneralLedger.class;
	}

	@Override
	public int generateSequenceNo(int divisionId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.setProjection(Projections.max(GeneralLedger.FIELD.sequenceNo.name()));
		dc.add(Restrictions.eq(GeneralLedger.FIELD.divisionId.name(), divisionId));
		List<Object> result = getByProjection(dc);
		if (result == null)
			return 1;
		Object obj = result.iterator().next();
		if (obj == null)
			return 1;
		return ((Integer) obj) + 1;
	}

	@Override
	public GeneralLedger getGL(final int id) {
		HibernateCallback<GeneralLedger> hibernateCallBack = new HibernateCallback<GeneralLedger>() {
			@Override
			public GeneralLedger doInHibernate(Session session)
					throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(GeneralLedger.class);
				criteria.add(Restrictions.eq(GeneralLedger.FIELD.id.name(), id));
				GeneralLedger gl = get(criteria);
				getHibernateTemplate().initialize(gl.getGlEntries());
				return gl;
			}
		};
		return getHibernateTemplate().execute(hibernateCallBack);
	}

	@Override
	public Page<GeneralLedger> searchGeneralLedger(String searchCriteria, PageSetting pageSetting) {
		return search(null, searchCriteria, pageSetting);
	}

	@Override
	public Page<GeneralLedger> searchGeneralLedgers(Integer divisionId, String searchCriteria,
			PageSetting pageSetting) {
		return search(divisionId, searchCriteria, pageSetting);
	}

	private Page<GeneralLedger> search(Integer divisionId, String searchCriteria, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		if (StringFormatUtil.isNumeric(searchCriteria)) {
			dc.add(Restrictions.or(
					Restrictions.sqlRestriction("SEQUENCE_NO LIKE ?", "%" + searchCriteria.trim() + "%",
							Hibernate.STRING),
					Restrictions.like(GeneralLedger.FIELD.comment.name(), "%" + searchCriteria.trim() + "%")));
		} else {
			dc.add(Restrictions.like(GeneralLedger.FIELD.comment.name(), "%" + searchCriteria.trim() + "%"));
		}
		if (divisionId != null) {
			dc.add(Restrictions.eq(GeneralLedger.FIELD.divisionId.name(), divisionId));
		}
		return getAll(dc, pageSetting);
	}

	@Override
	public List<GeneralLedger> getGLWithNullWF() {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.isNull(GeneralLedger.FIELD.formWorkflowId.name()));
		dc.getExecutableCriteria(getSession()).setMaxResults(100);
		return getAll(dc);
	}

	@Override
	public Page<GeneralLedger> getAllGeneralLedger(ApprovalSearchParam searchParam,
			List<Integer> formStatusIds, PageSetting pageSetting) {
		DetachedCriteria glCriteria = getDetachedCriteria();

		DetachedCriteria glEntryCrit = DetachedCriteria.forClass(GlEntry.class);
		glEntryCrit.setProjection(Projections.property(GlEntry.FIELD.generalLedgerId.name()));
		//Search for company number
		String compNo = searchParam.getCompanyNo();
		DetachedCriteria acctCombiCrit = DetachedCriteria.forClass(AccountCombination.class);
		acctCombiCrit.setProjection(Projections.property(AccountCombination.FIELD.id.name()));

		addUserCompany(acctCombiCrit, searchParam.getUser());
		DetachedCriteria companyCrit  = DetachedCriteria.forClass(Company.class);
		companyCrit.setProjection(Projections.property(Company.Field.id.name()));
		if(compNo != null) {
			companyCrit.add(Restrictions.like(Company.Field.companyNumber.name(), "%"+compNo.trim()+"%"));
		}
		acctCombiCrit.add(Subqueries.propertyIn(AccountCombination.FIELD.companyId.name(), companyCrit));
		glEntryCrit.add(Subqueries.propertyIn(GlEntry.FIELD.accountCombinationId.name(), acctCombiCrit));

		//Search for amount
		if(searchParam.getAmount() != null) {
			glEntryCrit.add(Restrictions.sqlRestriction("IS_DEBIT != 0 GROUP BY GENERAL_LEDGER_ID HAVING SUM(AMOUNT) = ?",
					searchParam.getAmount(), Hibernate.DOUBLE));
		}

		//Search for date
		Date dateFrom = searchParam.getDateFrom();
		Date dateTo = searchParam.getDateTo();
		if(dateFrom != null) {
			if(dateTo != null) {
				glCriteria.add(Restrictions.between(GeneralLedger.FIELD.glDate.name(), dateFrom, dateTo));
			} else {
				glCriteria.add(Restrictions.eq(GeneralLedger.FIELD.glDate.name(), dateFrom));
			}
		}

		String criteria = searchParam.getSearchCriteria();
		if(criteria != null) {
			if (StringFormatUtil.isNumeric(criteria))
				glCriteria.add(Restrictions.or(Restrictions.sqlRestriction("SEQUENCE_NO LIKE ?", "%"+criteria.trim()+"%", Hibernate.STRING),
						Restrictions.like(GeneralLedger.FIELD.comment.name(), "%"+criteria.trim()+"%")));
			else
				glCriteria.add(Restrictions.like(GeneralLedger.FIELD.comment.name(), "%"+criteria.trim()+"%"));
		}

		// Workflow status
		DetachedCriteria dcWorkFlow = DetachedCriteria.forClass(FormWorkflow.class);
		if (formStatusIds.size() > 0)
			addAsOrInCritiria(dcWorkFlow, FormWorkflow.FIELD.currentStatusId.name(), formStatusIds);
		dcWorkFlow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		glCriteria.add(Subqueries.propertyIn(GeneralLedger.FIELD.formWorkflowId.name(), dcWorkFlow));
		glCriteria.add(Subqueries.propertyIn(GeneralLedger.FIELD.id.name(), glEntryCrit));
		glCriteria.addOrder(Order.desc(GeneralLedger.FIELD.glDate.name()));
		glCriteria.addOrder(Order.desc(GeneralLedger.FIELD.sequenceNo.name()));
		return getAll(glCriteria, pageSetting);
	}

	@Override
	public boolean hasNullFW() {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.isNull(GeneralLedger.FIELD.formWorkflowId.name()));
		return getAll(dc).size() > 0;
	}

	@Override
	public Page<GeneralLedger> searchJournalVoucherRegister(Integer companyId, Integer divisionId,
			Date fromGLDate, Date toGLDate, Integer status, Integer createdBy,
			Integer updatedBy, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		// GL Date
		if(fromGLDate != null && toGLDate != null)
			dc.add(Restrictions.between(GeneralLedger.FIELD.glDate.name(), fromGLDate, toGLDate));
		else if(fromGLDate != null)
			dc.add(Restrictions.ge(GeneralLedger.FIELD.glDate.name(), fromGLDate));
		else if(toGLDate != null)
			dc.add(Restrictions.le(GeneralLedger.FIELD.glDate.name(), toGLDate));

		// Filtering company.
		DetachedCriteria glCriteria = DetachedCriteria.forClass(GlEntry.class);
		glCriteria.setProjection(Projections.property(GlEntry.FIELD.generalLedgerId.name()));
		DetachedCriteria acctCombinationCriteria = DetachedCriteria.forClass(AccountCombination.class);
		acctCombinationCriteria.setProjection(Projections.property(AccountCombination.FIELD.id.name()));
		acctCombinationCriteria.add(Restrictions.eq(AccountCombination.FIELD.companyId.name(), companyId));
		if(!divisionId.equals(DivisionDto.DIV_ALL)) {
			dc.add(Restrictions.eq(GeneralLedger.FIELD.divisionId.name(),divisionId));
		}
		glCriteria.add(Subqueries.propertyIn(GlEntry.FIELD.accountCombinationId.name(), acctCombinationCriteria));
		dc.add(Subqueries.propertyIn(GeneralLedger.FIELD.id.name(), glCriteria));

		// Selected created by
		if(createdBy != -1) 
			dc.add(Restrictions.eq(GeneralLedger.FIELD.createdBy.name(),createdBy));

		// Selected updated by
		if(updatedBy != -1) {
			Criterion newGL = Restrictions.sqlRestriction("{alias}.UPDATED_BY = ? AND {alias}.FORM_WORKFLOW_ID IN "
					+ "(SELECT FORM_WORKFLOW_ID FROM FORM_WORKFLOW WHERE CURRENT_STATUS_ID=1)", updatedBy, Hibernate.INTEGER);
			String updatedWorkflowLogSql = "(SELECT FORM_WORKFLOW_ID FROM "
					+ "(select (SELECT CREATED_BY FROM FORM_WORKFLOW_LOG L WHERE L.FORM_WORKFLOW_LOG_ID = MAX(FWL.FORM_WORKFLOW_LOG_ID) ) AS UPDATED_BY, "
					+ "FWL.FORM_WORKFLOW_ID "
					+ "from GENERAL_LEDGER GL	"
					+ "JOIN FORM_WORKFLOW_LOG FWL ON FWL.FORM_WORKFLOW_ID = GL.FORM_WORKFLOW_ID "
					+ "AND FWL.FORM_STATUS_ID != 1 "
					+ "GROUP BY FWL.FORM_WORKFLOW_ID) GL_UPDATED_LOG "
					+ "where GL_UPDATED_LOG.UPDATED_BY = ?)";
			Criterion updatedByCriterion = Restrictions.sqlRestriction("{alias}.FORM_WORKFLOW_ID IN "
					+ updatedWorkflowLogSql , updatedBy, Hibernate.INTEGER);
			dc.add(Restrictions.or(newGL, updatedByCriterion));
		}

		// Status is not all
		if(status != -1){
			DetachedCriteria workflowCriteria = DetachedCriteria.forClass(FormWorkflow.class);
			workflowCriteria.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
			workflowCriteria.add(Restrictions.eq(FormWorkflow.FIELD.currentStatusId.name(), status));
			dc.add(Subqueries.propertyIn(GeneralLedger.FIELD.formWorkflowId.name(), workflowCriteria));
		}

		dc.addOrder(Order.asc(GeneralLedger.FIELD.sequenceNo.name()));
		return getAll(dc,pageSetting);
	}

	@Override
	public GeneralLedger getGLByWorkflow(Integer formWorkflowId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(GeneralLedger.FIELD.formWorkflowId.name(), formWorkflowId));
		return get(dc);
	}

	@Override
	public Page<GeneralLedger> getAllGLDivsByStatus(int typeId, ApprovalSearchParam searchParam,
			List<Integer> formStatusIds, PageSetting pageSetting) {
		return getAllGLByStatus(typeId, searchParam, formStatusIds, pageSetting);
	}

	private Page<GeneralLedger> getAllGLByStatus(final int typeId, final ApprovalSearchParam searchParam,
			final List<Integer> formStatusIds, final PageSetting pageSetting) {
		HibernateCallback<Page<GeneralLedger>> hibernateCallback = new HibernateCallback<Page<GeneralLedger>>() {
			@Override
			public Page<GeneralLedger> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria glCriteria = session.createCriteria(GeneralLedger.class);
				if (typeId != 0) {
					glCriteria.add(Restrictions.eq(GeneralLedger.FIELD.divisionId.name(), typeId));
				}
				if (StringFormatUtil.isNumeric(searchParam.getSearchCriteria())) {
					glCriteria.add(Restrictions.sqlRestriction("SEQUENCE_NO LIKE ?",
							searchParam.getSearchCriteria().trim(), Hibernate.STRING));
				}
				SearchCommonUtil.searchCommonParams(glCriteria, null, "companyId",
						GeneralLedger.FIELD.glDate.name(), GeneralLedger.FIELD.glDate.name(),
						GeneralLedger.FIELD.glDate.name(), searchParam.getUser().getCompanyIds(), searchParam);
				DetachedCriteria workflowCriteria = DetachedCriteria.forClass(FormWorkflow.class);
				if (!formStatusIds.isEmpty()) {
					addAsOrInCritiria(workflowCriteria, FormWorkflow.FIELD.currentStatusId.name(), formStatusIds);
				}
				workflowCriteria.addOrder(Order.asc(FormWorkflow.FIELD.currentStatusId.name()));
				workflowCriteria.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
				glCriteria.add(Subqueries.propertyIn(GeneralLedger.FIELD.formWorkflowId.name(), workflowCriteria));
				glCriteria.addOrder(Order.desc(GeneralLedger.FIELD.glDate.name()));
				glCriteria.addOrder(Order.desc(GeneralLedger.FIELD.sequenceNo.name()));
				return getAll(glCriteria, pageSetting);
			}
		};
		return getHibernateTemplate().execute(hibernateCallback);
	}
}
