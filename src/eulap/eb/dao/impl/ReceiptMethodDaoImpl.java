package eulap.eb.dao.impl;


import java.util.ArrayList;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.common.dao.DaoUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.dao.ReceiptMethodDao;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.ReceiptMethod;
import eulap.eb.domain.hibernate.User;

/**
 * Implementation class of {@link ReceiptMethodDao}

 */
public class ReceiptMethodDaoImpl extends BaseDao<ReceiptMethod> implements ReceiptMethodDao{

	@Override
	protected Class<ReceiptMethod> getDomainClass() {
		return ReceiptMethod.class;
	}

	@Override
	public boolean isUniqueReceiptMethod(ReceiptMethod receiptMethod) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.like(ReceiptMethod.FIELD.name.name(), receiptMethod.getName().trim()));
		dc.add(Restrictions.eq(ReceiptMethod.FIELD.companyId.name(), receiptMethod.getCompanyId()));
		if (receiptMethod.getBankAccountId() > 0) {
			dc.add(Restrictions.eq(ReceiptMethod.FIELD.bankAccountId.name(), receiptMethod.getBankAccountId()));
		} else if (receiptMethod.getDbACDivisionId() != 0) {
			DetachedCriteria acDc = DetachedCriteria.forClass(AccountCombination.class);
			acDc.setProjection(Projections.property(AccountCombination.FIELD.id.name()));
			acDc.add(Restrictions.eq(AccountCombination.FIELD.divisionId.name(), receiptMethod.getDbACDivisionId()));
			dc.add(Subqueries.propertyIn(ReceiptMethod.FIELD.debitAcctCombinationId.name(), acDc));
		}
		if (receiptMethod.getId() != 0) {
			dc.add(Restrictions.ne(ReceiptMethod.FIELD.id.name(), receiptMethod.getId()));
		}
		return getAll(dc).size() == 0;
	}

	@Override
	public ReceiptMethod getReceiptMethodById(int receiptId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ReceiptMethod.FIELD.id.name(), receiptId));
		return get(dc);
	}

	@Override
	public Page<ReceiptMethod> searchReceiptMethod(String searchCriteria,
			PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.like(ReceiptMethod.FIELD.name.name(), "%" + searchCriteria + "%"));
		return getAll(dc, pageSetting);
	}

	@Override
	public List<ReceiptMethod> getReceiptMethods(User user) {
		DetachedCriteria dc = getDetachedCriteria();
		addUserCompany(dc, user);
		dc.add(Restrictions.eq(ReceiptMethod.FIELD.active.name(), true));
		dc.addOrder(Order.asc(ReceiptMethod.FIELD.name.name()));
		return getAll(dc);
	}

	@Override
	public Page<ReceiptMethod> searchReceiptMethod(String name,
			Integer companyId, Integer bankAccountId, SearchStatus status,
			PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		if(!name.trim().isEmpty())
			dc.add(Restrictions.like(ReceiptMethod.FIELD.name.name(), "%"+name.trim()+"%"));
		if(companyId != -1)
			dc.add(Restrictions.eq(ReceiptMethod.FIELD.companyId.name(), companyId));
		if(bankAccountId != -1)
			dc.add(Restrictions.eq(ReceiptMethod.FIELD.bankAccountId.name(), bankAccountId));
		dc = DaoUtil.setSearchStatus(dc,ReceiptMethod.FIELD.active.name(),status);
		dc.addOrder(Order.asc(ReceiptMethod.FIELD.name.name()));
		return getAll(dc, pageSetting);
	}

	@Override
	public List<ReceiptMethod> getReceiptMethods(Integer companyId) {
		DetachedCriteria dc = getDetachedCriteria();
		if(companyId != null && companyId != 0) {
			dc.add(Restrictions.eq(ReceiptMethod.FIELD.companyId.name(), companyId));
		}
		dc.add(Restrictions.eq(ReceiptMethod.FIELD.active.name(), true));
		return getAll(dc);
	}

	@Override
	public List<ReceiptMethod> getReceiptMethodsByDivision(Integer divisionId) {
		List<ReceiptMethod> receiptMethods = new ArrayList<ReceiptMethod>();
		String sql = "SELECT RM.RECEIPT_METHOD_ID, RM.NAME, RM.COMPANY_ID, RM.DEBIT_ACCOUNT_COMBINATION_ID, "
				+ "RM.CREDIT_ACCOUNT_COMBINATION_ID, RM.BANK_ACCOUNT_ID  "
				+ "FROM RECEIPT_METHOD RM "
				+ "LEFT JOIN ACCOUNT_COMBINATION AC ON RM.DEBIT_ACCOUNT_COMBINATION_ID "
				+ "LEFT JOIN BANK_ACCOUNT BA ON BA.BANK_ACCOUNT_ID = RM.BANK_ACCOUNT_ID "
				+ "LEFT JOIN ACCOUNT_COMBINATION BA_AC ON BA_AC.ACCOUNT_COMBINATION_ID = BA.CASH_IN_BANK_ACCT_ID "
				+ "WHERE RM.ACTIVE = 1  "
				+ "AND (AC.DIVISION_ID = ? OR BA_AC.DIVISION_ID = ? ) "
				+ "GROUP BY RM.RECEIPT_METHOD_ID";
		Session session = null;
		ReceiptMethod rm = null;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			Integer index = 0;
			query.setParameter(index, divisionId);
			query.setParameter(++index, divisionId);
			List<Object[]> list = query.list();
			if (list != null && !list.isEmpty()) {
				for (Object[] row : list) {
					int column = 0;
					rm = new ReceiptMethod();
					rm.setId((Integer) row[column]);
					rm.setName((String) row[++column]);
					rm.setCompanyId((Integer) row[++column]);
					rm.setDebitAcctCombinationId((Integer) row[++column]);
					rm.setCreditAcctCombinationId((Integer) row[++column]);
					rm.setBankAccountId((Integer) row[++column]);
					receiptMethods.add(rm);
				}
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return receiptMethods;
	}

	@Override
	public List<ReceiptMethod> getReceiptMethods(Integer companyId, Integer receiptMethodId) {
		DetachedCriteria dc = getDetachedCriteria();
		if(companyId != null) {
			dc.add(Restrictions.eq(ReceiptMethod.FIELD.companyId.name(), companyId));
		}
		if(receiptMethodId != null) {
			dc.add(Restrictions.or(Restrictions.eq(ReceiptMethod.FIELD.active.name(), true),
					Restrictions.eq(ReceiptMethod.FIELD.id.name(), receiptMethodId)));
		} else {
			dc.add(Restrictions.eq(ReceiptMethod.FIELD.active.name(), true));
		}
		return getAll(dc);
	}
}
