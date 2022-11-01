package eulap.eb.dao.impl;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.CheckbookDao;
import eulap.eb.domain.hibernate.BankAccount;
import eulap.eb.domain.hibernate.Checkbook;
import eulap.eb.domain.hibernate.User;

/**
 * The implementation class of {@link CheckbookDao}

 */
public class CheckbookDaoImpl extends BaseDao<Checkbook> implements CheckbookDao{

	@Override
	protected Class<Checkbook> getDomainClass() {
		return Checkbook.class;
	}

	@Override
	public boolean isUniqueCheckbook(Checkbook checkbook, int bankAccountId) {
		DetachedCriteria dc =getDetachedCriteria();
		dc.add(Restrictions.like(Checkbook.FIELD.name.name(), checkbook.getName().trim()));
		dc.add(Restrictions.eq(Checkbook.FIELD.bankAccountId.name(), bankAccountId));
		return getAll(dc).size() < 1;
	}

	@Override
	public boolean isValidSeries(Checkbook checkbook) {
		String sql = "SELECT * FROM CHECKBOOK where (? BETWEEN CHECKBOOK_NO_FROM AND CHECKBOOK_NO_TO" +
				" OR ? BETWEEN  CHECKBOOK_NO_FROM AND CHECKBOOK_NO_TO OR CHECKBOOK_NO_FROM >= ? and CHECKBOOK_NO_TO <= ?) " +
				" and BANK_ACCOUNT_ID = ? ";
		if(checkbook.getId() > 0) {
			sql += " AND CHECKBOOK_ID != ? ";
		}
		Session session = null;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(0, checkbook.getCheckbookNoFrom());
			query.setParameter(1, checkbook.getCheckbookNoTo());
			query.setParameter(2, checkbook.getCheckbookNoFrom());
			query.setParameter(3, checkbook.getCheckbookNoTo());
			query.setParameter(4, checkbook.getBankAccountId());
			if(checkbook.getId() > 0) {
				query.setParameter(5, checkbook.getId());
			}
			List<?> list = query.list();
			return list.size() < 1;
		} finally {
			if (session != null)
				session.close();
		}
	}

	@Override
	public Page<Checkbook> searchCheckbook(User user, String searchCriteria,
			PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.like(Checkbook.FIELD.name.name(), "%" + searchCriteria.trim() + "%"));
		evalLoggedUser(user, dc);
		return getAll(dc, pageSetting);
	}

	@Override
	public List<Checkbook> getCheckbooks(User user, int bankAccountId, String checkBookName, Integer limit) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(Checkbook.FIELD.active.name(), true));
		dc.add(Restrictions.eq(Checkbook.FIELD.bankAccountId.name(), bankAccountId));
		evalLoggedUser(user, dc);
		if(checkBookName != null && checkBookName.trim() != ""){
			dc.add(Restrictions.like(Checkbook.FIELD.name.name(), "%" +checkBookName+ "%"));
		}
		if (limit != null) {
			dc.getExecutableCriteria(getSession()).setMaxResults(limit);
		}
		return getAll(dc);
	}

	@Override
	public Page<Checkbook> getCheckbooks(User user, Integer companyId, String bankAccountName, String name,
			BigDecimal checkNo, int status, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		if (companyId != null && companyId != -1) {
			DetachedCriteria baDc = DetachedCriteria.forClass(BankAccount.class);
			baDc.setProjection(Projections.property(BankAccount.FIELD.id.name()));
			baDc.add(Restrictions.eq(BankAccount.FIELD.companyId.name(), companyId));
			dc.add(Subqueries.propertyIn(Checkbook.FIELD.bankAccountId.name(), baDc));
		}
		dc.createAlias("bankAccount", "ba");
		if (bankAccountName != null && !bankAccountName.trim().isEmpty()) 
			dc.add(Restrictions.like("ba.name", "%" + bankAccountName + "%"));
		if (name != null && !name.trim().isEmpty())
			dc.add(Restrictions.like(Checkbook.FIELD.name.name(), "%" + name + "%"));
		if (checkNo != null) {
			dc.add(Restrictions.and(
					Restrictions.le(Checkbook.FIELD.checkbookNoFrom.name(), checkNo), 
					Restrictions.ge(Checkbook.FIELD.checkbookNoTo.name(), checkNo)));
		}
		if (status != -1)
			dc.add(Restrictions.eq(Checkbook.FIELD.active.name(), status == 1));
		evalLoggedUser(user, dc);
		dc.addOrder(Order.asc("ba.name"));
		return getAll(dc, pageSetting);
	}

	@Override
	public List<Checkbook> getCheckbooks(User user, int bankAccountId) {
		return getCheckbooks(user, bankAccountId, null, null);
	}

	private void evalLoggedUser(User user, DetachedCriteria dc) {
		DetachedCriteria bankAccountDc = DetachedCriteria.forClass(BankAccount.class);
		bankAccountDc.setProjection(Projections.property(BankAccount.FIELD.id.name()));
		addUserCompany(bankAccountDc, user);
		dc.add(Subqueries.propertyIn(Checkbook.FIELD.bankAccountId.name(), bankAccountDc));
	}

	@Override
	public Checkbook getCheckBook(User user, Integer bankAccountId, String checkBookName) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(Checkbook.FIELD.active.name(), true));
		dc.add(Restrictions.eq(Checkbook.FIELD.bankAccountId.name(), bankAccountId));
		dc.add(Restrictions.eq(Checkbook.FIELD.name.name(), checkBookName.trim()));
		evalLoggedUser(user, dc);
		return get(dc);
	}
}