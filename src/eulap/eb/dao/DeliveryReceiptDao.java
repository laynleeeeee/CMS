package eulap.eb.dao;

import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.DeliveryReceipt;
import eulap.eb.domain.hibernate.DeliveryReceiptType;
import eulap.eb.service.fap.FormPluginParam;
import eulap.eb.service.report.DeliveryReceiptRegisterParam;
import eulap.eb.web.dto.DeliveryReceiptRegisterDto;
import eulap.eb.web.dto.DrReferenceDto;

/**
 * Data access object for {@link DeliveryReceipt}

 */

public interface DeliveryReceiptDao extends Dao<DeliveryReceipt> {

	/**
	 * Generate sequence number.
	 * @param companyId The company id.
	 * @return The sequence number.
	 */
	int generateSeqNo(int companyId, Integer typeId);

	/**
	 * Get the paged delivery receipts.
	 * @param type id The delivery receipt type id
	 * @param param The search parameter.
	 * @return The paged delivery receipts.
	 */
	Page<DeliveryReceipt> getDeliveryReceipts(int typeId, FormPluginParam param);

	/**
	 * Filter {@link DeliveryReceipt}
	 * @param criteria The searchCriteria
	 * @param pageSetting The page setting.
	 * @param typeId The delivery receipt type id.
	 * @return The paged collection of {@link DeliveryReceipt}
	 */
	Page<DeliveryReceipt> searchDeliveryReceipts(Integer typeId, String criteria, PageSetting pageSetting);

	/**
	 * Get the paged list of delivery receipt form references
	 * @param companyId The company id
	 * @param arCustomerId The customer id
	 * @param arCustomerAccountId The customer account id
	 * @param drNumber The DR number
	 * @param statusId The form status id
	 * @param dateFrom The start date
	 * @param dateTo The end date
	 * @param pageSetting The page setting
	 * @return The paged list of delivery receipt form references
	 */
	Page<DeliveryReceipt> getDRReferences(Integer companyId, Integer divisionId, Integer arCustomerId, Integer arCustomerAccountId,
			Integer drNumber, Integer statusId, Date dateFrom, Date dateTo, String drRefNumber, Integer typeId, PageSetting pageSetting);

	/**
	 * Get the list of delivery receipts by authority to withdraw id.
	 * @param soId The sales order id.
	 * @return The list of DRs.
	 */
	List<DeliveryReceipt> getDRsByATWId(Integer atwId);

	/**
	 * Get the list of DR reference DTOs
	 * @return The list of DR reference DTOs
	 */
	List<DrReferenceDto> getDrReferenceDtos();

	/**
	 * Get the DR form by object and type id
	 * @param drId The object id
	 * @param drTypeId The type id
	 * @return The DR form object
	 */
	DeliveryReceipt getDrByTypeId(Integer drId, Integer drTypeId);

	/**
	 * Get the list of {@link DeliveryReceipt} by sales order id and {@link DeliveryReceiptType}.
	 * @param soId The sales order id.
	 * @param drTypeId The {@link DeliveryReceiptType} id.
	 * @return The list of {@link DeliveryReceipt}.
	 */
	List<DeliveryReceipt> getUsedDrsBySoId(Integer soId, Integer drTypeId);

	/**
	 * Search the Delivery Reciept Register.
	 * @param param The object that holds the search parameters.
	 * @return The paged result.
	 */
	List<DeliveryReceiptRegisterDto> searchDeliveryReceiptsRegister(DeliveryReceiptRegisterParam param);
}
