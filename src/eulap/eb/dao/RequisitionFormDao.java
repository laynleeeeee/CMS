package eulap.eb.dao;

import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.RequisitionForm;
import eulap.eb.domain.hibernate.WorkOrder;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.PRReferenceDto;
import eulap.eb.web.dto.RfReferenceDto;
import eulap.eb.web.dto.UsedRequisitionFormDto;

/**
 * Data access object for {@link RequisitionForm}

 */
public interface RequisitionFormDao extends Dao<RequisitionForm> {

	/**
	 * Generate sequence number for requisition form by company id and type id.
	 * @param companyId The company filter.
	 * @param isPurchaseRequisition True if Purchase Requisition classification, otherwise false.
	 * @param requisitionFormTypeId The type filter.
	 * @return The generated sequence number.
	 */
	Integer generateSequenceNumber(Integer companyId,
			boolean isPurchaseRequisition, Integer requisitionFormTypeId);

	/**
	 * Get the list of Requisition Forms
	 * @param searchParam The search parameter criteria
	 * @param typeId
	 * @param formStatusIds The form status ids
	 * @param pageSetting The page setting
	 * @return The list of Requisition Forms
	 */
	Page<RequisitionForm> getRequisitionForms(ApprovalSearchParam searchParam, int typeId, 
			List<Integer> formStatusIds, boolean isPurchaseRequest,  PageSetting pageSetting);

	/**
	 * Search for requisition forms.
	 * @param criteria The search criteria
	 * @param typeId The requisition form type id
	 * @param pageSetting The page setting
	 * @return The list requisition forms
	 */
	Page<RequisitionForm> searchRequisitionForms(String criteria, int typeId, boolean isPurchaseRequest, PageSetting pageSetting);

	/**
	 * Get the remaining quantity of the purchase order reference.
	 * @param referenceObjectId The reference object id.
	 * @param wsId The withdrawal slip id.
	 * @param trId The transfer receipt id.
	 * @param prId The purchase requisition id.
	 * @param isExcludePr
	 * @return The remaining quantity.
	 */
	double getRemainingQuantity(Integer referenceObjectId, Integer wsId, Integer prId, boolean isExcludePr);

	/**
	 * Get the paged list of requisition forms available for references.
	 * @param user The logged user.
	 * @param companyId the id of the Company.
	 * @param fleetId The fleet profile id.
	 * @param projectId The customer id.
	 * @param prNumber The sequence number of the purchase requisition.
	 * @param dateFrom The start date.
	 * @param dateTo The end date.
	 * @param status The current status of the requisition form: Used, Unused, or All.
	 * @param pageSetting The page setting.
	 * @return The paged list of requisition forms available for referencing.
	 */
	Page<PRReferenceDto> getPrReferences(User user, Integer companyId, Integer fleetId, Integer projectId,
			Integer prNumber, Date dateFrom, Date dateTo, Integer status, PageSetting pageSetting);

	/**
	 * Get the list of requisition forms by job order id
	 * @param jobOrderId The job order id
	 * @param isComplete True if complete status only, else false
	 * @return The list of requisition forms
	 */
	List<RequisitionForm> getRequisitionFormsByJobOrderId(Integer jobOrderId, Boolean isComplete);

	/**
	 * Get the used requisition form for validation when canceling the form.
	 * @param requisitionFormId The id of the form to be cancelled.
	 * @return The used requisition form
	 */
	List<UsedRequisitionFormDto> getUsedRequisitionForm(Integer requisitionFormId);

	/**
	 * Get the Purchase Requisition By reference Requisition Form ID.
	 * @param pRequisitionFormId The ID of the Purchase Requisition.
	 * @param refRequisitionFormId The ID of the reference.
	 * @return The Purchase Requisition Form object.
	 */
	RequisitionForm getPrfByRefRequisitionFormId(Integer pRequisitionFormId, Integer refRequisitionFormId);

	/**
	 * Get the used Purchase Requisition Form in Purchase Order Details.
	 * @param requisitionFormID The ID of the Purchase Requisition Form.
	 * @return The details of used  Purchase Requisition Form populated in {@link UsedRequisitionFormDto} object.
	 */
	UsedRequisitionFormDto getUsedPR(Integer requisitionFormID);

