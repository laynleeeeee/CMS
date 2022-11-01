package eulap.eb.dao;

import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.CAPDelivery;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.ArTransactionRegisterDto;

/**
 * DAO Layer of {@link CAPDelivery} object.

 *
 */
public interface CAPDeliveryDao extends Dao<CAPDelivery>{

	/**
	 * Get the list of CAP Deliveries for approval.
	 * @param param The search parameter.
	 * @param typeId The inventory type of the Paid in Advance Delivery.
	 * @return The list of {@link CAPDelivery}
	 */
	Page<CAPDelivery> getCAPDeliveries(ApprovalSearchParam searchParam, List<Integer> formStatusIds, Integer typeId, PageSetting pageSetting);

	/**
	 * Get the list of CAP Deliveries.
	 * @param criteria The search criteria.
	 * @param typeId The inventory type of the Paid in Advance Delivery.
	 * @param pageSetting The page setting.
	 * @return The list of {@link CAPDelivery}
	 */
	Page<CAPDelivery> searchDeliveries(String criteria, int typeId, PageSetting pageSetting);

	/**
	 * Get the data to be used for the CAP Register Report.
	 * @param companyId The id of the company.
	 * @param arCustomerId The id of the customer.
	 * @param arCustomerAccountId The id of the customer account, -1 for all accounts.
	 * @param dateFrom Start date of the date range.
	 * @param dateTo End date of the date range.
	 * @param statusId The id of the status {-1 = ALL, 1 = Created, 14 = Validated, and 4 = Cancelled}
	 * @param pageSetting The page setting.
	 * @return The paged data for the report.
	 */
	Page<ArTransactionRegisterDto> generatePAIDRegister(int companyId, int arCustomerId, int arCustomerAccountId,
			Date dateFrom, Date dateTo, int statusId, PageSetting pageSetting);

	/**
	 * Generate the next available sequence number for the Paid in Advance Delivery.
	 * @param typeId The type of the inventory method.
	 * @param companyId The id of the company.
	 * @return The next available sequence number.
	 */
	int generateSequenceNo(int typeId, int companyId);

	/**
	 * Get the total delivered amount
	 * @param arTransactionId The AR transaction id
	 * @return The total delivered amount
	 */
	double getTotalDeliveredTAmt(Integer arTransactionId);

	/**
	 * Get the total WIPSO delivered amount
	 * @param arTransactionId The AR transaction id
	 * @param deliveryId The cap delivery id.
	 * @return The total delivered amount
	 */
	double getTotalDeliveredWipsoAmt(Integer arTransactionId, int deliveryId);

	/**
	 * Get the list of CAP Deliveries that are using the CAP id reference.
	 * @param customerAdvancePaymentId The id of the reference Customer Advance Payment.
	 * @return The list of {@link CAPDelivery} objects.
	 */
	List<CAPDelivery> getDeliveryByCapId(int customerAdvancePaymentId);
}
