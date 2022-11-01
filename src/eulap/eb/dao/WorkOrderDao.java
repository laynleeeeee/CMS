package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.ArCustomer;
import eulap.eb.domain.hibernate.ArCustomerAccount;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.RequisitionForm;
import eulap.eb.domain.hibernate.RequisitionType;
import eulap.eb.domain.hibernate.WorkOrder;
import eulap.eb.web.dto.ApprovalSearchParam;

/**
 * Data access object interface for {@link WorkOrder}

 */

public interface WorkOrderDao extends Dao<WorkOrder> {

	/**
	 * Generate form sequence number
	 * @param companyId The company id
	 * @return The generated form sequence number
	 */
	Integer generateSequenceNo(int companyId);

	/**
	 * 
	 * @param searchParam
	 * @param statuses
	 * @param pageSetting
	 * @return
	 */
	Page<WorkOrder> getWorkOrders(ApprovalSearchParam searchParam, List<Integer> statuses,
			PageSetting pageSetting);

	/**
	 * 
	 * @param searchCriteria
	 * @param pageSetting
	 * @return
	 */
	Page<WorkOrder> retrieveWorkOrders(String searchCriteria, PageSetting pageSetting);

	/**
	 * Get the paged list of work order for sub work order reference
	 * @param companyId The company id
	 * @param woNumber The work order number
	 * @param arCustomerId The customer id
	 * @param arCustomerAcctId The customer account id
	 * @param statusId The status id
	 * @param pageSetting The page setting
	 * @return The paged list of work order for sub work order reference
	 */
	Page<WorkOrder> getWorkOrderReferences(Integer companyId, Integer woNumber, Integer arCustomerId,
			Integer arCustomerAcctId, Integer statusId, PageSetting pageSetting);

	/**
	 * Get the list of sub work orders
	 * @param refWorkOrderId Thw reference work order id
	 * @return The list of sub work orders
	 */
	List<WorkOrder> getSubWorkOrders(int refWorkOrderId);

	/**
	 * Get the remaining serial item quantity.
	 * @param soId The reference sales order id.
	 * @param itemId The item id.
	 * @return The remaining serial item quantity.
	 */
	Double getSerialItemQty(Integer soId, Integer itemId);

	/**
	 * Get the remaining purchased item quantity.
	 * @param soId The reference sales order id.
	 * @param itemId The item id.
	 * @return The remaining purchased item quantity.
	 */
	Double getPurchasedItemsQty(Integer soId, Integer itemId);

	/**
	 * Get the list of work orders by sales order id.
	 * @param soId The sales order id.
	 * @return The list of work orders.
	 */
	List<WorkOrder> getWoBySalesOrderId(Integer soId);

	/**
	 * Get the {@link WorkOrder} reference for {@link RequisitionForm} with {@link RequisitionType} 6.
	 * {@link RequisitionType} 6 = Admin.
	 * @param companyId The {@link Company} id.
	 * @param woNumber The {@link WorkOrder} sequence number.
	 * @param arCustomerId The {@link ArCustomer} id.
	 * @param arCustomerAcctId The {@link ArCustomerAccount} id.
	 * @param statusId The status id.
	 * @param pageSetting The {@link PageSetting}.
	 * @return List of {@link WorkOrder}.
	 */
	Page<WorkOrder> getMrWoReferences(Integer companyId, Integer woNumber, Integer arCustomerId,
			Integer arCustomerAcctId, Integer statusId, PageSetting pageSetting);

	/**
	 * Check if {@link WorkOrder} is used by {@link RequisitionForm} as reference.
	 * @param workOrderId The {@link WorkOrder} id.
	 * @return True if {@link WorkOrder} is used as reference, otherwise false.
	 */
	boolean isUsedByRf(Integer workOrderId);
}
