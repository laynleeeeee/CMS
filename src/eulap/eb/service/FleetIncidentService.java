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
import eulap.eb.dao.FleetIncidentDao;
import eulap.eb.dao.ReferenceDocumentDao;
import eulap.eb.domain.hibernate.FleetIncident;
import eulap.eb.domain.hibernate.ORType;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.ReferenceDocument;
import eulap.eb.domain.hibernate.User;
import eulap.eb.validator.ValidatorMessages;
import eulap.eb.web.dto.FleetIncidentDto;

/**
 * Handles business logic of {@link FleetIncident}

 *
 */
@Service
public class FleetIncidentService {

	@Autowired
	private FleetProfileService fleetProfileService;
	@Autowired
	private FleetIncidentDao fleetIncidentDao;
	@Autowired
	private ReferenceDocumentDao referenceDocumentDao;
	@Autowired
	private ReferenceDocumentService referenceDocumentService;

	/**
	 * Get the FleetIncidentDto.
	 * @param refObjectId The reference Object Id.
	 * @param isActiveOnly True if active only, otherwise false.
	 * @return The FleetIncidentDto;
	 */
	public FleetIncidentDto getFleetIncidentDto(Integer refObjectId, Boolean isActiveOnly) {
		FleetIncidentDto fleetIncidentDto = new FleetIncidentDto();
		fleetIncidentDto.setFleetIncidents(fleetIncidentDao.getFleetIncidentsByRefObject(refObjectId, isActiveOnly));
		fleetIncidentDto.setReferenceDocuments(referenceDocumentDao.getRDsByEbObject(refObjectId, FleetIncident.OR_TYPE_REF_DOC));
		return fleetIncidentDto;
	}

	/**
	 * Save the fleet incident
	 * @param fleetIncidentDto The Fleet Incident Dto that contains the list of Fleet Incidents to be saved.
	 * @param user The current logged user.
	 */
	public void saveFleetIncident(FleetIncidentDto fleetIncidentDto, User user) {
		Integer refObjectId = fleetIncidentDto.getFpEbObjectId();
		Date currentDate = new Date();
		List<Domain> fleetIncidents = null;
		List<Domain> refDocs = null;
		List<Domain> o2os = null;
		Integer ebObjectId = null;

		// Inactive the previously saved Fleet Incidents.
		List<FleetIncident> savedFIs = fleetIncidentDao.getFleetIncidentsByRefObject(refObjectId, true);
		fleetIncidents = new ArrayList<>();
		if(!savedFIs.isEmpty()) {
			for(FleetIncident fi : savedFIs) {
				fi.setUpdatedBy(user.getId());
				AuditUtil.addAudit(fi, new Audit(user.getId(), false, currentDate));
				fi.setActive(false);
				fleetIncidents.add(fi);
				fi = null;
			}
			savedFIs = null;
			fleetIncidentDao.batchSaveOrUpdate(fleetIncidents);
		}

		// Saving the new Fleet Incidents
		fleetIncidents = new ArrayList<>();
		o2os = new ArrayList<>();
		for(FleetIncident fleetIncident : fleetIncidentDto.getFleetIncidents()) {
			AuditUtil.addAudit(fleetIncident, new Audit(user.getId(), true, currentDate));
			ebObjectId = fleetProfileService.saveAndGetEbObject(user,
					FleetIncident.OBJECT_TYPE_ID, currentDate);
			fleetIncident.setEbObjectId(ebObjectId);
			fleetIncident.setActive(true);
			fleetIncident.setEmployeeName(fleetIncident.getEmployeeName().trim());
			fleetIncident.setLocation(fleetIncident.getLocation().trim());
			fleetIncident.setCause(fleetIncident.getCause().trim());
			if(fleetIncident.getInsuranceClaims() != null) {
				fleetIncident.setInsuranceClaims(fleetIncident.getInsuranceClaims().trim());
			}
			fleetIncidents.add(fleetIncident);
			o2os.add(ObjectToObject.getInstanceOf(refObjectId, ebObjectId,
					ORType.PARENT_OR_TYPE_ID, user, currentDate));
		}

		fleetIncidentDao.batchSave(fleetIncidents);
		fleetProfileService.saveDomains(o2os);

		// Deleting the old reference documents.
		List<Domain> toBeDeleted = new ArrayList<>();
		List<ReferenceDocument> savedRefDocs = referenceDocumentDao.getRDsByEbObject(refObjectId, FleetIncident.OR_TYPE_REF_DOC);
		if (!savedRefDocs.isEmpty()) {
			toBeDeleted.addAll(savedRefDocs);
			savedRefDocs = null;
			referenceDocumentDao.batchDelete(toBeDeleted);
			toBeDeleted = null;
		}

		// Saving the new Reference Documents
		refDocs = new ArrayList<>();
		o2os = new ArrayList<>();
		for(ReferenceDocument referenceDocument : fleetIncidentDto.getReferenceDocuments()) {
			ebObjectId = fleetProfileService.saveAndGetEbObject(user,
					ReferenceDocument.OBJECT_TYPE_ID, currentDate);
			referenceDocument.setEbObjectId(ebObjectId);
			refDocs.add(referenceDocument);
			o2os.add(ObjectToObject.getInstanceOf(refObjectId, ebObjectId,
					FleetIncident.OR_TYPE_REF_DOC, user, currentDate));
		}
		referenceDocumentDao.batchSave(refDocs);
		fleetProfileService.saveDomains(o2os);
	}

