package eulap.eb.dao;

import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.ArInvoice;
import eulap.eb.domain.hibernate.ArReceipt;
import eulap.eb.domain.hibernate.CustomerAdvancePayment;
import eulap.eb.domain.hibernate.DeliveryReceipt;
import eulap.eb.domain.hibernate.SalesOrder;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.SalesPoMonitoringDto;
import eulap.eb.web.dto.SalesReportDto;
import eulap.eb.web.dto.SalesDeliveryEfficiencyDto;
import eulap.eb.web.dto.SalesOrderRegisterDto;

/**
 * Data access object for {@link SalesOrder}

 */

public interface SalesOrderDao extends Dao<SalesOrder> {

	/**
	 * Generate form sequence number
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @return The generated form sequence number
	 */
	Integer generateSequenceNo(int companyId, Integer divisionId);

	/**
	 * Get the paged list of {@link SalesOrder} for approval view
	 * @param searchParam The search parameter object
	 * @param formStatusIds The list of form status ids
	 * @param pageSetting The page setting
	 * @return The paged list of {@code SalesQuotation} for approval view
	 */
	Page<SalesOrder> getSalesOrders(int typeId, ApprovalSearchParam searchParam, List<Integer> formStatusIds, PageSetting pageSetting);

	/**
	 * Get the paged list of sales order for ATW reference
	 * @param companyId The company id
	 * @param soNumber The sales order sequence number
	 * @param arCustomerId The AR customer id
	 * @param arCustomerAcctId The AR customer account id id
	 * @param statusId The reference form status id (ALL, USED, and UNUSED)
	 * @param pageSetting The page setting
	 * @return The paged list of sales order for ATW reference
	 */
	Page<SalesOrder> getSaleOrderReferences(Integer companyId, Integer soNumber, Integer arCustomerId,
			Integer arCustomerAcctId, Integer statusId, PageSetting pageSetting);

	/**
	 * Get the paged list for sales orders for general search
	 * @param searchCriteria The search criteria
	 * @param pageSetting The page setting
	 * @return The paged list for sales order for general search
	 */
	Page<SalesOrder> getSOForms(int typeId, String searchCriteria,  PageSetting pageSetting);

	/**
	 * Show the list of deposit sales orders for reference.
	 * @param seqNo The sequence number.
	 * @param companyId The company id.
	 * @param pageSetting The page setting.
	 * @return  The list of sales orders that meets the search criteria.
	 */
	Page<SalesOrder> showDepositSOForms(String seqNo, Integer companyId, PageSetting pageSetting);

	/**
	 * Get the sales order object by sequence number and company id.
	 * @param seqNo The sequence number.
	 * @param companyId The company id.
	 * @return The sales order object,
	 */
	SalesOrder getBySeqNo(Integer seqNo, Integer companyId);

	/**
	 * Check if the sales order form is used by customer advance payment form.
	 * @param soId The sales order id.
	 * @return True if the SO form has been referenced in CAP forms.
	 */
	boolean isUsedByCAP(Integer soId);

	/**
	 * Check if the sales order form is used by work order form.
	 * @param soId The sales order id.
	 * @return True if the SO form has been referenced in WO forms.
	 */
	boolean isUsedByWo(Integer soId);

	/**
	 * Check if sales order is used by {@link DeliveryReceipt} types 2 and 3.
	 * DeliveryReceiptType 2 = Waybill
	 * DeliveryReceiptType 3 = Equipment Utilization
	 * @param soId The sales order id.
	 * @param drTypeId The Delivery receipt type id.
	 * @return True if so is used as reference, otherwise false.
	 */
	boolean isUsedByDr(Integer soId, Integer drTypeId);

	/**
	 * Get the list of sales orders by sales quotation id.
	 * @param soId The sales quotation id.
	 * @return The list of SOs.
	 */
	List<SalesOrder> getSOsBySalesOrderId(Integer sqId);

	/**
	 * Get the paged list of sales order for ATW reference
	 * @param companyId The company id
	 * @param soNumber The sales order sequence number
	 * @param arCustomerId The AR customer id
	 * @param arCustomerAcctId The AR customer account id id
	 * @param statusId The reference form status id (ALL, USED, and UNUSED)
	 * @param pageSetting The page setting
	 * @return The paged list of sales order for ATW reference
	 */
	Page<SalesOrder> getSOServiceReferences(Integer companyId, Integer soNumber, Integer arCustomerId,
			Integer arCustomerAcctId, Integer statusId, PageSetting pageSetting);

	/**
	 * Get the list of sales order with sales order trucking line data.
	 * @param companyId The company id.
	 * @param soNumber The sales order sequence number.
	 * @param arCustomerId The ar customer id.
	 * @param arCustomerAcctId The ar customer account id.
	 * @param statusId The form current status id.
	 * @param pageSetting The {@link PageSetting} object. 
	 * @return List of {@link SalesOrder}.
	 */
	Page<SalesOrder> getSOTruckingReferences(Integer companyId, Integer soNumber, Integer arCustomerId,
			Integer arCustomerAcctId, Integer statusId, PageSetting pageSetting, Integer typeId);

	/**
	 * Get the latest encoded ship to for the selected customer
	 * @param arCustomerId The customer id
	 * @return The latest encoded ship to field
	 */
	String getCustomerShipTo(Integer arCustomerId);

