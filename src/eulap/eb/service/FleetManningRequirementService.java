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
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.dao.FleetManningRequirementDao;
import eulap.eb.dao.ObjectToObjectDao;
import eulap.eb.dao.ReferenceDocumentDao;
import eulap.eb.domain.hibernate.FleetManningRequirement;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.ReferenceDocument;
import eulap.eb.domain.hibernate.User;
import eulap.eb.validator.ValidatorMessages;
import eulap.eb.web.dto.FleetManningRequirementDto;

/**
 * Service class for {@link FleetManningRequirement}

 *
 */
@Service
public class FleetManningRequirementService {
	@Autowired
	private FleetProfileService fleetProfileService;
	@Autowired
	private ReferenceDocumentDao documentDao;
	@Autowired
	private ObjectToObjectDao objectToObjectDao;
	@Autowired
	private FleetManningRequirementDao manningRequirementDao;
	@Autowired
	private ReferenceDocumentService documentService;
	

	/**
	 * Saving manning requirement.
	 * @param manningRequirementDto The manning requirement dto.
	 */
	public void saveFleetManningRequirement(FleetManningRequirementDto manningRequirementDto, User user) {
		int refObjectId = manningRequirementDto.getFpEbObjectId();
		Date currentDate = new Date();

		FleetManningRequirement manningRequirement = manningRequirementDto.getManningRequirement();
		boolean isNew = manningRequirement.getId() == 0;
		AuditUtil.addAudit(manningRequirement, new Audit(user.getId(), isNew, currentDate));
		int ebObjectId = 0;
		if(isNew){
			ebObjectId = fleetProfileService.saveAndGetEbObject(user, FleetManningRequirement.OBJECT_TYPE_ID, currentDate);
		} else {
			ebObjectId = manningRequirement.getEbObjectId();
		}
		manningRequirement.setEbObjectId(ebObjectId);
		if(isNew){
			objectToObjectDao.save(ObjectToObject.getInstanceOf(refObjectId, ebObjectId,
					manningRequirement.getOrTypeId(), user, currentDate));
		} else {
			List<ObjectToObject> objectToObjects = objectToObjectDao.getReferenceObjects(refObjectId, manningRequirement.getEbObjectId(), null);
			if(objectToObjects != null && !objectToObjects.isEmpty()){
				ObjectToObject objectToObject = objectToObjects.iterator().next();
				objectToObject.setOrTypeId(manningRequirement.getOrTypeId());
				objectToObjectDao.update(objectToObject);
			}
		}
		manningRequirementDao.saveOrUpdate(manningRequirement);

		saveReferennceDocument(manningRequirementDto.getReferenceDocuments(), manningRequirement.getEbObjectId(), user, currentDate);
	}

	private void saveReferennceDocument(List<ReferenceDocument> referenceDocuments, Integer refObjectId, User user, Date currentDate){
		List<Domain> documents = new ArrayList<>();
		List<Domain> o2os = new ArrayList<>();
		for (ReferenceDocument document : referenceDocuments) {
			boolean isNew = document.getId() == 0;
			AuditUtil.addAudit(document, new Audit(user.getId(), isNew, currentDate));
			if(document.getEbObjectId() == null){
				int ebObjectId = fleetProfileService.saveAndGetEbObject(user, ReferenceDocument.OBJECT_TYPE_ID, currentDate);
				document.setEbObjectId(ebObjectId);
				o2os.add(ObjectToObject.getInstanceOf(refObjectId, ebObjectId, 
						FleetManningRequirement.REFERENCE_DOCUMENT_OR_TYPE_ID, user, currentDate));
			}
			documents.add(document);
		}
		documentDao.batchSaveOrUpdate(documents);
		objectToObjectDao.batchSaveOrUpdate(o2os);
	}

	/**
	 * Get {@link List<FleetManningRequirement>} by reference of object id.
	 * @param refObjectId The reference object id.
	 * @return {@link List<FleetManningRequirement>}.
	 */
	public List<FleetManningRequirement> getAllFleetMRByRefObjectId(Integer refObjectId, Integer orTypeId) {
		return manningRequirementDao.getAllFleetMRByRefObjectId(refObjectId, orTypeId);
	}

	/**
	 * Get the reference documents by reference object id.
	 * @param refObjectId The reference object id.
	 * @return {@link List<ReferenceDocument>}.
	 */
	public List<ReferenceDocument> getReferenceDocumentByRefObjectId(Integer refObjectId) {
		return documentDao.getRDsByEbObject(refObjectId, FleetManningRequirement.REFERENCE_DOCUMENT_OR_TYPE_ID);
	}

