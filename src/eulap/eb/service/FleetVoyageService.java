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
import eulap.eb.dao.FleetVoyageDao;
import eulap.eb.dao.ReferenceDocumentDao;
import eulap.eb.domain.hibernate.FleetVoyage;
import eulap.eb.domain.hibernate.ORType;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.ReferenceDocument;
import eulap.eb.domain.hibernate.User;
import eulap.eb.validator.ValidatorMessages;
import eulap.eb.web.dto.FleetVoyageDto;

/**
 * Handles business logic of {@link FleetVoyage}

 *
 */
@Service
public class FleetVoyageService {

	@Autowired
	private FleetVoyageDao fleetVoyagesDao;
	@Autowired
	private ReferenceDocumentDao referenceDocumentDao;
	@Autowired 
	private ReferenceDocumentService referenceDocumentService;
	@Autowired
	private FleetProfileService fleetProfileService;

	/**
	 * Get the {@link FleetVoyageDto}
	 * @param refObjectId the refObjectId.
	 * @return The {@link FleetVoyageDto}
	 */
	public FleetVoyageDto getFleetVoyageDto(Integer refObjectId) {
		FleetVoyageDto fleetVoyagesDto = new FleetVoyageDto();
		fleetVoyagesDto.setFleetVoyages(getFleetVoyagesByRefObjectId(refObjectId));
		fleetVoyagesDto.setReferenceDocuments(referenceDocumentDao.getRDsByEbObject(refObjectId, FleetVoyage.OR_TYPE_REF_DOC_ID));
		return fleetVoyagesDto;
	}

	/**
	 * Get the list of {@link FleetVoyage}
	 * @param refObjectId The refObjectId.
	 * @return The list of {@link FleetVoyage}.
	 */
	public List<FleetVoyage> getFleetVoyagesByRefObjectId(Integer refObjectId){
		List<FleetVoyage> fleetVoyages = fleetVoyagesDao.getFleetVoyagesByRefObjectId(refObjectId);
		for(FleetVoyage voyage : fleetVoyages) {
			if(voyage.getDateOfDeparture() != null) {
				voyage.setStrDateOfDeparture(DateUtil.formatDate(voyage.getDateOfDeparture()));
			}
			if(voyage.getDateOfUnloading() != null) {
				voyage.setStrDateOfUnloading(DateUtil.formatDate(voyage.getDateOfUnloading()));
			}
		}
		return fleetVoyages;
	}

	/**
	 * Save the Fleet Voyages
	 * @param fleetVoyagesDto The DTO class that holds the lis of {@link FleetVoyage}
	 * @param user The current logged user.
	 */
	public void saveFleetVoyage(FleetVoyageDto fleetVoyagesDto, User user) {
		if(fleetVoyagesDto.getFleetVoyages() != null && !fleetVoyagesDto.getFleetVoyages().isEmpty()) {
			Date currentDate = new Date();
			List<Domain> voyages = new ArrayList<>();
			List<Domain> o2os = new ArrayList<>();

			fleetVoyagesDto.setFleetVoyages(trimFleetVoyages(fleetVoyagesDto.getFleetVoyages()));
			for(FleetVoyage voyage : fleetVoyagesDto.getFleetVoyages()) {
				boolean isNew = voyage.getId() == 0;
				if(hasVoyage(voyage)) {
					AuditUtil.addAudit(voyage, new Audit(user.getId(), isNew, currentDate));
					voyage.setActive(true);
					if(isNew) {
						voyage.setEbObjectId(fleetProfileService.saveAndGetEbObject(user, FleetVoyage.OBJECT_TYPE_ID, currentDate));
						o2os.add(ObjectToObject.getInstanceOf(fleetVoyagesDto.getFpEbObjectId(), voyage.getEbObjectId(),
								ORType.PARENT_OR_TYPE_ID, user, currentDate));
					} else {
						FleetVoyage savedFleetVoyage = fleetVoyagesDao.get(voyage.getId());
						voyage.setCreatedBy(savedFleetVoyage.getCreatedBy());
						DateUtil.setCreatedDate(voyage, savedFleetVoyage.getCreatedDate());
						voyage.setUpdatedBy(user.getId());
						voyage.setUpdatedDate(currentDate);
					}
					voyages.add(voyage);
				}
			}
			fleetVoyagesDao.batchSaveOrUpdate(voyages);
			fleetProfileService.saveDomains(o2os);

			// Deleting the old reference documents.
			List<Domain> toBeDeleted = new ArrayList<>();
			List<ReferenceDocument> savedRefDocs = referenceDocumentDao.getRDsByEbObject(fleetVoyagesDto.getFpEbObjectId(), FleetVoyage.OR_TYPE_REF_DOC_ID);
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
			for(ReferenceDocument referenceDocument : fleetVoyagesDto.getReferenceDocuments()) {
				ebObjectId = fleetProfileService.saveAndGetEbObject(user,
						ReferenceDocument.OBJECT_TYPE_ID, currentDate);
				referenceDocument.setEbObjectId(ebObjectId);
				refDocs.add(referenceDocument);
				o2os.add(ObjectToObject.getInstanceOf(fleetVoyagesDto.getFpEbObjectId(), ebObjectId,
						FleetVoyage.OR_TYPE_REF_DOC_ID, user, currentDate));
			}
			referenceDocumentDao.batchSave(refDocs);
			fleetProfileService.saveDomains(o2os);
		}
	}

