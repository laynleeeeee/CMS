package eulap.eb.dao.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
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
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.DivisionDao;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.ArCustomer;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.DivisionProject;
import eulap.eb.domain.hibernate.Employee;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.DivisionDto;

/**
 * DAO implementation of DivisionDao

 */

public class DivisionDaoImpl extends BaseDao<Division> implements DivisionDao{

	@Override
	protected Class<Division> getDomainClass() {
		return Division.class;
	}

	@Override
	public Division getDivisionById(int divisionId, User user) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(Division.Field.id.name(), divisionId));
		dc.add(Restrictions.eq(Division.Field.serviceLeaseKeyId.name(), user.getServiceLeaseKeyId()));
		return get(dc);
	}

	@Override
	public Page<Division> getAllDivisions(User user) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(Division.Field.serviceLeaseKeyId.name(), user.getServiceLeaseKeyId()));
		dc.addOrder(Order.asc(Division.Field.name.name()));
		return getAll(dc, new PageSetting(1));
	}

	@Override
	public Page<Division> searchDivisions(String number, String name, User user, SearchStatus status, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		if (number != null && !number.isEmpty())
			dc.add(Restrictions.like(Division.Field.number.name(), "%" + number.trim() + "%"));
		if (name != null && !name.isEmpty())
			dc.add(Restrictions.like(Division.Field.name.name(), "%" + name.trim() + "%"));
		dc = DaoUtil.setSearchStatus(dc, ArCustomer.FIELD.active.name(), status);
		dc.add(Restrictions.eq(Division.Field.serviceLeaseKeyId.name(), user.getServiceLeaseKeyId()));
		dc.addOrder(Order.asc(Division.Field.name.name()));
		return getAll(dc, pageSetting);
	}

	@Override
	public boolean isUniqueDivisionField(Division division, int divisionField, User user) {
		DetachedCriteria dc = getDetachedCriteria();
		switch (divisionField) {
		case 1: dc.add(Restrictions.eq(Division.Field.number.name(), division.getNumber().trim()));
				break;
		case 2: dc.add(Restrictions.like(Division.Field.name.name(), division.getName().trim()));
				break;
		}
		dc.add(Restrictions.eq(Division.Field.serviceLeaseKeyId.name(), user.getServiceLeaseKeyId()));
		return getAll(dc).size() < 1;
	}

	@Override
	public Collection<Division> getActiveDivisions(User user) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(Division.Field.active.name(), true));
		dc.add(Restrictions.eq(Division.Field.serviceLeaseKeyId.name(), user.getServiceLeaseKeyId()));
		dc.addOrder(Order.asc(Division.Field.name.name()));
		return getAll(dc);
	}

	@Override
	public Collection<Division> getDivisionByAcctCombination(int companyId) {
		DetachedCriteria divisionCriteria = getDetachedCriteria();
		// For account combination
		DetachedCriteria acctCombinationCriteria = DetachedCriteria.forClass(AccountCombination.class);
		acctCombinationCriteria.add(Restrictions.eq(AccountCombination.FIELD.active.name(), true));
		acctCombinationCriteria.add(Restrictions.eq(AccountCombination.FIELD.companyId.name(), companyId));
		acctCombinationCriteria.setProjection(Projections.property("divisionId"));
		divisionCriteria.add(Subqueries.propertyIn("id", acctCombinationCriteria));
		divisionCriteria.add(Restrictions.eq(Division.Field.active.name(), true));
		divisionCriteria.addOrder(Order.asc(Division.Field.name.name()));
		return getAll(divisionCriteria);
	}

	@Override
	public Collection<Division> getDivisions(int companyId, int accountId,
			Integer selectedDivisionId, boolean isActiveOnly, User user) {
		DetachedCriteria divisionCriteria = getDetachedCriteria();
		// For account combination
		DetachedCriteria acctCombinationCriteria = DetachedCriteria.forClass(AccountCombination.class);
		if (isActiveOnly)
			acctCombinationCriteria.add(Restrictions.eq(AccountCombination.FIELD.active.name(), true));
		acctCombinationCriteria.add(Restrictions.eq(AccountCombination.FIELD.companyId.name(), companyId));
		acctCombinationCriteria.add(Restrictions.eq(AccountCombination.FIELD.accountId.name(), accountId));
		acctCombinationCriteria.setProjection(Projections.property("divisionId"));
		divisionCriteria.add(Subqueries.propertyIn("id", acctCombinationCriteria));
		if (selectedDivisionId != null)
			divisionCriteria.add(Restrictions.ne(Division.Field.id.name(), selectedDivisionId));
		if (isActiveOnly)
			divisionCriteria.add(Restrictions.eq(Division.Field.active.name(), true));
		divisionCriteria.add(Restrictions.eq(Division.Field.serviceLeaseKeyId.name(), user.getServiceLeaseKeyId()));
		divisionCriteria.addOrder(Order.asc(Division.Field.name.name()));
		return getAll(divisionCriteria);
	}

	@Override
	public Division getDivisionByDivNumber(String divisionNumber, int serviceLeaseKeyId) {
		DetachedCriteria dc = getDCWithSLkeyIdAndActive(serviceLeaseKeyId);
		dc.add(Restrictions.eq(Division.Field.number.name(), divisionNumber));
		return get(dc);
	}

	@Override
	public Division getDivisionByName(String divisionName, boolean isActiveOnly, boolean excludeDivWithAcctCombi) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(Division.Field.name.name(), divisionName));
		if (isActiveOnly) {
			dc.add(Restrictions.eq(Division.Field.active.name(), isActiveOnly));
		}
		if (excludeDivWithAcctCombi) {
			DetachedCriteria acDc = DetachedCriteria.forClass(AccountCombination.class);
			acDc.setProjection(Projections.property(AccountCombination.FIELD.divisionId.name()));
			acDc.add(Restrictions.eq(AccountCombination.FIELD.active.name(), true));
			dc.add(Subqueries.propertyNotIn(Division.Field.id.name(), acDc));
		}
		return get(dc);
	}

	@Override
	public List<Division> getDivisionByCompanyIdAndName(Integer companyId, String divisionNumber, boolean isActive, Integer limit) {
		DetachedCriteria dc = getDetachedCriteria();
		if (divisionNumber != null && !divisionNumber.isEmpty()){
			dc.add(Restrictions.or(Restrictions.like(Division.Field.name.name(), "%" + divisionNumber.trim() + "%"),
					Restrictions.like(Division.Field.number.name(), "%" + divisionNumber.trim() + "%")));
		}
		if (isActive) {
			dc.add(Restrictions.eq(Division.Field.active.name(), true));
		}
		if (limit != null) {
			dc.getExecutableCriteria(getSession()).setMaxResults(limit);
		}
		DetachedCriteria acctCombinationCriteria = DetachedCriteria.forClass(AccountCombination.class);
		if(companyId != null) {
			acctCombinationCriteria.add(Restrictions.eq(AccountCombination.FIELD.companyId.name(), companyId));
		}
		acctCombinationCriteria.add(Restrictions.eq(AccountCombination.FIELD.active.name(), true));
		acctCombinationCriteria.setProjection(Projections.property("divisionId"));
		dc.add(Subqueries.propertyIn(Division.Field.id.name(), acctCombinationCriteria));
		dc.addOrder(Order.asc(Division.Field.name.name()));
		return getAll(dc);
	}

	@Override
	public Collection<Division> getActiveDivisions() {
		DetachedCriteria dc = this.getDetachedCriteria();
		dc.add(Restrictions.eq((String)Division.Field.active.name(), true));
		return this.getAll(dc);
	}

	@Override
	public Division getDivisionByEmployeeId(Integer employeeId) {
		DetachedCriteria dc = getDetachedCriteria();
		DetachedCriteria employeeDc = DetachedCriteria.forClass(Employee.class);
		employeeDc.setProjection(Projections.property(Employee.FIELD.divisionId.name()));
		if (employeeId != null) {
			employeeDc.add(Restrictions.eq(Employee.FIELD.id.name(), employeeId));
		}
		dc.add(Subqueries.propertyIn(Division.Field.id.name(), employeeDc));
		return get(dc);
	}

	@Override
	public Division getMaxDivisionByNumber() {
		DetachedCriteria dc = getDetachedCriteria();
		dc.addOrder(Order.desc(Division.Field.number.name()));
		dc.getExecutableCriteria(getSession()).setMaxResults(1);
		return get(dc);
	}

	@Override
	public List<Division> getDivisions(String numberOrName, Integer divisionId, boolean isMainLevelOnly) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(Division.Field.active.name(), true));
		if (divisionId != null) {
			dc.add(Restrictions.ne(Division.Field.id.name(), divisionId));
		}
		if(numberOrName != null && !numberOrName.trim().isEmpty()) {
			dc.add(Restrictions.or(Restrictions.like(Division.Field.name.name(), "%"+numberOrName.trim()+"%"),
					Restrictions.like(Division.Field.number.name(), "%"+numberOrName.trim()+"%")));
		}
		if (isMainLevelOnly) {
			dc.add(Restrictions.isNull(Division.Field.parentDivisionId.name()));
		}
		DetachedCriteria acDc = DetachedCriteria.forClass(AccountCombination.class);
		acDc.setProjection(Projections.property(AccountCombination.FIELD.divisionId.name()));
		acDc.add(Restrictions.eq(AccountCombination.FIELD.active.name(), true));
		dc.add(Subqueries.propertyNotIn(Division.Field.id.name(), acDc));

		return getAll(dc);
	}

	@Override
	public Page<DivisionDto> searchDivisionWithSubs(String divNumber, String name, User user, SearchStatus status,
			boolean isMainDivisionOnly, PageSetting pageSetting) {
		String sql = "SELECT D.DIVISION_ID, D.NUMBER, D.NAME AS DIV_NAME, D.DESCRIPTION, "
				+ "PD.DIVISION_ID AS PD_ID, COALESCE(PD.NAME, '') AS PD_NAME, D.ACTIVE FROM DIVISION D "
				+ "LEFT JOIN DIVISION PD ON PD.DIVISION_ID = D.PARENT_DIVISION_ID ";
		Integer searchStatusAsId = null;
		if(status == SearchStatus.All) {
			searchStatusAsId = -1;
			sql += "WHERE D.ACTIVE != ? ";
		} else {
			if(status == SearchStatus.Active ) {
				searchStatusAsId = 1;
			} else if(status == SearchStatus.Inactive ) {
				searchStatusAsId = 0;
			}
			sql += "WHERE D.ACTIVE = ? ";
		}
		sql += (isMainDivisionOnly ? "AND D.PARENT_DIVISION_ID IS NULL " : "");
		if (!divNumber.isEmpty()) {
			String tmpDivNumber = divNumber.replace("%", "\\%").replace("_", "\\_");
			sql += "AND D.NUMBER LIKE '%" + tmpDivNumber + "%' ";
		}
		if (!name.isEmpty()) {
			String tmpName = name.replace("%", "\\%").replace("_", "\\_");
			sql += "AND D.NAME LIKE '%" + tmpName + "%' ";
		}
		sql += "ORDER BY D.NUMBER";
		return getAllAsPage(sql, pageSetting, new DivisionDtoHandler(this, searchStatusAsId));
	}

	private static class DivisionDtoHandler implements QueryResultHandler<DivisionDto> {
		private DivisionDaoImpl divisionDaoImpl;
		private Integer searchStatusAsId;

		private DivisionDtoHandler(DivisionDaoImpl divisionDaoImpl, Integer searchStatusAsId) {
			this.divisionDaoImpl = divisionDaoImpl;
			this.searchStatusAsId = searchStatusAsId;
		}

		@Override
		public List<DivisionDto> convert(List<Object[]> queryResult) {
			List<DivisionDto> mainDivs = new ArrayList<>();
			DivisionDto dto = null;
			for (Object[] rowResult : queryResult) {
				int colNum = 0;
				Integer id = (Integer) rowResult[colNum++];
				String number = (String) rowResult[colNum++];
				String name = (String) rowResult[colNum++];
				String description = (String) rowResult[colNum++];
				Integer pdId = (Integer) rowResult[colNum++];
				String pdName = (String) rowResult[colNum++];
				boolean active = (Boolean) rowResult[colNum++];
				dto = DivisionDto.getInstanceOf(id, number, name, description, pdId, pdName, active, this.divisionDaoImpl.isLastLevel(id, searchStatusAsId));
				dto.setMainParent(true);
				mainDivs.add(dto);
			}
			return mainDivs;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			if(searchStatusAsId == null) {
				query.setParameter(index, 1);
			} else {
				query.setParameter(index, searchStatusAsId);
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("DIVISION_ID", Hibernate.INTEGER);
			query.addScalar("NUMBER", Hibernate.STRING);
			query.addScalar("DIV_NAME", Hibernate.STRING);
			query.addScalar("DESCRIPTION", Hibernate.STRING);
			query.addScalar("PD_ID", Hibernate.INTEGER);
			query.addScalar("PD_NAME", Hibernate.STRING);
			query.addScalar("ACTIVE", Hibernate.BOOLEAN);
		}
	}

	@Override
	public boolean isLastLevel(int divisionId, Integer searchStatusId) {
		String sql = "SELECT DIVISION_ID FROM DIVISION WHERE DIVISION_ID NOT IN "
				+ "(SELECT PARENT_DIVISION_ID FROM DIVISION "
				+ "WHERE PARENT_DIVISION_ID IS NOT NULL ";
		if(searchStatusId.equals(-1)) {
			sql += "AND ACTIVE != ?) ";
		} else {
			sql += "AND  ACTIVE = ?) ";
		}
		sql += "AND DIVISION_ID = ? ";
		Session session = null;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(0, searchStatusId);
			query.setParameter(1, divisionId);
			return !query.list().isEmpty();
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	@Override
	public List<DivisionDto> getAllChildren(int divisionId, boolean isActive) {
		return getChildren(divisionId, StringUtils.EMPTY, isActive, Boolean.FALSE, false);
	}

	@Override
	public List<DivisionDto> getAllChildren(int divisionId, String name, boolean isActive, boolean isExact) {
		return getChildren(divisionId, name, isActive, Boolean.TRUE, isExact);
	}

	private List<DivisionDto> getChildren(int divisionId, String name, boolean isActive, boolean isIncludeParent, boolean isExact) {
		String sql = "SELECT D.DIVISION_ID, D.NUMBER, D.NAME AS DIV_NAME, D.DESCRIPTION, "
				+ "PD.DIVISION_ID AS PD_ID, PD.NAME AS PD_NAME, D.ACTIVE FROM DIVISION D "
				+ "LEFT JOIN DIVISION PD ON PD.DIVISION_ID = D.PARENT_DIVISION_ID ";
		Integer searchStatusId = null;
		if (isActive) {
			searchStatusId = 1;
			sql += "WHERE D.ACTIVE = ? ";
		} else {
			searchStatusId = -1;
			sql += "WHERE D.ACTIVE != ? ";
		}

		// include parent division in the list
		if (isIncludeParent) {
			sql += "AND (PD.DIVISION_ID = " + divisionId + " OR D.DIVISION_ID = " + divisionId + ")";
		} else {
			sql += "AND PD.DIVISION_ID = " + divisionId;
		}

		// filter by name
		if (!name.isEmpty()) {
			if (isExact) {
				sql += " AND (D.NAME = '"+name.trim()+"' OR D.NUMBER = '"+name.trim()+"')";
			} else {
				sql += " AND (D.NAME LIKE '%"+name.trim()+"%' OR D.NUMBER LIKE '%"+name.trim()+"%')";
			}
		}
		return (List<DivisionDto>) getAllAsPage(sql, new PageSetting(PageSetting.START_PAGE, 
				PageSetting.NO_PAGE_CONSTRAINT), new DivisionDtoHandler(this, searchStatusId)).getData();
	}

	@Override
	public List<Division> getLastLevelDivisions(boolean isExcludeNoParent) {
		List<Division> divisions = new ArrayList<>();
		String sql = "SELECT DIVISION_ID, PARENT_DIVISION_ID, EB_OBJECT_ID, NUMBER, NAME, DESCRIPTION, ACTIVE, "
			+ "CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, EB_SL_KEY_ID FROM DIVISION WHERE ";
			if (isExcludeNoParent) {
				sql += "PARENT_DIVISION_ID IS NOT NULL ";
			} else {
				sql += "DIVISION_ID NOT IN "
					+ "(SELECT PARENT_DIVISION_ID FROM DIVISION WHERE PARENT_DIVISION_ID IS NOT NULL AND ACTIVE = 1) ";
			}
			sql += "AND ACTIVE = ? ";
		Session session = null;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(0, 1);
			List<Object[]> list = query.list();
			if (list != null && !list.isEmpty()) {
				for (Object[] row : list) {
					int colNum = 0;
					Integer id = (Integer) row[colNum++];
					Integer parentDivisionId = (Integer) row[colNum++];
					Integer ebObjectId = (Integer) row[colNum++];
					String number = (String) row[colNum++];
					String name = (String) row[colNum++];
					String description = (String) row[colNum++];
					boolean active = (Boolean) row[colNum++];
					Integer createdBy = (Integer) row[colNum++];
					Date createdDate = (Date) row[colNum++];
					Integer updatedBy = (Integer) row[colNum++];
					Date updatedDate = (Date) row[colNum++];
					Integer serviceLeaseKeyId = (Integer) row[colNum++];
					divisions.add(Division.getInstanceOf(id, parentDivisionId, ebObjectId, 
							number, name, description, active, createdBy, createdDate, updatedBy, 
							updatedDate, serviceLeaseKeyId));
				}
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return divisions;
	}

	@Override
	public List<DivisionDto> getByCombination(Integer companyId) {
		List<DivisionDto> divisionDtos = new ArrayList<>();
		String sql = "SELECT DISTINCT D.DIVISION_ID, D.NUMBER, D.NAME, D.DESCRIPTION, "
				+ "COALESCE(D.PARENT_DIVISION_ID, CAST(0 as unsigned)) AS PD_ID, COALESCE(PD.NAME, '') AS PD_NAME, D.ACTIVE FROM DIVISION D "
				+ "LEFT JOIN DIVISION PD ON PD.DIVISION_ID = D.PARENT_DIVISION_ID "
				+ "INNER JOIN ACCOUNT_COMBINATION AC ON AC.DIVISION_ID = D.DIVISION_ID " 
				+ "WHERE D.ACTIVE = 1 "
				+ "AND AC.ACTIVE = 1 " 
				+ "AND AC.COMPANY_ID = ? " 
				+ "ORDER BY D.NUMBER ";
		Session session = null;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(0, companyId);

			List<Object[]> list = query.list();
			if (list != null && !list.isEmpty()) {
				for (Object[] row : list) {
					divisionDtos.add(DivisionDto.getInstanceOf((Integer) row[0], (String) row[1], (String) row[2],
							(String) row[3], ((BigInteger) row[4]).intValue(), (String) row[5], (Boolean) row[6],
							isLastLevel((Integer) row[0], 1)));
				}
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return divisionDtos;
	}

	@Override
	public boolean isUsedAsParentDivision(Integer divisionId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(Division.Field.parentDivisionId.name(), divisionId));
		return getAll(dc).size() > 0;
	}

	@Override
	public List<Division> retrieveDivisions(String divisionName, Integer divisionId, boolean isExact, Integer limit) {
		DetachedCriteria dc = getDetachedCriteria();
		if (divisionName != null && !divisionName.isEmpty()) {
			if (isExact) {
				dc.add(Restrictions.eq(Division.Field.name.name(),
						StringFormatUtil.removeExtraWhiteSpaces(divisionName)));
			} else {
				dc.add(Restrictions.like(Division.Field.name.name(),
						StringFormatUtil.appendWildCard(divisionName)));
			}
		}
		Criterion criterion1 = Restrictions.eq(Division.Field.active.name(), true);
		if (divisionId != null) {
			Criterion criterion2 = Restrictions.and(Restrictions.eq(Division.Field.id.name(), divisionId),
					Restrictions.eq(Division.Field.active.name(), false));
			criterion1 = Restrictions.or(criterion1, criterion2);
		}
		dc.add(criterion1);
		if (limit != null) {
			dc.getExecutableCriteria(getSession()).setMaxResults(limit);
		}
		return getAll(dc);
	}

	@Override
	public List<Division> getProjectDivisionsByName(String name) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.like(Division.Field.name.name(), "%"+name));
		return getAll(dc);
	}

	@Override
	public List<Division> getProjectDivisions(String divisionName, Integer companyId, boolean isExact) {
		DetachedCriteria dc = getDetachedCriteria();
		if(!divisionName.trim().isEmpty()) {
			if(isExact) {
				dc.add(Restrictions.eq(Division.Field.name.name(), divisionName));
			} else {
				dc.add(Restrictions.like(Division.Field.name.name(), "%"+divisionName.trim()+"%"));
			}
		}

		dc.add(Restrictions.eq(Division.Field.active.name(), true));

		DetachedCriteria divProjDC = DetachedCriteria.forClass(DivisionProject.class);
		divProjDC.setProjection(Projections.property(DivisionProject.FIELD.divisionId.name()));

		DetachedCriteria custDC = DetachedCriteria.forClass(ArCustomer.class);
		custDC.setProjection(Projections.property(ArCustomer.FIELD.id.name()));
		custDC.add(Restrictions.eq(ArCustomer.FIELD.active.name(), true));
		divProjDC.add(Subqueries.propertyIn(DivisionProject.FIELD.projectId.name(), custDC));
		dc.add(Subqueries.propertyIn(Division.Field.id.name(), divProjDC));

		DetachedCriteria acctCombinationCriteria = DetachedCriteria.forClass(AccountCombination.class);
		acctCombinationCriteria.setProjection(Projections.property(AccountCombination.FIELD.divisionId.name()));
		acctCombinationCriteria.add(Restrictions.eq(AccountCombination.FIELD.active.name(), true));
		acctCombinationCriteria.add(Restrictions.eq(AccountCombination.FIELD.companyId.name(), companyId));
		dc.add(Subqueries.propertyIn(Division.Field.id.name(), acctCombinationCriteria));
		return getAll(dc);
	}
}