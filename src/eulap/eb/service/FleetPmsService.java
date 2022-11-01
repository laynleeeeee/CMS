package eulap.eb.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.domain.Domain;
import eulap.common.util.DateUtil;
import eulap.eb.dao.FleetPmsDao;
import eulap.eb.dao.ReferenceDocumentDao;
import eulap.eb.domain.hibernate.FleetPms;
import eulap.eb.domain.hibernate.ORType;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.ReferenceDocument;
import eulap.eb.domain.hibernate.User;
import eulap.eb.validator.ValidatorMessages;
import eulap.eb.web.dto.FleetPmsDto;

/**
 * Handles business logic for {@link FleetPms}

 *
 */

@Service
public class FleetPmsService {

	@Autowired
	private FleetPmsDao fleetPmsDao;
	@Autowired
	private FleetProfileService fleetProfileService;
	@Autowired
	private ReferenceDocumentDao referenceDocumentDao;
	@Autowired
	private ReferenceDocumentService referenceDocumentService;

	/**
	 * Get the {@link FleetPmsDto} Object.
	 * @param refObjectId The refObjectId.
	 * @return The {@link FleetPmsDto}.
	 */
	public FleetPmsDto getFleetPmsDto(Integer refObjectId) {
		FleetPmsDto fleetPmsDto = new FleetPmsDto();
		fleetPmsDto.setFleetPms(getFleetPms(refObjectId));
		fleetPmsDto.setReferenceDocuments(referenceDocumentDao.getRDsByEbObject(refObjectId, FleetPms.OR_TYPE_REF_DOC_ID));
		return fleetPmsDto;
	}

	/**
	 * Save the List of FleetPms
	 * @param fleetPmsDto The DTO that holds the list of FleetPms
	 * @param user The current logged user.
	 */
	public void saveFleetPms(FleetPmsDto fleetPmsDto, User user) {
		if(fleetPmsDto.getFleetPms() != null && !fleetPmsDto.getFleetPms().isEmpty()) {
			Date currentDate = new Date();
			List<Domain> pmsList = new ArrayList<>();
			List<Domain> o2os = new ArrayList<>();

			for(FleetPms fleetPms : fleetPmsDto.getFleetPms()) {
				if(fleetPmsHasValue(fleetPms)) {
					boolean isNew = fleetPms.getId() == 0;
					fleetPms.setActive(true);
					fleetPms.setEbObjectId(fleetProfileService.saveAndGetEbObject(user,
							FleetPms.OBJECT_TYPE_ID, currentDate));
					AuditUtil.addAudit(fleetPms, new Audit(user.getId(), isNew, currentDate));
					if(!isNew) {
						FleetPms savedPms = fleetPmsDao.get(fleetPms.getId());
						fleetPms.setCreatedBy(savedPms.getCreatedBy());
						DateUtil.setCreatedDate(fleetPms, savedPms.getCreatedDate());
						fleetPms.setUpdatedBy(user.getId());
						fleetPms.setUpdatedDate(currentDate);
					}
					pmsList.add(fleetPms);
					o2os.add(ObjectToObject.getInstanceOf(fleetPmsDto.getFpEbObjectId(), fleetPms.getEbObjectId(),
							ORType.PARENT_OR_TYPE_ID, user, currentDate));
				}
			}
			fleetPmsDao.batchSaveOrUpdate(pmsList);
			fleetProfileService.saveDomains(o2os);

			// Deleting the old reference documents.
			List<Domain> toBeDeleted = new ArrayList<>();
			List<ReferenceDocument> savedRefDocs = referenceDocumentDao.getRDsByEbObject(fleetPmsDto.getFpEbObjectId(), FleetPms.OR_TYPE_REF_DOC_ID);
			if (!savedRefDocs.isEmpty()) {
				toBeDeleted.addAll(savedRefDocs);
				savedRefDocs = null;
				referenceDocumentDao.batchDelete(toBeDeleted);
				toBeDeleted = null;
			}

			// Save new reference Documents.
			Integer ebObjectId = null;
			List<Domain> refDocs = new ArrayList<>();
			o2os = new ArrayList<>();
			for(ReferenceDocument referenceDocument : fleetPmsDto.getReferenceDocuments()) {
				ebObjectId = fleetProfileService.saveAndGetEbObject(user,
						ReferenceDocument.OBJECT_TYPE_ID, currentDate);
				referenceDocument.setEbObjectId(ebObjectId);
				refDocs.add(referenceDocument);
				o2os.add(ObjectToObject.getInstanceOf(fleetPmsDto.getFpEbObjectId(), ebObjectId,
						FleetPms.OR_TYPE_REF_DOC_ID, user, currentDate));
			}
			referenceDocumentDao.batchSave(refDocs);
			fleetProfileService.saveDomains(o2os);
		}
	}