	/**
	 * Handles validation for {@link FleetVoyage}
	 * @param fDto The DTO class that holds the list of {@link FleetVoyage}
	 * @param errors The errors detected.
	 */
	public void validate(FleetVoyageDto fDto, Errors errors) {
		boolean hasValue = false;
		if (fDto.getFleetVoyages() != null && !fDto.getFleetVoyages().isEmpty()) {
			int rowCount = 1;
			String attribName = "fleetVoyagesErrMsg";
			fDto.setFleetVoyages(trimFleetVoyages(fDto.getFleetVoyages()));
			for(FleetVoyage voyage : fDto.getFleetVoyages()) {
				if(hasVoyage(voyage)) {
					hasValue = true;
					if(voyage.getCatcher() != null && voyage.getCatcher().length() > FleetVoyage.MAX_CHAR) {
						errors.rejectValue(attribName, null, null, ValidatorMessages.getString("FleetVoyageService.0") + FleetVoyage.MAX_CHAR+
								ValidatorMessages.getString("FleetVoyageService.1")+rowCount+ValidatorMessages.getString("FleetVoyageService.2"));
					}
					if(voyage.getVolume() != null && voyage.getVolume().length() > FleetVoyage.MAX_CHAR) {
						errors.rejectValue(attribName, null, null, ValidatorMessages.getString("FleetVoyageService.3") + FleetVoyage.MAX_CHAR+
								ValidatorMessages.getString("FleetVoyageService.4")+rowCount+ValidatorMessages.getString("FleetVoyageService.5"));
					}
				}
				rowCount++;
			}
		}
		if(!hasValue) {
			errors.rejectValue("fleetVoyagesErrMsg", null, null, ValidatorMessages.getString("FleetVoyageService.6"));
		}
		// Reference Documents.
		referenceDocumentService.validateReferences(fDto.getReferenceDocuments(), errors);
	}

	// Trim fleet voyages
	private List<FleetVoyage> trimFleetVoyages(List<FleetVoyage> fleetVoyages){
		if(fleetVoyages != null && !fleetVoyages.isEmpty()) {
			for(FleetVoyage voyage : fleetVoyages) {
				if(voyage.getCatcher() != null) {
					voyage.setCatcher(voyage.getCatcher().trim());
				}
				if(voyage.getVolume() != null) {
					voyage.setVolume(voyage.getVolume().trim());
				}
			}
		}
		return fleetVoyages;
	}

	private boolean hasVoyage(FleetVoyage fleetVoyage) {
		if(fleetVoyage.getDateOfDeparture() != null || fleetVoyage.getDateOfUnloading() != null ||
				(fleetVoyage.getCatcher() != null && !fleetVoyage.getCatcher().equals("")) ||
				(fleetVoyage.getVolume() != null && !fleetVoyage.getVolume().equals(""))) {
			return true;
		}
		return false;
	}
}