	/**
	 * Validates Fleet Incident
	 * @param fleetIncidentDto The {@link FleetIncidentDto} to be validated.
	 * @param errors The Binding result.
	 */
	public void validateFleeIncident(FleetIncidentDto fleetIncidentDto, Errors errors) {
		List<FleetIncident> fleetIncidents = fleetIncidentDto.getFleetIncidents();
		if(fleetIncidents != null && !fleetIncidents.isEmpty()) {
			int rowCount = 1;
			for(FleetIncident fleetIncident : fleetIncidents) {

				// Date
				if(fleetIncident.getDate() == null) {
					errors.rejectValue("fleetIncidentErrorMsg", null, null, ValidatorMessages.getString("FleetIncidentService.0")+rowCount+
							ValidatorMessages.getString("FleetIncidentService.1"));
				}

				// Employee Name
				if(fleetIncident.getEmployeeName() == null) {
					errors.rejectValue("fleetIncidentErrorMsg", null, null, ValidatorMessages.getString("FleetIncidentService.2")+rowCount+
							ValidatorMessages.getString("FleetIncidentService.3"));
				} else if(fleetIncident.getEmployeeName().trim().length() > FleetIncident.MAX_CHAR){
					errors.rejectValue("fleetIncidentErrorMsg", null, null, ValidatorMessages.getString("FleetIncidentService.4") + FleetIncident.MAX_CHAR +
							ValidatorMessages.getString("FleetIncidentService.5")+rowCount+ValidatorMessages.getString("FleetIncidentService.6"));
				}

				// Location
				if(fleetIncident.getLocation() == null) {
					errors.rejectValue("fleetIncidentErrorMsg", null, null, ValidatorMessages.getString("FleetIncidentService.7")+rowCount+
							ValidatorMessages.getString("FleetIncidentService.8"));
				} else if (fleetIncident.getLocation().trim().length() > FleetIncident.MAX_CHAR) {
					errors.rejectValue("fleetIncidentErrorMsg", null, null, ValidatorMessages.getString("FleetIncidentService.9") + FleetIncident.MAX_CHAR +
							ValidatorMessages.getString("FleetIncidentService.10")+rowCount+ValidatorMessages.getString("FleetIncidentService.11"));
				}

				// Cause
				if(fleetIncident.getCause() == null) {
					errors.rejectValue("fleetIncidentErrorMsg", null, null, ValidatorMessages.getString("FleetIncidentService.12")+rowCount+
							ValidatorMessages.getString("FleetIncidentService.13"));
				} else if(fleetIncident.getCause().trim().length() > FleetIncident.MAX_CHAR) {
					errors.rejectValue("fleetIncidentErrorMsg", null, null, ValidatorMessages.getString("FleetIncidentService.14") + FleetIncident.MAX_CHAR +
							ValidatorMessages.getString("FleetIncidentService.15")+rowCount+ValidatorMessages.getString("FleetIncidentService.16"));
				}

				// Insurance Claims
				if(fleetIncident.getInsuranceClaims() != null
						&& fleetIncident.getInsuranceClaims().trim().length() > FleetIncident.MAX_CHAR) {
					errors.rejectValue("fleetIncidentErrorMsg", null, null, ValidatorMessages.getString("FleetIncidentService.17") + FleetIncident.MAX_CHAR +
							ValidatorMessages.getString("FleetIncidentService.18")+rowCount+ValidatorMessages.getString("FleetIncidentService.19"));
				}
				rowCount++;
			}
		} else {
			errors.rejectValue("fleetIncidentErrorMsg", null, null, ValidatorMessages.getString("FleetIncidentService.20"));
		}

		// Validate Reference Documents
		referenceDocumentService.validateReferences(fleetIncidentDto.getReferenceDocuments(), errors);
	}
}
