package eulap.eb.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.dao.EBObjectDao;
import eulap.eb.dao.WarehouseDao;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.Warehouse;
import eulap.eb.web.dto.WarehouseDto;

/**
 * A class that handles the business logic of Warehouse


 */
@Service
public class WarehouseService {
	@Autowired
	private WarehouseDao warehouseDao;
	@Autowired
	private ItemService itemService;
	@Autowired
	private EBObjectDao ebObjectDao;
	private static Logger logger = Logger.getLogger(Warehouse.class);

	/**
	 * Get the Warehouse using the id.
	 * @param warehouseID The unique id of the Warehouse.
	 * @return The Warehouse object.
	 */
	public Warehouse getWarehouse(int warehouseId) {
		Warehouse warehouse = warehouseDao.get(warehouseId);
		Integer parentWarehouseId = warehouse.getParentWarehouseId();
		if (parentWarehouseId != null) {
			Warehouse parentWarehouse = warehouseDao.get(parentWarehouseId);
			warehouse.setParentWarehouseName(parentWarehouse.getName());
		}
		return warehouse;
	}
	/**
	 * Get all Warehouse List.
	 * @param companyId The id of the company.
	 * @param pageNumber The page number.
	 * @return The list of Warehouse in paged format.
	 */
	public Page<Warehouse> getWarehouse(Integer companyId, String name, String address, String status, Integer pageNumber) {
		logger.info("Retrieving Warehouse.");
		SearchStatus searchStatus = SearchStatus.getInstanceOf(status);
		logger.debug("Searching for the parameters: Company="+companyId+", name="+name.trim() + ", status="+status);
		return warehouseDao.getAllWarehouseList(companyId, name.trim(), address, searchStatus, new PageSetting(pageNumber, 25));
	}
	
	/**
	 * Save the warehouse.
	 */
	public void saveWarehouse(Warehouse warehouse, User user) {
		logger.info("Saving the Warehouse.");
		Integer warehouseId = warehouse.getId();
		boolean isNew = warehouseId == 0;
		if(isNew){
			EBObject eb = new EBObject();
			AuditUtil.addAudit(eb, new Audit(user.getId(), true, new Date()));
			eb.setObjectTypeId(Warehouse.OBJECT_TYPE_ID);
			ebObjectDao.save(eb);
			warehouse.setEbObjectId(eb.getId());
		}
		AuditUtil.addAudit(warehouse, new Audit(user.getId(), isNew, new Date()));
		warehouse.setCompanyId(warehouse.getCompanyId());
		warehouse.setName(warehouse.getName().trim());
		warehouse.setAddress(warehouse.getAddress().trim());
		warehouseDao.saveOrUpdate(warehouse);
		logger.info("Successfully saved the Warehouse: "+warehouse.getName());
	}
	
	/**
	 * Get the list of warehouse.
	 * @param companyId The selected company id.
	 * @return The list of warehouse.
	 */
	public List<Warehouse> getWarehouseList(Integer companyId) {
		List<Warehouse> warehouseList = warehouseDao.getWarehouseList(companyId);
		if(warehouseList ==  null || warehouseList.isEmpty())
			return Collections.emptyList();
		return warehouseList;
	}

	/**
	 * Get the list of warehouse.
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @return The list of warehouses.
	 */
	public List<Warehouse> getWarehouseList(Integer companyId, Integer divisionId) {
		List<Warehouse> warehouseList = warehouseDao.getWarehouseList(companyId, divisionId);
		if(warehouseList ==  null || warehouseList.isEmpty())
			return Collections.emptyList();
		return warehouseList;
	}

	/**
	 * Get the list of warehouses by company and user company.
	 * @param companyId The company filter.
	 * @param user The logged user.
	 * @param True to get only all active warehouses, otherwise get all warehouses.
	 * @param warehouseId The warehouse filter.
	 * @return List of warehouses.
	 */
	public List<Warehouse> getWHsByUserCompany(Integer companyId, User user, boolean isActiveOnly, Integer warehouseId) {
		List<Warehouse> warehouses = warehouseDao.getWHsByUserCompany(companyId, user, isActiveOnly);
		if (warehouses != null && !warehouses.isEmpty() && isActiveOnly && warehouseId != null) {
			Warehouse warehouse = warehouseDao.get(warehouseId);
			if (warehouse!= null && !warehouse.isActive()) {
				warehouses.add(warehouse);
			}
		}
		return warehouses;
	}

	/**
	 * Get the list of {@link Warehouse} with existing stocks of the itemId parameter.
	 * @param companyId The id of the company.
	 * @param itemId The id of the selected item.
	 * @param warehouseId The warehouse id.
	 * @return The list of warehouses with the existing stocks of the item.
	 */
	public List<Warehouse> getWarehousesWithES(int companyId, int itemId, Integer warehouseId, Integer divisionId) {
		List<Warehouse> warehouses = getWarehouseList(companyId, divisionId);
		if(warehouseId != null && warehouseId != 0) {
			Warehouse warehouse = warehouseDao.get(warehouseId);
			//Add warehouse if the warehouse is inactive to avoid duplicate warehouse in the list.
			if(!warehouse.isActive()) {
				if(warehouses.isEmpty()) {
					//if warehouses is an empty list. Reinitialize the list as an array list. Since emptyList are immutable.
					warehouses = new ArrayList<Warehouse>();
				}
				warehouses.add(warehouse);
			}
		}
		for (Warehouse wh : warehouses) {
			double existingStocks = itemService.getItemExistingStocks(itemId, wh.getId());
			wh.setExistingStocks(existingStocks);
		}
		return warehouses;
	}

