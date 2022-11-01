package eulap.eb.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;
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
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.WarehouseDao;
import eulap.eb.domain.hibernate.CashSaleItem;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.Warehouse;
import eulap.eb.web.dto.WarehouseDto;

/**
 * Implementation class of {@link WarehouseDao}


 */
public class WarehouseDaoImpl extends BaseDao<Warehouse> implements WarehouseDao{

	@Override
	protected Class<Warehouse> getDomainClass() {
		return Warehouse.class;
	}

	@Override
	public List<Warehouse> getWarehouseList(Integer companyId) {
		return getAll(getWarehouses(companyId, null));
	}

	private DetachedCriteria getWarehouses(Integer companyId, Integer divisionId) {
		DetachedCriteria dc = getDetachedCriteria();
		if(companyId != null){
			dc.add(Restrictions.eq(Warehouse.FIELD.companyId.name(), companyId));
			dc.add(Restrictions.eq(Warehouse.FIELD.active.name(), true));
		}
		if(divisionId != null) {
			dc.add(Restrictions.eq(Warehouse.FIELD.divisionId.name(), divisionId));
		}
		dc.addOrder(Order.asc(Warehouse.FIELD.name.name()));
		return dc;
	}

	@Override
	public List<Warehouse> getWarehouseList(Integer companyId, Integer divisionId) {
		return getAll(getWarehouses(companyId, divisionId));
	}

	@Override
	public Page<Warehouse> getAllWarehouseList(Integer companyId, String name, String address, SearchStatus status, PageSetting PageNumber) {
		DetachedCriteria warehouseCriteria = getDetachedCriteria();
		if(!name.isEmpty()){
			warehouseCriteria.add(Restrictions.like(Warehouse.FIELD.name.name(), "%"+name+"%"));
			if(!address.isEmpty()){
				warehouseCriteria.add(Restrictions.like(Warehouse.FIELD.address.name(), "%"+address+"%"));
			}
		}
		if(!address.isEmpty()){
			warehouseCriteria.add(Restrictions.like(Warehouse.FIELD.address.name(), "%"+address+"%"));
			if(!name.isEmpty()){
				warehouseCriteria.add(Restrictions.like(Warehouse.FIELD.name.name(), "%"+name+"%"));
			}
		}
		if(companyId != -1) {
			warehouseCriteria.add(Restrictions.eq(Warehouse.FIELD.companyId.name(), companyId));
		}
		warehouseCriteria = DaoUtil.setSearchStatus(warehouseCriteria, Warehouse.FIELD.active.name(), status);

		warehouseCriteria.createAlias("company", "company").addOrder(Order.asc("company.name"));
		warehouseCriteria.addOrder(Order.asc(Warehouse.FIELD.name.name()));
		return getAll(warehouseCriteria, PageNumber);
	}

	@Override
	public boolean isUniqueWarehouseName(String name, Integer companyId, int id) {		
		DetachedCriteria warehouseCriteria = getDetachedCriteria();
		warehouseCriteria.add(Restrictions.eq(Warehouse.FIELD.name.name(), name.trim()));
		if(id != 0) {
			warehouseCriteria.add(Restrictions.ne(Warehouse.FIELD.id.name(), id));
		}
		
		if(companyId != null) {
			warehouseCriteria.add(Restrictions.eq(Warehouse.FIELD.companyId.name(), companyId));
		}
		return getAll(warehouseCriteria).isEmpty();
	}

	@Override
	public List<Warehouse> getWHsByUserCompany(Integer companyId, User user, boolean isActiveOnly) {
		DetachedCriteria dc = getDetachedCriteria();
		if(companyId != null){
			dc.add(Restrictions.eq(Warehouse.FIELD.companyId.name(), companyId));
		}
		if (isActiveOnly) {
			dc.add(Restrictions.eq(Warehouse.FIELD.active.name(), true));
		}
		addUserCompany(dc, user);
		return getAll(dc);
	}

	@Override
	public List<Warehouse> getListOfWarehousePerCashSaleId(Integer cashSaleId) {
		DetachedCriteria dc = getDetachedCriteria();

		DetachedCriteria cashSalItemDc = DetachedCriteria.forClass(CashSaleItem.class);
		cashSalItemDc.setProjection(Projections.property(CashSaleItem.FIELD.warehouseId.name()));
		cashSalItemDc.add(Restrictions.eq(CashSaleItem.FIELD.cashSaleId.name(), cashSaleId));

		dc.add(Subqueries.propertyIn(Warehouse.FIELD.id.name(), cashSalItemDc));
		return getAll(dc);
	}

