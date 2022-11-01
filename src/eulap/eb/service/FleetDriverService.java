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
import eulap.eb.dao.FleetDriverDao;
import eulap.eb.dao.ReferenceDocumentDao;
import eulap.eb.domain.hibernate.FleetDriver;
import eulap.eb.domain.hibernate.ORType;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.ReferenceDocument;
import eulap.eb.domain.hibernate.User;
import eulap.eb.validator.ValidatorMessages;
import eulap.eb.web.dto.FleetDriverDto;

/**
 * Service class for {@link FleetDriver}

 *
 */
@Service
public class FleetDriverService {
	@Autowired
	private FleetDriverDao fleetDriverDao;
	@Autowired
	private ReferenceDocumentDao referenceDocumentDao;
	@Autowired
	private FleetProfileService fleetProfileService;
	@Autowired
	private ReferenceDocumentService referenceDocumentService;

	/**
	 * Initialize the fleet driver dto.
	 * @param refObjectId The unique id of the reference object.
	 */
	public FleetDriverDto getFleetDriver(int refObjectId) {
		FleetDriverDto driverDto = new FleetDriverDto();
		driverDto.setFleetDrivers(fleetDriverDao.getByRefObject(refObjectId, true));
		driverDto.setReferenceDocuments(referenceDocumentDao.getRDsByEbObject(refObjectId, FleetDriver.OR_TYPE_REF_DOC));
		return driverDto;
	}

	/**
	 * 
	 * @param driverDto The fleet driver dto.
	 * @param user The logged user.
	 */
	public void saveFleetDrivers(FleetDriverDto driverDto, User user) {
		Date currentDate = new Date();
		Integer refObjectId = driverDto.getFpEbObjectId();
		if (refObjectId != null) {
			List<Domain> toBeSaved = null;
			List<Domain> o2os = null;
			List<FleetDriver> fleetDrivers = driverDto.getFleetDrivers();

			// Set to inactive the previous fleet drivers.
			List<FleetDriver> savedFDrivers = fleetDriverDao.getByRefObject(refObjectId, true);
			if (!savedFDrivers.isEmpty()) {
				toBeSaved = new ArrayList<>();
				for (FleetDriver fd : savedFDrivers) {
					fd.setUpdatedBy(user.getId());
					AuditUtil.addAudit(fd, new Audit(user.getId(), false, currentDate));
					fd.setActive(false);
					toBeSaved.add(fd);
					fd = null;
				}
				savedFDrivers = null;
				fleetDriverDao.batchSaveOrUpdate(toBeSaved);
			}

			if (fleetDrivers != null && !fleetDrivers.isEmpty()) {

				// Save the new fleet drivers.
				toBeSaved = new ArrayList<>();
				o2os = new ArrayList<>();
				for (FleetDriver fd : fleetDrivers) {
					int ebObjectId = fleetProfileService.saveAndGetEbObject(user, FleetDriver.OBJECT_TYPE_ID, currentDate);
					AuditUtil.addAudit(fd, new Audit(user.getId(), true, new Date()));
					fd.setActive(true);
					fd.setEbObjectId(ebObjectId);
					toBeSaved.add(fd);
					fd = null;
					o2os.add(ObjectToObject.getInstanceOf(refObjectId, ebObjectId,
							ORType.PARENT_OR_TYPE_ID, user, currentDate));
				}
				fleetDrivers = null;
				fleetDriverDao.batchSave(toBeSaved);
				fleetProfileService.saveDomains(o2os);
			}

			// Deleting the old reference documents.
			List<Domain> toBeDeleted = new ArrayList<>();
			List<ReferenceDocument> savedRefDocs = referenceDocumentDao.getRDsByEbObject(refObjectId, FleetDriver.OR_TYPE_REF_DOC);
			if (!savedRefDocs.isEmpty()) {
				toBeDeleted.addAll(savedRefDocs);
				savedRefDocs = null;
				referenceDocumentDao.batchDelete(toBeDeleted);
				toBeDeleted = null;
			}

			// Saving the new reference documents.
			toBeSaved = new ArrayList<>();
			o2os = new ArrayList<>();
			List<ReferenceDocument> referenceDocuments = driverDto.getReferenceDocuments();
			if (!referenceDocuments.isEmpty()) {
				for (ReferenceDocument rd : referenceDocuments) {
					int ebObjectId = fleetProfileService.saveAndGetEbObject(user, ReferenceDocument.OBJECT_TYPE_ID, currentDate);
					rd.setEbObjectId(ebObjectId);
					toBeSaved.add(rd);
					rd = null;
					o2os.add(ObjectToObject.getInstanceOf(refObjectId, ebObjectId,
							FleetDriver.OR_TYPE_REF_DOC, user, currentDate));
				}
				referenceDocuments = null;
				referenceDocumentDao.batchSave(toBeSaved);
				fleetProfileService.saveDomains(o2os);
			}
			toBeSaved = null;
			o2os = null;
		}
	}

	/**
	 * Validate the fleet driver before saving.
	 * @param fleetDriverDto The dto to be evaluated.
	 * @param errors The binding result object.
	 * @param isNew True if new, otherwise false.
	 */
	public void validate(FleetDriverDto fleetDriverDto, Errors errors, boolean isNew) {
		List<FleetDriver> fleetDrivers = fleetDriverDto.getFleetDrivers();
		String attribName = "fleetDriversErrMsg";
		if (fleetDrivers != null && !fleetDrivers.isEmpty()) {
			int count=1;
			for (FleetDriver fd : fleetDrivers) {
				if(fd.getDate() == null) {
					errors.rejectValue(attribName, null, null, ValidatorMessages.getString("FleetDriverService.0")+count+ValidatorMessages.getString("FleetDriverService.1"));
				}
				if (fd.getName() == null) {
					errors.rejectValue(attribName, null, null, ValidatorMessages.getString("FleetDriverService.2")+count+ValidatorMessages.getString("FleetDriverService.3"));
				} else if(!fd.getName().trim().isEmpty() &&
						fd.getName().trim().length() > FleetDriver.MAX_NAME) {
					errors.rejectValue(attribName, null, null, ValidatorMessages.getString("FleetDriverService.4")+ FleetDriver.MAX_NAME +ValidatorMessages.getString("FleetDriverService.5")+count+ValidatorMessages.getString("FleetDriverService.6"));
				}

				if (fd.getLicenseNo() == null) {
					errors.rejectValue(attribName, null, null, ValidatorMessages.getString("FleetDriverService.7")+count+ValidatorMessages.getString("FleetDriverService.8"));
				}
				else if(!fd.getLicenseNo().trim().isEmpty() &&
						fd.getLicenseNo().trim().length() > FleetDriver.MAX_LICENCSE_NO) {
					errors.rejectValue(attribName, null, null, ValidatorMessages.getString("FleetDriverService.9")+ FleetDriver.MAX_LICENCSE_NO +ValidatorMessages.getString("FleetDriverService.10")+count+ValidatorMessages.getString("FleetDriverService.11"));
				}

				if(fd.getExpirationDate() == null) {
					errors.rejectValue(attribName, null, null, ValidatorMessages.getString("FleetDriverService.12")+count+ValidatorMessages.getString("FleetDriverService.13"));
				}
				count++;
			}
		} else {
			errors.rejectValue(attribName, null, null, ValidatorMessages.getString("FleetDriverService.14"));
		}
		referenceDocumentService.validateReferences(fleetDriverDto.getReferenceDocuments(), errors);
	}
}