	/**
	 * Checks if the warehouse name is unique.
	 * @param name The name of the warehouse.
	 * @param companyId The company id.
	 * @param id The id of the warehouse.
	 * @return True if the name of warehouse is unique, otherwise false.
	 */
	public boolean isUniqueWarehouseName(String name, Integer companyId, int id) {	
		return warehouseDao.isUniqueWarehouseName(name, companyId, id);
	}

	/**
	 * Get all the list of warehouse from cash sales.
	 * @param cashSaleId The cash sale id.
	 * @return The list of warehouse id from cash sales.
	 */
	public List<Integer> getListOfWarehouseIdsPerCashSaleId(Integer cashSaleId){
		List<Warehouse> warehouses = warehouseDao.getListOfWarehousePerCashSaleId(cashSaleId);
		List<Integer> warehouseIds = new ArrayList<>();
		for (Warehouse warehouse : warehouses) {
			warehouseIds.add(warehouse.getId());
		}
		return warehouseIds;
	}

	/**
	 * Get the list of warehouses with inactive.
	 * @param companyId The id of the company.
	 * @param itemId The id of the Item.
	 * @param warehouseId The id of the warehouse.
	 * @return The lists of warehouses with inactive.
	 */
	public List<Warehouse> getWarehousesWithInactive(Integer companyId, Integer itemId, Integer warehouseId){
		List<Warehouse> warehouses = warehouseDao.getWarehousesWithInactive(companyId, warehouseId, null);
		for (Warehouse wh : warehouses) {
			double existingStocks = itemService.getItemExistingStocks(itemId, wh.getId());
			wh.setExistingStocks(existingStocks);
		}
		return warehouses;
	}

	/**
	 * Get the list of warehouses with inactive.
	 * @param companyId The id of the company.
	 * @param warehouseId The id of the warehouse.
	 * @return The lists of warehouses with inactive.
	 */
	public List<Warehouse> getWarehouses(Integer companyId, Integer warehouseId, Integer divisionId){
		return warehouseDao.getWarehousesWithInactive(companyId, warehouseId, divisionId);
	}

	/**
	 * Get the warehouse by company and name.
	 * @param companyId The company id.
	 * @param name The warehouse name.
	 * @return {@link Warehouse}
	 */
	public Warehouse getWarehouse(Integer companyId, String name) {
		return warehouseDao.getWarehouse(companyId, name);
	}

	/**
	 * Get the list of warehouse as parent
	 * @param companyId The company id
	 * @param divisionId The division id.
	 * @param warehouseId The warehouse id
	 * @param name The warehouse name
	 * @param isExact True if exact name, otherwise false
	 * @param isActiveOnly True if retrieve active warehouse only, otherwise all
	 * @return The list of warehouse as parent
	 */
	public List<Warehouse> getParentWarehouses(Integer companyId, Integer divisionId, Integer warehouseId,
			String name, boolean isExact, boolean isActiveOnly) {
		return warehouseDao.getParentWarehouses(companyId, divisionId, warehouseId, name, isExact, isActiveOnly);
	}

	/**
	 * Get paged the list of warehouses with sub levels
	 * @param companyId The company id
	 * @param divisionId The division id.
	 * @param warehouseName The warehouse name
	 * @param address The warehouse address
	 * @param status The warehouse setting status
	 * @param pageNumber The page number
	 * @return The paged list of warehouses with sub levels
	 */
	public Page<WarehouseDto> getWarehouseWithSubs(Integer companyId, Integer divisionId, String warehouseName,
			String address, String status, Integer pageNumber) {
		SearchStatus searchStatus = SearchStatus.getInstanceOf(status);
		boolean isMainWarehouseOnly = warehouseName.trim().isEmpty();
		return warehouseDao.getWarehouseWithSubs(companyId, divisionId, warehouseName, address, searchStatus,
				isMainWarehouseOnly, new PageSetting(pageNumber, PageSetting.MAX_ADMIN_RECORD));
	}

	/**
	 * Get the list of warehouse children
	 * @param warehouseId The warehouse id
	 * @param isActive True if get all active only, otherwise all
	 * @return The list of warehouse children
	 */
	public List<WarehouseDto> getAllChildren(Integer warehouseId, String status) {
		int statusId = -1;//default all status id.
		SearchStatus searchStatus = SearchStatus.getInstanceOf(status);
		if (searchStatus == SearchStatus.Active ) {
			statusId = 1;
		} else if (searchStatus == SearchStatus.Inactive ) {
			statusId = 0;
		}
		List<WarehouseDto> children = warehouseDao.getAllChildren(warehouseId, statusId);
		addChildren(children, statusId);
		return children;
	}

	private void addChildren(List<WarehouseDto> children,  int statusId) {
		List<WarehouseDto> tmpDtos = null;
		if (!children.isEmpty()) {
			for (WarehouseDto dto : children) {
				tmpDtos = warehouseDao.getAllChildren(dto.getId(), statusId);
				if (!tmpDtos.isEmpty()) {
					dto.setChildrenWarehouse(tmpDtos);
					addChildren(tmpDtos, statusId);
				}
			}
		}
	}
}