	/**
	 * Get the list of requisition form references
	 * @param companyId The company id
	 * @param fleetId The fleet id
	 * @param projectId The project id
	 * @param rfNumber The requisition form number
	 * @param dateFrom The start date
	 * @param dateTo The end date
	 * @param status The form status, USED if the RF form was partially used
	 * and UNUSED if never been used as reference
	 * @param reqTypeId The requisition form type id
	 * @param isExcludePakyawanSubcon True if exclude PAKYAWAN and SUBCON RF forms
	 * @param isPakyawanSubconOnly True if retrieve PAKYAWAN and SUBCON RF forms only
	 * @param pageSetting The page setting
	 * @return The list of requisition form references
	 */
	Page<RfReferenceDto> getRfReferenceForms(Integer companyId, Integer fleetId, Integer projectId,
			Integer rfNumber, Date dateFrom, Date dateTo, Integer status, Integer reqTypeId,
			boolean isExcludePakyawanSubcon, boolean isPakyawanSubconOnly, boolean isExcludePrForms,
			PageSetting pageSetting);

	/**
	 * Get the purchase request ids.
	 */
//	List<Integer> getPrIds();

	/**
	 * Get the requisition form register report data.
	 * @param companyId The company id.
	 * @param warehouseId The warehouse id.
 	 * @param typeId The type id.
	 * @param stockCodeDesc The item id.
	 * @param rfDateFrom The requisition form from date.
	 * @param rfDateTo The requisition form to date.
	 * @param requestDateFrom The request from date.
	 * @param requestDateTo The request to date.
	 * @param fleetId The fleet id.
	 * @param projectId The project id.
	 * @param sequenceNo The requisition form sequence number.
	 * @param userId The user id.
	 * @param joNumber The job order sequence number.
	 * @param formStatus The form status id.
	 * @return requisition form register report data.
	 */
//	Page<RequisitionFormRegisterDto> getRequisitionFormRegisterDtos (Integer companyId, Integer warehouseId,
//			Integer typeId, Integer stockCodeDesc, Date rfDateFrom, Date rfDateTo, Date requestDateFrom,
//			Date requestDateTo, Integer fleetId, Integer projectId, Integer sequenceNo, Integer joNumber, Integer userId, Integer formStatus,
//			PageSetting pageSetting);

	/**
	 * Get the purchase requisition register report data.
	 * @param companyId The company id.
	 * @param warehouseId The warehouse id.
	 * @param stockCodeDesc The item id.
	 * @param prDateFrom The purchase requisition date from.
	 * @param prDateTo The purchase requisition date to.
	 * @param fleetId The fleet id.
	 * @param projectId The ar customer id.
	 * @param sequenceNo The sequence number.
	 * @param referenceNo The requisition requisition sequence number.
	 * @param userId The user id.
	 * @param pageSetting The page setting.
	 * @return Purchase requisition register report data.
	 */
//	Page<PurchaseRequisitionRegisterDto> getPurchaseRequisitionRegisterDtos (Integer companyId, Integer warehouseId, Integer typeId,
//			Integer stockCodeDesc, Date prDateFrom, Date prDateTo, Integer fleetId, Integer projectId, Integer sequenceNo, Integer referenceNo,
//			Integer userId, Integer formStatus, PageSetting pageSetting);

	/**
	 * Get Purchase Requisition by reference ID
	 * @param refReqFormId the reference requisition form
	 * @return list of requisition form
	 */
	List<RequisitionForm> getReqFormByRefId(Integer refReqFormId);

	/**
	 * Get the list of {@link RequisitionForm} by {@link WorkOrder} id.
	 * @param woId The {@link WorkOrder} id.
	 * @return The list of {@link RequisitionForm}.
	 */
	List<RequisitionForm> getReqFormByWoId(Integer woId);

	/**
	 * Get the remaining quantity of the purchase requisition reference.
	 * @param referenceObjectId The reference object id.
	 * @param poId The purchase requisition id.
	 * @return The remaining quantity.
	 */
	double getRemainingPRQuantity(Integer referenceObjectId, Integer poId);
}