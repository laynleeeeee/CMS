package eulap.eb.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.domain.Domain;
import eulap.eb.dao.FleetInsurancePermitRenewalDao;
import eulap.eb.dao.ObjectToObjectDao;
import eulap.eb.dao.ReferenceDocumentDao;
import eulap.eb.domain.hibernate.FleetInsurancePermitRenewal;
import eulap.eb.domain.hibernate.ORType;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.ReferenceDocument;
import eulap.eb.domain.hibernate.User;
import eulap.eb.validator.ValidatorMessages;
import eulap.eb.web.dto.FleetInsurancePermitRenewalDto;

/**
 * A class that handles the business logic of Fleet Insurance Permit Renewal. 

 *
 */
@Service
public class FleetInsurancePermitRenewalService {
	private static Logger logger = Logger.getLogger(FleetInsurancePermitRenewalService.class);
	@Autowired
	private FleetInsurancePermitRenewalDao fleetInsurancePermitRenewalDao;
	@Autowired
	private FleetProfileService fleetProfileService;
	@Autowired
	private ReferenceDocumentDao documentDao;
	@Autowired
	private ObjectToObjectDao objectToObjectDao;
	@Autowired
	private ReferenceDocumentService documentService;

	/**
	 * 
	 * @param insurancePermitRenewalDto The Fleet Insurance permit renewal.
	 * @param user The user current log.
	 */
	public void saveIPR(FleetInsurancePermitRenewalDto insurancePermitRenewalDto, User user) {
		logger.info("Saving fleet insurance permit and renewals."); 
		Integer refObjectId = insurancePermitRenewalDto.getFpEbObjectId();
		List<Domain> o2os = new ArrayList<>();
		Date currentDate = new Date();
		List<Integer> ids = new ArrayList<>();
		List<FleetInsurancePermitRenewal> fleetInsurancePermitRenewals = getFleetIPRByRefObjectId(refObjectId);
		for (FleetInsurancePermitRenewal insurancePermitRenewal : insurancePermitRenewalDto.getInsurancePermitRenewals()) {
			boolean isNew = insurancePermitRenewal.getId() == 0;
			FleetInsurancePermitRenewal ipr = fleetInsurancePermitRenewalDao.get(insurancePermitRenewal.getId());
			boolean hasChnages = false;
			if(!isNew){
				hasChnages = !(ipr.getRefNo() != null ? ipr.getRefNo().equals(insurancePermitRenewal.getRefNo()) :
						(insurancePermitRenewal.getRefNo() == null)) ||
						!(ipr.getDescription() != null ? ipr.getDescription().equals(insurancePermitRenewal.getDescription()) :
							(insurancePermitRenewal.getDescription() == null)) ||
						!(ipr.getRemarks() != null ? ipr.getRemarks().equals(insurancePermitRenewal.getRemarks()) :
							(insurancePermitRenewal.getRemarks() == null)) ||
						(ipr.getExpirationDate() != null ? ipr.getExpirationDate().getTime() != insurancePermitRenewal.getExpirationDate().getTime() :
							insurancePermitRenewal.getExpirationDate() != null) ||
						(ipr.getIssuanceDate() != null ? ipr.getIssuanceDate().getTime() != insurancePermitRenewal.getIssuanceDate().getTime() :
							insurancePermitRenewal.getIssuanceDate() != null);
			}
			int ebObjectId = fleetProfileService.saveAndGetEbObject(user, FleetInsurancePermitRenewal.OBJECT_TYPE_ID, currentDate);
			insurancePermitRenewal.setActive(true);
			if (isNew || hasChnages){
				insurancePermitRenewal.setId(0);
				AuditUtil.addAudit(insurancePermitRenewal, new Audit(user.getId(), true, currentDate));
				insurancePermitRenewal.setEbObjectId(ebObjectId);
				o2os.add(ObjectToObject.getInstanceOf(refObjectId, ebObjectId, 
						ORType.PARENT_OR_TYPE_ID, user, currentDate));
			} else {
				ids.add(insurancePermitRenewal.getId());
				continue;
			}
			fleetInsurancePermitRenewalDao.saveOrUpdate(insurancePermitRenewal);
			ids.add(insurancePermitRenewal.getId());
			objectToObjectDao.saveOrUpdate(ObjectToObject.getInstanceOf(refObjectId, insurancePermitRenewal.getEbObjectId(), 
					ORType.PARENT_OR_TYPE_ID, user, currentDate));
		}
		for (FleetInsurancePermitRenewal ipr : fleetInsurancePermitRenewals) {
			if(!ids.contains(ipr.getId())){
				ipr.setActive(false);
				fleetInsurancePermitRenewalDao.update(ipr);
			}
		}

		saveReferennceDocument(insurancePermitRenewalDto.getReferenceDocuments(), refObjectId, user, currentDate);
	}