	/**
	 * Get the list of {@link SalesOrder} reference for {@link DeliveryReceipt}. 
	 * @param companyId The company id.
	 * @param arCustomerId The ar customer id.
	 * @param arCustomerAcctId The ar customer account id.
	 * @param soNumber The sales order sequence number.
	 * @param statusId The status id.
	 * @param dateFrom The start date filter.
	 * @param dateTo The end date filter.
	 * @param pageSetting The pagenumber.
	 * @param drTypeId The dr type id.
	 * @return The list of {@link SalesOrder} reference.
	 */
	Page<SalesOrder> getDrSalesOrders(Integer companyId, Integer arCustomerId, Integer arCustomerAcctId,
			Integer soNumber, Integer statusId, Date dateFrom, Date dateTo, PageSetting pageSetting,
			Integer drTypeId, String poNumber, Integer divisionId);

	/**
	 * Get the list of {@link SalesOrder} reference for {@link CustomerAdvancePayment}.
	 * @param companyId The company id.
	 * @param arCustomerId The ar customer id.
	 * @param arCustomerAcctId The ar customer account id.
	 * @param soNumber The sales order sequence number.
	 * @param statusId The status id.
	 * @param dateFrom The start date filter.
	 * @param dateTo The end date filter.
	 * @param poNumber The po/pcr number.
	 * @param divisionId The division id.
	 * @param pageNumber The pagenumber.
	 * @return List of {@link SalesOrder} reference.
	 */
	Page<SalesOrder> getCapSalesOrders(Integer companyId, Integer arCustomerId, Integer arCustomerAcctId,
			Integer soNumber, Integer statusId, Date dateFrom, Date dateTo, PageSetting pageSetting,
			String poNumber, Integer divisionId);

	/**
	 * Get sales order by delivery receipt id.
	 * @param drId The delivery receipt id.
	 * @return The sales order object.
	 */
	SalesOrder getSoByDr(Integer drId);

	/**
	 * Get the list of {@link SalesOrder} with retention for {@link ArInvoice} or {@link ArReceipt} forms.
	 * @param companyId The company  Id.
	 * @param divisionId The division id.
	 * @param arCustomerId The arCustomer id.
	 * @param arCustomerAcctId the arCustomer account id.
	 * @param soNumber The sales order sequence number.
	 * @param poNumber The po number.
	 * @param dateFrom The start date filter.
	 * @param dateTo The end date filter.
	 * @param statusId The status id.
	 * @param pageSetting The {@link PageSetting}.
	 * @return The list of {@link SalesOrder} in page format.
	 */
	Page<SalesOrder> getProjectRetentionSalesOrders(Integer companyId, Integer divisionId, Integer arCustomerId, 
			Integer arCustomerAcctId, Integer soNumber, String poNumber, Date dateFrom, Date dateTo,
			Integer statusId, PageSetting pageSetting);

	/**
	 * Get the sales report data.
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @param salesPersonnelId The sales personnel id.
	 * @param dateFrom The invoice start date filter.
	 * @param dateTo The invoice end date filter.
	 * @param currencyId The currency id.
	 * @param pageSetting The {@link PageSetting}
	 * @return The sales report data.
	 */
	Page<SalesReportDto> generateSalesReport(Integer companyId, Integer divisionId, Integer salesPersonnelId, Date dateFrom, Date dateTo,
			Integer currencyId, PageSetting pageSetting);

	/**
	 * Get the list of {@link SalesDeliveryEfficiencyDto}
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @param customerId The customer id
	 * @param dateFrom The date from
	 * @param dateTo The date to
	 * @param pageSetting
	 * @return List of {@link SalesDeliveryEfficiencyDto}
	 */
	List<SalesDeliveryEfficiencyDto> getSalesDeliveryEfficiency(Integer companyId, Integer divisionId, Integer customerId, Date dateFrom, Date dateTo);

	/**
	 * Get the data to be used for the SO Register Report.
	 * @param companyId The id of the company.
	 * @param divisionId The id of the division.
	 * @param arCustomerId The id of the customer.
	 * @param arCustomerAccountId The id of the customer account, -1 for all accounts.
	 * @param soType The sales order Type, -1 for all so type, 0 for PO, 1 for PCR.
	 * @param soFrom Start sequence number of the sales order sequence number.
	 * @param soTo end sequence number of the sales order sequence number.
	 * @param popcrNo The number of po/pcr.
	 * @param dateFrom Start date of the date range.
	 * @param dateTo End date of the date range.
	 * @param statusId The id of the status {-1 = ALL, 1 = Partially Served, 2 = Fully Served , 3 = Unserved and 4 = Cancelled}
	 * @param pageSetting The page setting.
	 * @return The paged data for the report.
	 */
	Page<SalesOrderRegisterDto> getSoRegisterData(Integer companyId, Integer divisionId, Integer arCustomerId, Integer arCustomerAccountId,
			Integer soType, Integer soFrom, Integer soTo, String popcrNo, Date dateFrom, Date dateTo,
			Integer statusId, PageSetting pageSetting);
	/**
	 * Generate and process the report data.
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @param customerId The customer id.
	 * @param customerAcctId The customer account id.
	 * @param salesPersonnelId The sales personnel id.
	 * @param poNumber The po number.
	 * @param soDateFrom The so start date filter.
	 * @param soDateTo The so end date filter.
	 * @param drDateFrom The dr start date filter.
	 * @param drDateTo The dr end date filter.
	 * @param ariDateFrom The ar invoice start date filter.
	 * @param ariDateTo The ar invoice end date filter.
	 * @return The processed report data.
	 */
	List<SalesPoMonitoringDto> getSalesOutputReportData(Integer companyId, Integer divisionId, Integer customerId, Integer customerAcctId,
			Integer salesPersonnelId, String poNumber, Date soDateFrom, Date soDateTo, Date drDateFrom, Date drDateTo, Date ariDateFrom, Date ariDateTo);
}
