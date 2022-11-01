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
import eulap.eb.dao.FleetDryDockDao;
import eulap.eb.dao.ReferenceDocumentDao;
import eulap.eb.domain.hibernate.FleetDryDock;
import eulap.eb.domain.hibernate.ORType;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.ReferenceDocument;
import eulap.eb.domain.hibernate.User;
import eulap.eb.validator.ValidatorMessages;
import eulap.eb.web.dto.FleetDryDockDto;

/**
 * Handles business logic for {@link FleetDryDock}

 *
 */
@Service
public class FleetDryDockService {
	@Autowired
	private FleetDryDockDao fleetDryDockingDao;
	@Autowired
	private ReferenceDocumentDao referenceDocumentDao;
	@Autowired
	private FleetProfileService fleetProfileService;
	@Autowired
	private ReferenceDocumentService referenceDocumentService;

	/**
	 * Get the FleetDryDockingDTo by refObjectId.
	 * @param refObjectId The refObjectId.
	 * @return The FleetDryDockingDTo.
	 */
	public FleetDryDockDto getDryDocking(Integer refObjectId) {
		FleetDryDockDto fleetDryDockingDto = new FleetDryDockDto();
		fleetDryDockingDto.setDryDockings(getFleetDryDockings(refObjectId));
		fleetDryDockingDto.setReferenceDocuments(referenceDocumentDao.getRDsByEbObject(refObjectId, FleetDryDock.OR_TYPE_REF_DOC_ID));
		return fleetDryDockingDto;
	}

	/**
	 * Get the list of {@link FleetDryDock}
	 * @param refObjectId the refObjectId.
	 * @return The list of FleetDryDocking.
	 */
	public List<FleetDryDock> getFleetDryDockings(Integer refObjectId){
		List<FleetDryDock> dryDockings = fleetDryDockingDao.getFleetDryDockings(refObjectId);
		for(FleetDryDock docking : dryDockings) {
			if(docking.getDate() != null) {
				docking.setStrDate(DateUtil.formatDate(docking.getDate()));
			}
		}
		return dryDockings;
	}

	/**
	 * Save the {@link FleetDryDock}
	 * @param fleetDryDockingDto The DTO class that holds the list of {@link FleetDryDock} to be saved.
	 * @param user The current logged user.
	 */
	public void saveDryDocking(FleetDryDockDto fleetDryDockingDto, User user) {
		if(fleetDryDockingDto.getDryDockings() != null && !fleetDryDockingDto.getDryDockings().isEmpty()) {
			Date currentDate = new Date();
			List<Domain> dryDockingList = new ArrayList<>();
			List<Domain> o2os = new ArrayList<>();

			for(FleetDryDock dryDocking : fleetDryDockingDto.getDryDockings()) {
				boolean isNew = dryDocking.getId() == 0;
				if(dryDocking.getContractor() != null) {
					dryDocking.setContractor(dryDocking.getContractor().trim());
				}
				if(dryDocking.getDate() != null || (dryDocking.getContractor() != null
						&& !dryDocking.getContractor().equals("")) || dryDocking.getIsActual()) {
					dryDocking.setActive(true);
					AuditUtil.addAudit(dryDocking, new Audit(user.getId(), isNew, currentDate));
					if(isNew) {
						dryDocking.setEbObjectId(fleetProfileService.saveAndGetEbObject(user,
								FleetDryDock.OBJECT_TYPE_ID, currentDate));
						o2os.add(ObjectToObject.getInstanceOf(fleetDryDockingDto.getFpEbObjectId(), dryDocking.getEbObjectId(),
								ORType.PARENT_OR_TYPE_ID, user, currentDate));
					} else {
						FleetDryDock savedDryDocking = fleetDryDockingDao.get(dryDocking.getId());
						dryDocking.setCreatedBy(savedDryDocking.getCreatedBy());
						DateUtil.setCreatedDate(dryDocking, savedDryDocking.getCreatedDate());
						dryDocking.setUpdatedBy(user.getId());
						dryDocking.setUpdatedDate(currentDate);
					}
					dryDockingList.add(dryDocking);
				}
			}

			fleetDryDockingDao.batchSaveOrUpdate(dryDockingList);
			fleetProfileService.saveDomains(o2os);

			// Deleting the old reference documents.
			List<Domain> toBeDeleted = new ArrayList<>();
			List<ReferenceDocument> savedRefDocs = referenceDocumentDao.getRDsByEbObject(fleetDryDockingDto.getFpEbObjectId(), FleetDryDock.OR_TYPE_REF_DOC_ID);
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
			for(ReferenceDocument referenceDocument : fleetDryDockingDto.getReferenceDocuments()) {
				ebObjectId = fleetProfileService.saveAndGetEbObject(user,
						ReferenceDocument.OBJECT_TYPE_ID, currentDate);
				referenceDocument.setEbObjectId(ebObjectId);
				refDocs.add(referenceDocument);
				o2os.add(ObjectToObject.getInstanceOf(fleetDryDockingDto.getFpEbObjectId(), ebObjectId,
						FleetDryDock.OR_TYPE_REF_DOC_ID, user, currentDate));
			}
			referenceDocumentDao.batchSave(refDocs);
			fleetProfileService.saveDomains(o2os);
		}
	}

	/**
	 * Handles validation for FleetDryDockingDto
	 * @param fleetDryDockingDto The FleetDryDockingDto to be validated
	 * @param errors The errors detected.
	 */
	public void validate(FleetDryDockDto fleetDryDockingDto, Errors errors) {
		boolean hasValue = false;
		if(fleetDryDockingDto.getDryDockings() != null && !fleetDryDockingDto.getDryDockings().isEmpty()) {
			int rowCount = 1;
			for(FleetDryDock dryDocking : fleetDryDockingDto.getDryDockings()) {
				if(hasDryDock(dryDocking)) {
					hasValue = true;
					if(dryDocking.getContractor() != null && dryDocking.getContractor().trim().length() > FleetDryDock.MAX_CHAR) {
						errors.rejectValue("dryDockingErrMsg", null, null, ValidatorMessages.getString("FleetDryDockService.0") + FleetDryDock.MAX_CHAR+
								ValidatorMessages.getString("FleetDryDockService.1")+rowCount+ValidatorMessages.getString("FleetDryDockService.2"));
					}
				}
				rowCount++;
			}
		}
		if(!hasValue){
			errors.rejectValue("dryDockingErrMsg", null, null, ValidatorMessages.getString("FleetDryDockService.3"));
		}
		// Reference Documents.
		referenceDocumentService.validateReferences(fleetDryDockingDto.getReferenceDocuments(), errors);
	}

	private boolean hasDryDock(FleetDryDock fleetDryDock) {
		if(fleetDryDock.getDate() != null || (fleetDryDock.getContractor() != null
						&& !fleetDryDock.getContractor().equals("")) || fleetDryDock.getIsActual()) {
			return true;
		}
		return false;
	}
}