	private void saveReferennceDocument(List<ReferenceDocument> referenceDocuments, Integer refObjectId, User user, Date currentDate){
		logger.info("Saving reference documents of fleet insurance permit and renewals.");

		// Delete the previously saved reference documents.
		List<ReferenceDocument> savedRefDocs = documentDao.getRDsByEbObject(refObjectId, FleetInsurancePermitRenewal.REFERENCE_DOCUMENT_OR_TYPE_ID);
		List<Domain> toBeDeleted = new ArrayList<>();
		if(!savedRefDocs.isEmpty()) {
			toBeDeleted.addAll(savedRefDocs);
			documentDao.batchDelete(toBeDeleted);
			savedRefDocs = null;
			toBeDeleted = null;
		}

		List<Domain> documents = new ArrayList<>();
		List<Domain> o2os = new ArrayList<>();
		for (ReferenceDocument document : referenceDocuments) {
			int ebObjectId = fleetProfileService.saveAndGetEbObject(user, ReferenceDocument.OBJECT_TYPE_ID, currentDate);
			document.setEbObjectId(ebObjectId);
			documents.add(document);
			o2os.add(ObjectToObject.getInstanceOf(refObjectId, ebObjectId, 
					FleetInsurancePermitRenewal.REFERENCE_DOCUMENT_OR_TYPE_ID, user, currentDate));
		}
		documentDao.batchSave(documents);
		fleetProfileService.saveDomains(o2os);
	}

	/**
	 * Get active {@link List<FleetInsurancePermitRenewal>} by reference of object id.
	 * @param refObjectId The reference object id.
	 * @return {@link List<FleetInsurancePermitRenewal>}.
	 */
	public List<FleetInsurancePermitRenewal> getFleetIPRByRefObjectId(Integer refObjectId) {
		return fleetInsurancePermitRenewalDao.getFleetIPRByRefObjectId(refObjectId);
	}

	/**
	 * Get the reference documents by reference object id.
	 * @param refObjectId The reference object id.
	 * @return {@link List<ReferenceDocument>}.
	 */
	public List<ReferenceDocument> getReferenceDocumentByRefObjectId(Integer refObjectId) {
		return documentDao.getRDsByEbObject(refObjectId, FleetInsurancePermitRenewal.REFERENCE_DOCUMENT_OR_TYPE_ID);
	}

	/**
	 * Validate the fleet insurance permit renewals before saving.
	 * @param insurancePermitRenewalDto The dto to be evaluated.
	 * @param errors The binding result object.
	 */
	public void validate(FleetInsurancePermitRenewalDto insurancePermitRenewalDto, Errors errors) {
		List<FleetInsurancePermitRenewal> fleetInsurancePermitRenewals = insurancePermitRenewalDto.getInsurancePermitRenewals();
		int count = 1;
		String attribName = "fleetIprErrMsg"; 
		if (fleetInsurancePermitRenewals != null && !fleetInsurancePermitRenewals.isEmpty()) {
			for (FleetInsurancePermitRenewal ipr : fleetInsurancePermitRenewals) {

				// Reference No.
				if (ipr.getRefNo() == null) {
					errors.rejectValue(attribName, null, null, ValidatorMessages.getString("FleetInsurancePermitRenewalService.0")+count+ValidatorMessages.getString("FleetInsurancePermitRenewalService.1"));
				} else if(ipr.getRefNo().length() > FleetInsurancePermitRenewal.MAX_REF_NO){ 
					errors.rejectValue(attribName, null, null, ValidatorMessages.getString("FleetInsurancePermitRenewalService.2")+
							FleetInsurancePermitRenewal.MAX_REF_NO +ValidatorMessages.getString("FleetInsurancePermitRenewalService.3")+count+ValidatorMessages.getString("FleetInsurancePermitRenewalService.4"));
				}

				// Description
				if (ipr.getDescription() == null) {
					errors.rejectValue(attribName, null, null, ValidatorMessages.getString("FleetInsurancePermitRenewalService.5")+count+ValidatorMessages.getString("FleetInsurancePermitRenewalService.6"));
				} else if(ipr.getDescription().length() > FleetInsurancePermitRenewal.MAX_DESCRIPTION) {
					errors.rejectValue(attribName, null, null, ValidatorMessages.getString("FleetInsurancePermitRenewalService.7")+
							FleetInsurancePermitRenewal.MAX_DESCRIPTION +ValidatorMessages.getString("FleetInsurancePermitRenewalService.8")+count+ValidatorMessages.getString("FleetInsurancePermitRenewalService.9"));
				}

				// Issuance Date
				if(ipr.getIssuanceDate() == null) {
					errors.rejectValue(attribName, null, null, ValidatorMessages.getString("FleetInsurancePermitRenewalService.10")+count+ValidatorMessages.getString("FleetInsurancePermitRenewalService.11"));
				}

				if(ipr.getExpirationDate() == null) {
					errors.rejectValue(attribName, null, null, ValidatorMessages.getString("FleetInsurancePermitRenewalService.12")+count+ValidatorMessages.getString("FleetInsurancePermitRenewalService.13"));
				}
				count++;
			}
		} else {
			errors.rejectValue(attribName, null, null, ValidatorMessages.getString("FleetInsurancePermitRenewalService.14"));
		}
		documentService.validateReferences(insurancePermitRenewalDto.getReferenceDocuments(), errors);
	}
}