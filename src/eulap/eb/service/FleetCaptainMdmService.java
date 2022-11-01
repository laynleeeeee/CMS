package eulap.eb.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.domain.Domain;
import eulap.eb.dao.FleetCaptainMdmDao;
import eulap.eb.dao.ReferenceDocumentDao;
import eulap.eb.domain.hibernate.FleetCaptainMdm;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.ReferenceDocument;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.FleetCaptainMdmDto;

/**
 * Handles business logic of {@link FleetCaptainMdm}

 *
 */
@Service
public class FleetCaptainMdmService {

	@Autowired
	private FleetProfileService fleetProfileService;
	@Autowired
	private FleetCaptainMdmDao fleetCaptainMdmDao;
	@Autowired
	private ReferenceDocumentDao referenceDocumentDao;
	@Autowired
	private ReferenceDocumentService referenceDocumentService;

	/**
	 * Get the FleetCaptainMdmDto by refObjectId.
	 * @param refObjectId The refObjectId.
	 * @param isActiveOnly True if active only, otherwise false.
	 * @return The FleetCaptainMdmDto.
	 */
	public FleetCaptainMdmDto getFleetCaptainMdmDto(Integer refObjectId, Boolean isActiveOnly) {
		FleetCaptainMdmDto fleetCaptainMdmDto = new FleetCaptainMdmDto();
		fleetCaptainMdmDto.setFleetCaptainMdms(fleetCaptainMdmDao.getFleetCaptainMdms(refObjectId, isActiveOnly));
		fleetCaptainMdmDto.setReferenceDocuments(referenceDocumentDao.getRDsByEbObject(refObjectId,
				FleetCaptainMdm.OR_TYPE_REF_DOC));
		return fleetCaptainMdmDto;
	}

	/**
	 * Save the list of {@link FleetCaptainMdm}
	 * @param fleetCaptainMdmDto The {@link FleetCaptainMdmDto} that contains the list of the {@link FleetCaptainMdm}
	 * @param user The current logged user.
	 */
	public void saveFleetCaptainMdm(FleetCaptainMdmDto fleetCaptainMdmDto, User user) {
		Integer refObjectId = fleetCaptainMdmDto.getFpEbObjectId();
		Date currentDate = new Date();
		List<Domain> refDocs = null;

		// Set previously saved Fleet Captains/MDM to inactive.
		List<FleetCaptainMdm> savedFleetCaptains = fleetCaptainMdmDao.getFleetCaptainMdms(refObjectId, true);
		inactivePrevFleetCaptainMdm(savedFleetCaptains, user, currentDate);

		// Save the Captains
		List<FleetCaptainMdm> fleetCaptains = fleetCaptainMdmDto.getFleetCaptainMdms();
		saveFleetCaptainMdms(refObjectId, fleetCaptains, FleetCaptainMdm.CAPTAIN_OR_TYPE_ID, user, currentDate);

		// Deleting the old reference documents.
		List<Domain> toBeDeleted = new ArrayList<>();
		List<ReferenceDocument> savedRefDocs = referenceDocumentDao.getRDsByEbObject(refObjectId, FleetCaptainMdm.OR_TYPE_REF_DOC);
		if (!savedRefDocs.isEmpty()) {
			toBeDeleted.addAll(savedRefDocs);
			savedRefDocs = null;
			referenceDocumentDao.batchDelete(toBeDeleted);
			toBeDeleted = null;
		}

		// Saving the new Reference Documents
		refDocs = new ArrayList<>();
		List<Domain> o2os = new ArrayList<>();
		Integer ebObjectId = null;
		for(ReferenceDocument referenceDocument : fleetCaptainMdmDto.getReferenceDocuments()) {
			ebObjectId = fleetProfileService.saveAndGetEbObject(user,
					ReferenceDocument.OBJECT_TYPE_ID, currentDate);
			referenceDocument.setEbObjectId(ebObjectId);
			refDocs.add(referenceDocument);
			o2os.add(ObjectToObject.getInstanceOf(refObjectId, ebObjectId,
					FleetCaptainMdm.OR_TYPE_REF_DOC, user, currentDate));
		}
		referenceDocumentDao.batchSave(refDocs);
		fleetProfileService.saveDomains(o2os);
	}

	private void inactivePrevFleetCaptainMdm(List<FleetCaptainMdm> savedFleetCaptainMdms, User user, Date currentDate) {
		List<Domain> fleetCaptainMdms = new ArrayList<>();
		if(!savedFleetCaptainMdms.isEmpty()) {
			for(FleetCaptainMdm fleetCaptain : savedFleetCaptainMdms) {
				fleetCaptain.setUpdatedBy(user.getId());
				AuditUtil.addAudit(fleetCaptain, new Audit(user.getId(), false, currentDate));
				fleetCaptain.setActive(false);
				fleetCaptainMdms.add(fleetCaptain);
			}
			fleetCaptainMdmDao.batchSaveOrUpdate(fleetCaptainMdms);
		}
	}