	/**
	 * Validate the fleet insurance permit renewals before saving.
	 * @param manningRequirementDto The dto to be evaluated.
	 * @param errors The binding result object.
	 */
	public void validate(FleetManningRequirementDto manningRequirementDto, Errors errors) {
		FleetManningRequirement manningRequirement = manningRequirementDto.getManningRequirement();
		if ((manningRequirement.getLicense() != null && !manningRequirement.getLicense().trim().isEmpty()) &&
				manningRequirement.getLicense().length() > FleetManningRequirement.MAX_LICENSE) {
			errors.rejectValue("manningRequirement.license", null, null, ValidatorMessages.getString("FleetManningRequirementService.0")+
					FleetManningRequirement.MAX_LICENSE +ValidatorMessages.getString("FleetManningRequirementService.1"));
		}
		if(manningRequirement.getNumber() == null || manningRequirement.getNumber().trim().isEmpty()){
			errors.rejectValue("manningRequirement.number", null, null, ValidatorMessages.getString("FleetManningRequirementService.2"));
		} else if (manningRequirement.getNumber() != null && manningRequirement.getNumber().length() > FleetManningRequirement.MAX_NUMBER) {
			errors.rejectValue("manningRequirement.number", null, null, ValidatorMessages.getString("FleetManningRequirementService.3")+
					FleetManningRequirement.MAX_NUMBER +ValidatorMessages.getString("FleetManningRequirementService.4"));
		}
		if(manningRequirement.getPosition() == null || manningRequirement.getPosition().trim().isEmpty()){
			errors.rejectValue("manningRequirement.position", null, null, ValidatorMessages.getString("FleetManningRequirementService.5"));
		} else if (manningRequirement.getPosition().length() > FleetManningRequirement.MAX_POSITION) {
			errors.rejectValue("manningRequirement.position", null, null, ValidatorMessages.getString("FleetManningRequirementService.6")+
					FleetManningRequirement.MAX_POSITION +ValidatorMessages.getString("FleetManningRequirementService.7"));
		}
		if(!isUniqueManningRequirements(manningRequirement, manningRequirementDto.getFpEbObjectId())){
			errors.rejectValue("manningRequirement.active", null, null, ValidatorMessages.getString("FleetManningRequirementService.8"));
		}
		documentService.validateReferences(manningRequirementDto.getReferenceDocuments(), errors);
	}

	/**
	 * Check if manning is unique per License, Number, Position and Department.
	 * @param manningRequirement The manning requirements main object.
	 * @param refObjectId The reference object.
	 * @return True if manning requirement is unique, Otherwise false.
	 */
	private boolean isUniqueManningRequirements(FleetManningRequirement manningRequirement, Integer refObjectId) {
		return manningRequirementDao.isUniqueManningRequirements(manningRequirement, refObjectId);
	}

	/**
	 * Get the page of Fleet Manning requirements base on the filters.
	 * @param position
	 * @param license
	 * @param number
	 * @param remarks
	 * @param department
	 * @param status
	 * @param pageNumber
	 * @param refObjectId
	 * @return {@link Page<FleetManningRequirement>}
	 */
	public Page<FleetManningRequirement> searchManningRequirements(String position, String license, String number,
			String remarks, String department, String status, Integer pageNumber, Integer refObjectId) {
		SearchStatus searchStatus = SearchStatus.getInstanceOf(status);
		Page<FleetManningRequirement> result = manningRequirementDao.searchManningRequirements(position, license, number, remarks, department, refObjectId,
				searchStatus, new PageSetting(pageNumber, PageSetting.MAX_ADMIN_RECORD));
		for (FleetManningRequirement manningRequirement : result.getData()) {
			if(department != null && !department.trim().isEmpty() && !department.equals("All")){
				manningRequirement.setDepartment(department);
			} else {
				List<ObjectToObject> objectToObjects = objectToObjectDao.getReferenceObjects(refObjectId, manningRequirement.getEbObjectId(), null);
				if(objectToObjects != null && !objectToObjects.isEmpty()){
					Integer orTypeId= objectToObjects.iterator().next().getOrTypeId();
					manningRequirement.setDepartment(orTypeId.equals(FleetManningRequirement.MANNING_REQUIREMENT_DECK_OR_TYPE_ID) ?
							FleetManningRequirement.DEPARTMENT_DECK : FleetManningRequirement.DEPARTMENT_ENGINE);
				}
			}
		}
		return result;
	}

	/**
	 * Get {@link FleetManningRequirement} by manning requirement and refobject id.
	 * @param manningRequirementId The manning requirement id.
	 * @param refObjectId The reference object id.
	 * @return {@link FleetManningRequirement}
	 */
	public FleetManningRequirement getManningRequirementsById(Integer manningRequirementId, Integer refObjectId) {
		FleetManningRequirement manningRequirement = manningRequirementDao.get(manningRequirementId);
		List<ObjectToObject> objectToObjects = objectToObjectDao.getReferenceObjects(refObjectId, manningRequirement.getEbObjectId(), null);
		if(objectToObjects != null && !objectToObjects.isEmpty()){
			Integer orTypeId= objectToObjects.iterator().next().getOrTypeId();
			manningRequirement.setDepartment(orTypeId.equals(FleetManningRequirement.MANNING_REQUIREMENT_DECK_OR_TYPE_ID) ?
					FleetManningRequirement.DEPARTMENT_DECK : FleetManningRequirement.DEPARTMENT_ENGINE);
		}
		return manningRequirement;
	}
}