	/**
	 * Handles validation for {@link FleetPms}
	 * @param fleetPmsDto The {@link FleetPmsDto} that contains the list of Objects to be validated.
	 * @param errors The errors detected
	 */
	public void validateFleetPms(FleetPmsDto fleetPmsDto, Errors errors) {

		String attribName = "fleetPmsErrMsg";
		boolean hasValue = false;
		List<FleetPms> fleetPms = fleetPmsDto.getFleetPms();
		if(fleetPms != null && !fleetPms.isEmpty()) {
			int rowCount = 1;
			for(FleetPms fPms : fleetPms) {
				if(fleetPmsHasValue(fPms)) {
					hasValue = true;
					// Change Oil Engine
					if(fPms.getChangeOilEngine() != null
							&& fPms.getChangeOilEngine().trim().length() > FleetPms.MAX_CHAR) {
						validateMaxChar(errors, attribName, ValidatorMessages.getString("FleetPmsService.0"), rowCount);
					}

					// Main
					if(fPms.getOverhaulEngineMain() != null
							&& fPms.getOverhaulEngineMain().trim().length() > FleetPms.MAX_CHAR) {
						validateMaxChar(errors, attribName, ValidatorMessages.getString("FleetPmsService.14"), rowCount);
					}

					// Auxiliary
					if(fPms.getOverhaulEngineAuxiliary() != null
							&& fPms.getOverhaulEngineAuxiliary().trim().length() > FleetPms.MAX_CHAR) {
						validateMaxChar(errors, attribName, ValidatorMessages.getString("FleetPmsService.15"), rowCount);
					}

					// Change Oil Transmission
					if(fPms.getChangeOilTransmission() != null
							&& fPms.getChangeOilTransmission().trim().length() > FleetPms.MAX_CHAR) {
						validateMaxChar(errors, attribName, ValidatorMessages.getString(
								fleetPmsDto.getFleetTypeId() == 1 ? "FleetPmsService.1" : "FleetPmsService.16"), rowCount);
					}

					// Filter Fuel
					if(fPms.getFilterFuel() != null 
							&& fPms.getFilterFuel().trim().length() > FleetPms.MAX_CHAR) {
						validateMaxChar(errors, attribName, ValidatorMessages.getString("FleetPmsService.4"), rowCount);
					}

					// Filter Oil
					if(fPms.getFilterOil() != null
							&& fPms.getFilterOil().trim().length() > FleetPms.MAX_CHAR) {
						validateMaxChar(errors, attribName, ValidatorMessages.getString("FleetPmsService.5"), rowCount);
					}

					// Filter Air
					if(fPms.getFilterAir() != null
							&& fPms.getFilterAir().trim().length() > FleetPms.MAX_CHAR) {
						validateMaxChar(errors, attribName, ValidatorMessages.getString("FleetPmsService.6"), rowCount);
					}

					// Filter Fanbelt
					if(fPms.getFilterFanbelt() != null
							&& fPms.getFilterFanbelt().trim().length() > FleetPms.MAX_CHAR) {
						validateMaxChar(errors, attribName, ValidatorMessages.getString("FleetPmsService.7"), rowCount);
					}

					// Net
					if(fPms.getNet() != null
							&& fPms.getNet().trim().length() > FleetPms.MAX_CHAR) {
						validateMaxChar(errors, attribName, ValidatorMessages.getString("FleetPmsService.9"), rowCount);
					}
				}
				rowCount++;
			}
		}

		if(!hasValue) {
			errors.rejectValue(attribName, null, null, ValidatorMessages.getString("FleetPmsService.10"));
		}

		// Reference Documents.
		referenceDocumentService.validateReferences(fleetPmsDto.getReferenceDocuments(), errors);
	}

	private void validateMaxChar(Errors errors, String attribName, String fieldName, int rowCount) {
		errors.rejectValue(attribName, null, null, 
				fieldName + ValidatorMessages.getString("FleetPmsService.11") + FleetPms.MAX_CHAR +ValidatorMessages.getString("FleetPmsService.12")+rowCount+ValidatorMessages.getString("FleetPmsService.13"));
	}

	// True if at least 1 field of FleetPms is not empty, otherwise false.
	private boolean fleetPmsHasValue(FleetPms fleetPms) {
		if(fleetPms.getChangeOilEngine() != null || fleetPms.getChangeOilTransmission() != null
				|| fleetPms.getFilterFuel() != null || fleetPms.getFilterOil() != null
				|| fleetPms.getFilterAir() != null || fleetPms.getFilterFanbelt() != null
				|| fleetPms.getNet() != null || fleetPms.getOverhaulEngineMain() != null
				|| fleetPms.getOverhaulEngineAuxiliary() != null) {
			return true;
		}
		return false;
	}

	public List<FleetPms> getFleetPms(Integer refObjectId){
		return fleetPmsDao.getFleetPmsByRefObjectId(refObjectId);
	}
}