	@Override
	public List<Warehouse> getWarehousesWithInactive(Integer companyId, Integer warehouseId, Integer divisionId) {
		DetachedCriteria dc = getDetachedCriteria();
		if(companyId != null){
			dc.add(Restrictions.eq(Warehouse.FIELD.companyId.name(), companyId));
		}
		if(warehouseId != null) {
			dc.add(Restrictions.or(Restrictions.eq(Warehouse.FIELD.active.name(), true),
					Restrictions.eq(Warehouse.FIELD.id.name(), warehouseId)));
		} else {
			dc.add(Restrictions.eq(Warehouse.FIELD.active.name(), true));
		}
		if(divisionId != null) {
			dc.add(Restrictions.eq(Warehouse.FIELD.divisionId.name(), divisionId));
		}
		dc.addOrder(Order.asc(Warehouse.FIELD.name.name()));
		return getAll(dc);
	}

	@Override
	public Warehouse getWarehouse(Integer companyId, String name) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(Warehouse.FIELD.name.name(), name.trim()));
		dc.add(Restrictions.eq(Warehouse.FIELD.companyId.name(), companyId));
		return get(dc);
	}

	@Override
	public List<Warehouse> getParentWarehouses(Integer companyId, Integer divisionId, Integer warhouseId,
			String name, boolean isExact, boolean isActiveOnly) {
		//TODO: This method is incomplete, add filter to determine if warehouse a parent warehouse.
		return getAll(getWarehouse(companyId, divisionId, warhouseId, name, isExact, isActiveOnly));
	}

	private DetachedCriteria getWarehouse(Integer companyId, Integer divisionId, Integer warhouseId,
			String name, boolean isExact, boolean isActiveOnly) {
		DetachedCriteria dc = getDetachedCriteria();
		if (isActiveOnly) {
			dc.add(Restrictions.eq(Warehouse.FIELD.active.name(), true));
		}
		if (name != null && !name.isEmpty()) {
			if (isExact) {
				dc.add(Restrictions.eq(Warehouse.FIELD.name.name(),
						StringFormatUtil.removeExtraWhiteSpaces(name)));
			} else {
				dc.add(Restrictions.like(Warehouse.FIELD.name.name(),
						StringFormatUtil.appendWildCard(name)));
			}
		}
		if(companyId != null) {
			dc.add(Restrictions.eq(Warehouse.FIELD.companyId.name(), companyId));
		}
		if(divisionId != null) {
			dc.add(Restrictions.eq(Warehouse.FIELD.divisionId.name(), divisionId));
		}
		if (warhouseId != null) {
			dc.add(Restrictions.ne(Warehouse.FIELD.id.name(), warhouseId));
		}
		return dc;
	}

	@Override
	public Page<WarehouseDto> getWarehouseWithSubs(Integer companyId, Integer divisionId, String warehouseName, String address,
			SearchStatus searchStatus, boolean isMainWarehouseOnly, PageSetting pageSetting) {
		String sql = "SELECT W.WAREHOUSE_ID, C.NAME AS COMPANY_NAME, D.NAME AS DIVISION, W.NAME AS WAREHOUSE_NAME, W.ADDRESS, "
				+ "PW.WAREHOUSE_ID AS PARENT_WAREHOUSE_ID, COALESCE(PW.NAME, '') AS PARENT_WAREHOUSE_NAME, W.ACTIVE "
				+ "FROM WAREHOUSE W "
				+ "INNER JOIN COMPANY C ON C.COMPANY_ID = W.COMPANY_ID "
				+ "INNER JOIN DIVISION D ON D.DIVISION_ID = W.DIVISION_ID "
				+ "LEFT JOIN WAREHOUSE PW ON PW.WAREHOUSE_ID = W.PARENT_WAREHOUSE_ID ";
		Integer searchStatusAsId = null;
		if (searchStatus == SearchStatus.All) {
			searchStatusAsId = -1;
			sql += "WHERE W.ACTIVE != ? ";
		} else {
			if (searchStatus == SearchStatus.Active ) {
				searchStatusAsId = 1;
			} else if (searchStatus == SearchStatus.Inactive ) {
				searchStatusAsId = 0;
			}
			sql += "WHERE W.ACTIVE = ? ";
		}
		sql += (isMainWarehouseOnly ? "AND W.PARENT_WAREHOUSE_ID IS NULL " : "");
		if (!warehouseName.isEmpty()) {
			String tmpName = warehouseName.replace("%", "\\%").replace("_", "\\_");
			sql += "AND W.NAME LIKE '%" + tmpName + "%' ";
		}
		if (!address.isEmpty()) {
			String tmpAddress = address.replace("%", "\\%").replace("_", "\\_");
			sql += "AND W.ADDRESS LIKE '%" + tmpAddress + "%' ";
		}
		if(divisionId != null && divisionId != -1) {
			sql += "AND W.DIVISION_ID = ? ";
		}
		sql += "ORDER BY C.NAME, W.NAME";
		return getAllAsPage(sql, pageSetting, new WarehouseDtoHandler(this, searchStatusAsId, divisionId));
	}

	private static class WarehouseDtoHandler implements QueryResultHandler<WarehouseDto> {
		private WarehouseDaoImpl divisionDaoImpl;
		private Integer searchStatusAsId;
		private Integer divisionId;

		private WarehouseDtoHandler(WarehouseDaoImpl divisionDaoImpl, Integer searchStatusAsId, Integer divisionId) {
			this.divisionDaoImpl = divisionDaoImpl;
			this.searchStatusAsId = searchStatusAsId;
			this.divisionId = divisionId;
		}

		@Override
		public List<WarehouseDto> convert(List<Object[]> queryResult) {
			List<WarehouseDto> mainDivs = new ArrayList<>();
			WarehouseDto dto = null;
			for (Object[] rowResult : queryResult) {
				int colNum = 0;
				Integer id = (Integer) rowResult[colNum++];
				String companyName = (String) rowResult[colNum++];
				String divisionName = (String) rowResult[colNum++];
				String name = (String) rowResult[colNum++];
				String address = (String) rowResult[colNum++];
				Integer parentWarehouseId = (Integer) rowResult[colNum++];
				String parentWarehousName = (String) rowResult[colNum++];
				boolean active = (Boolean) rowResult[colNum++];
				dto = WarehouseDto.getInstanceOf(id, companyName, name, address, parentWarehouseId,
						parentWarehousName, active, this.divisionDaoImpl.isLastLevel(id, searchStatusAsId),
						divisionName);
				dto.setMainParent(true);
				mainDivs.add(dto);
			}
			return mainDivs;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			if (searchStatusAsId == null) {
				query.setParameter(index, 1);
			} else {
				query.setParameter(index, searchStatusAsId);
			}
			if(divisionId != null && divisionId != -1) {
				query.setParameter(++index, divisionId);
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("WAREHOUSE_ID", Hibernate.INTEGER);
			query.addScalar("COMPANY_NAME", Hibernate.STRING);
			query.addScalar("DIVISION", Hibernate.STRING);
			query.addScalar("WAREHOUSE_NAME", Hibernate.STRING);
			query.addScalar("ADDRESS", Hibernate.STRING);
			query.addScalar("PARENT_WAREHOUSE_ID", Hibernate.INTEGER);
			query.addScalar("PARENT_WAREHOUSE_NAME", Hibernate.STRING);
			query.addScalar("ACTIVE", Hibernate.BOOLEAN);
		}
	}

	@Override
	public boolean isLastLevel(Integer warehouseId, Integer searchStatusId) {
		boolean isLastLevel;
		String sql = "SELECT WAREHOUSE_ID FROM WAREHOUSE "
				+ "WHERE WAREHOUSE_ID NOT IN ("
				+ "SELECT PARENT_WAREHOUSE_ID FROM WAREHOUSE "
				+ "WHERE PARENT_WAREHOUSE_ID IS NOT NULL ";
		if (searchStatusId >= 0) {
			sql += "AND ACTIVE = ?) ";
		} else {
			sql += "AND ACTIVE != ?) ";
		}
		sql += "AND WAREHOUSE_ID = ? ";
		Session session = null;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(0, searchStatusId);
			query.setParameter(1, warehouseId);
			isLastLevel = !query.list().isEmpty();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return isLastLevel;
	}

	@Override
	public List<WarehouseDto> getAllChildren(Integer warehouseId, int statusId) {
		String sql = "SELECT W.WAREHOUSE_ID, C.NAME AS COMPANY_NAME, W.NAME AS WAREHOUSE_NAME, W.ADDRESS, "
				+ "PW.WAREHOUSE_ID AS PARENT_WAREHOUSE_ID, COALESCE(PW.NAME, '') AS PARENT_WAREHOUSE_NAME, W.ACTIVE "
				+ "FROM WAREHOUSE W "
				+ "INNER JOIN COMPANY C ON C.COMPANY_ID = W.COMPANY_ID "
				+ "INNER JOIN WAREHOUSE PW ON PW.WAREHOUSE_ID = W.PARENT_WAREHOUSE_ID ";
		if (statusId == -1) {
			sql += "WHERE W.ACTIVE != ? ";
		} else {
			sql += "WHERE W.ACTIVE = ? ";
		}
		sql += "AND PW.WAREHOUSE_ID = " + warehouseId;
		return (List<WarehouseDto>) getAllAsPage(sql, new PageSetting(PageSetting.START_PAGE, 
				PageSetting.NO_PAGE_CONSTRAINT), new WarehouseDtoHandler(this, statusId, null)).getData();
	}
}
