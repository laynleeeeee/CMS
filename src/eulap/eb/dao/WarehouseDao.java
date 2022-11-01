package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.Warehouse;
import eulap.eb.web.dto.WarehouseDto;

/**
 * DAO (Data-Access-Object) of {@link Warehouse}


 */
public interface WarehouseDao extends Dao<Warehouse>{

	/**
	 * Get the list of warehouse under a company.
	 * @param companyId The selected company id.
	 * @return The list of warehouse.
	 */
	List<Warehouse> getWarehouseList(Integer companyId);

	/**
	 * Get the list of warehouse.
	 * @param companyId The selected company id.
	 * @param divisionId The division id.
	 * @return The list of warehouse.
	 */
	List<Warehouse> getWarehouseList(Integer companyId, Integer divisionId);

	/**
	 * Get all the warehouse.
	 * @param companyId The id of the company.
	 * @param name The name of the warehouse.
	 * @param status The status of the warehouse.
	 * @param pageSetting The page setting.
	 * @return The list of warehouse in page format.
	 */
	Page<Warehouse> getAllWarehouseList(Integer companyId, String name, String address, SearchStatus status, PageSetting PageNumber);

	/**
	 * Checks if the warehouse name is unique.
	 * @param name The name of the warehouse.
	 * @param companyId The company id.
	 * @param id The id of the warehouse.
	 * @return True if the name of warehouse is unique, otherwise false.
	 */
	boolean isUniqueWarehouseName(String name, Integer companyId, int id);

	/**
	 * Get the list of warehouses by company and user company.
	 * @param companyId The company filter.
	 * @param user The logged user.
	 * @param True to get only all active warehouses, otherwise get all warehouses.
	 * @return List of warehouses.
	 */
	List<Warehouse> getWHsByUserCompany (Integer companyId, User user, boolean isActiveOnly);

	/**
	 * Get the list of warehouses per cash sales id.
	 * @param cashSaleId The cash sales id.
	 * @return The list of warehouses per cash sales id.
	 */
	List<Warehouse> getListOfWarehousePerCashSaleId(Integer cashSaleId);

	/**
	 * Get the list of warehouses with inactive.
	 * @param companyId The id of the company.
	 * @param warehouseId The id of the warehouse.
	 * @return The lists of warehouses with inactive.
	 */
	List<Warehouse> getWarehousesWithInactive(Integer companyId, Integer warehouseId, Integer divisionId);

	/**
	 * Get the warehouse by company and name.
	 * @param companyId The company id.
	 * @param name The warehouse name.
	 * @return {@link Warehouse}
	 */
	Warehouse getWarehouse(Integer companyId, String name);

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
	List<Warehouse> getParentWarehouses(Integer companyId, Integer divisionId, Integer warehouseId,
			String name, boolean isExact, boolean isActiveOnly);

	/**
	 * Get the list of warehouses with sub levels
	 * @param companyId The company id
	 * @param divisionId The division id.
	 * @param warehouseName The warehouse name
	 * @param address The warehouse address
	 * @param searchStatus The search status
	 * @param isMainWarehouseOnly True if retrieve the parent warehouses only, otherwise all
	 * @param pageSetting The page setting
	 * @return The list of warehouses with sub levels
	 */
	Page<WarehouseDto> getWarehouseWithSubs(Integer companyId, Integer divisionId, String warehouseName, String address,
			SearchStatus searchStatus, boolean isMainWarehouseOnly, PageSetting pageSetting);

	/**
	 * Check if the warehouse is the last level
	 * @param warehouseId The warehouse id
	 * @param searchStatusId The search status id
	 * @return True if the warehouse is at last level, otherwise false
	 */
	boolean isLastLevel(Integer warehouseId, Integer searchStatusId);

	/**
	 * Get the list of warehouse children
	 * @param warehouseId The warehouse id
	 * @param isActive True if get all active only, otherwise all
	 * @return The list of warehouse children
	 */
	List<WarehouseDto> getAllChildren(Integer warehouseId, int statusId);
}