	private void saveFleetCaptainMdms(Integer refObjectId, List<FleetCaptainMdm> fleetCaptainMdms,
			Integer orTypeId, User user, Date currentDate) {
		List<Domain> fleetCM = new ArrayList<>();
		List<Domain> o2os = new ArrayList<>();
		Integer ebObjectId = null;
		if(!fleetCaptainMdms.isEmpty()) {
			for(FleetCaptainMdm fleetCaptainMdm : fleetCaptainMdms) {
				AuditUtil.addAudit(fleetCaptainMdm, new Audit(user.getId(), true, currentDate));
				ebObjectId = fleetProfileService.saveAndGetEbObject(user,
						FleetCaptainMdm.OBJECT_TYPE_ID, currentDate);
				fleetCaptainMdm.setEbObjectId(ebObjectId);
				fleetCaptainMdm.setActive(true);
				fleetCM.add(fleetCaptainMdm);
				o2os.add(ObjectToObject.getInstanceOf(refObjectId, ebObjectId, fleetCaptainMdm.getOrTypeId(), user, currentDate));
			}
			fleetCaptainMdmDao.batchSaveOrUpdate(fleetCM);
			fleetProfileService.saveDomains(o2os);
		}
	}

	/**
	 * Validate {@link FleetCaptainMdm}
	 * @param fleetCaptainMdmDto The {@link FleetCaptainMdmDto}
	 * @param errors The errors detected.
	 */
	public void validateFleetCaptainMdms(FleetCaptainMdmDto fleetCaptainMdmDto, Errors errors) {

		// Validate Captains
		validateCM(fleetCaptainMdmDto.getFleetCaptainMdms(), errors, "fleetCaptainErrMsg");

		// Validate reference Documents
		referenceDocumentService.validateReferences(fleetCaptainMdmDto.getReferenceDocuments(), errors);

	}

	private void validateCM(List<FleetCaptainMdm> fleetCaptainMdms, Errors errors, String attribName) {
		if(!fleetCaptainMdms.isEmpty()) {
			int rowCount = 1;
			boolean hasDupLicenseNo = false;
			boolean hasDupSeamansBook = false;
			boolean hasDupFisheries = false;
			boolean hasDupPassport = false;
			Map<String, FleetCaptainMdm> mapLicense = new HashMap<String, FleetCaptainMdm>();
			Map<String, FleetCaptainMdm> mapSeaman = new HashMap<String, FleetCaptainMdm>();
			Map<String, FleetCaptainMdm> mapFisheries = new HashMap<String, FleetCaptainMdm>();
			Map<String, FleetCaptainMdm> mapPassport = new HashMap<String, FleetCaptainMdm>();
			for(FleetCaptainMdm fleetCaptainMdm : fleetCaptainMdms) {

				// Date
				if(fleetCaptainMdm.getDate() == null) {
					errors.rejectValue(attribName, null, null, "Date is required at row("+rowCount+").");
				}

				// Name
				if(fleetCaptainMdm.getName() == null) {
					errors.rejectValue(attribName, null, null, "Name is required at row("+rowCount+").");
				} else if(fleetCaptainMdm.getName().trim().length() > FleetCaptainMdm.MAX_CHAR){
					errors.rejectValue(attribName, null, null,
							"Name must not exceed " + FleetCaptainMdm.MAX_CHAR + " characters at row("+rowCount+")." );
				}

				// Position Type
				if(fleetCaptainMdm.getOrTypeId() == null) {
					errors.rejectValue(attribName, null, null, "Type is required at row("+rowCount+").");
				}

				// Position
				if(fleetCaptainMdm.getPosition() == null) { 
					errors.rejectValue(attribName, null, null, "Position is required at row("+rowCount+").");
				} else if(fleetCaptainMdm.getPosition().trim().length() > FleetCaptainMdm.MAX_CHAR) {
					errors.rejectValue(attribName, null, null,
							"Position must not exceed " + FleetCaptainMdm.MAX_CHAR + " characters at row("+rowCount+")." );
				}

				// License No
				if((fleetCaptainMdm.getLicenseNo() != null && fleetCaptainMdm.getLicenseNoExpirationDate() == null)
						|| fleetCaptainMdm.getLicenseNo() == null && fleetCaptainMdm.getLicenseNoExpirationDate() != null) {
					errors.rejectValue(attribName, null, null, "License No and Expiration date must be filled up at row("+rowCount+").");
				}
				if(fleetCaptainMdm.getLicenseNo() != null &&
						fleetCaptainMdm.getLicenseNo().trim().length() > FleetCaptainMdm.MAX_CHAR) {
					errors.rejectValue(attribName, null, null,
							"License No. must not exceed " + FleetCaptainMdm.MAX_CHAR + " characters at row("+rowCount+")." );
				}
				if(mapLicense.containsKey(fleetCaptainMdm.getLicenseNo()) 
						&& fleetCaptainMdm.getLicenseNo() != null && !hasDupLicenseNo) {
					hasDupLicenseNo = true;
				} else {
					mapLicense.put(fleetCaptainMdm.getLicenseNo(), fleetCaptainMdm);
				}

				// Seaman's Book
				if((fleetCaptainMdm.getSeamansBook() != null && fleetCaptainMdm.getSeamansBookExpirationDate() == null)
						|| fleetCaptainMdm.getSeamansBook() == null && fleetCaptainMdm.getSeamansBookExpirationDate() != null) {
					errors.rejectValue(attribName, null, null, "Seaman's Book and Expiration date must be filled up at row("+rowCount+").");
				}
				if(fleetCaptainMdm.getSeamansBook() != null &&
						fleetCaptainMdm.getSeamansBook().trim().length() > FleetCaptainMdm.MAX_CHAR) {
					errors.rejectValue(attribName, null, null,
							"Seaman's Book must not exceed " + FleetCaptainMdm.MAX_CHAR + " characters at row("+rowCount+")." );
				}
				if(mapSeaman.containsKey(fleetCaptainMdm.getSeamansBook())
						&& fleetCaptainMdm.getSeamansBook() != null && !hasDupSeamansBook) {
					hasDupSeamansBook = true;
				} else {
					mapSeaman.put(fleetCaptainMdm.getSeamansBook(), fleetCaptainMdm);
				}

				// Fisheries
				if((fleetCaptainMdm.getFisheries() != null && fleetCaptainMdm.getFishesriesExpirationDate() == null)
						|| fleetCaptainMdm.getFisheries() == null && fleetCaptainMdm.getFishesriesExpirationDate() != null) {
					errors.rejectValue(attribName, null, null, "Fisheries and Expiration date must be filled up at row("+rowCount+").");
				}
				if(fleetCaptainMdm.getFisheries() != null &&
						fleetCaptainMdm.getFisheries().trim().length() > FleetCaptainMdm.MAX_CHAR) {
					errors.rejectValue(attribName, null, null,
							"Fisheries must not exceed " + FleetCaptainMdm.MAX_CHAR + " characters at row("+rowCount+")." );
				}
				if(mapFisheries.containsKey(fleetCaptainMdm.getFisheries())
						&& fleetCaptainMdm.getFisheries() != null && !hasDupFisheries) {
					hasDupFisheries = true;
				} else {
					mapFisheries.put(fleetCaptainMdm.getFisheries(), fleetCaptainMdm);
				}

				// Passport
				if((fleetCaptainMdm.getPassport() != null && fleetCaptainMdm.getPassportExpirationDate() == null)
						|| fleetCaptainMdm.getPassport() == null && fleetCaptainMdm.getPassportExpirationDate() != null) {
					errors.rejectValue(attribName, null, null, "Passport and Expiration date must be filled up at row("+rowCount+").");
				}
				if(fleetCaptainMdm.getPassport() != null &&
						fleetCaptainMdm.getPassport().trim().length() > FleetCaptainMdm.MAX_CHAR) {
					errors.rejectValue(attribName, null, null,
							"Passport must not exceed " + FleetCaptainMdm.MAX_CHAR + " characters at row("+rowCount+")." );
				}
				if(mapPassport.containsKey(fleetCaptainMdm.getPassport())
						&& fleetCaptainMdm.getPassport() != null && !hasDupPassport) {
					hasDupPassport = true;
				} else {
					mapPassport.put(fleetCaptainMdm.getPassport(), fleetCaptainMdm);
				}

				rowCount++;
			}
			if(hasDupLicenseNo) {
				errors.rejectValue(attribName, null, null, "License No must be unique.");
			}
			if(hasDupSeamansBook) {
				errors.rejectValue(attribName, null, null, "Seaman's Book must be unique.");
			}
			if(hasDupFisheries) {
				errors.rejectValue(attribName, null, null, "Fisheries must be unique.");
			}
			if(hasDupPassport) {
				errors.rejectValue(attribName, null, null, "Passport must be unique.");
			}
		}
	}
}
