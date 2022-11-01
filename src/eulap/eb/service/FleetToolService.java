package eulap.eb.service;

import java.util.ArrayList;
import java.util.Collection;
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
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.FleetProfileDao;
import eulap.eb.dao.FleetToolConditionDao;
import eulap.eb.dao.ReferenceDocumentDao;
import eulap.eb.domain.hibernate.FleetToolCondition;
import eulap.eb.domain.hibernate.ORType;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.ReferenceDocument;
import eulap.eb.domain.hibernate.User;
import eulap.eb.validator.ValidatorMessages;
import eulap.eb.web.dto.FleetItemConsumedDto;
import eulap.eb.web.dto.FleetToolDto;

/**
 * Handles business logic for {@link FleetToolCondition}

 *
 */
@Service
public class FleetToolService {
	@Autowired
	private FleetProfileDao fleetProfileDao;
	@Autowired
	private FleetToolConditionDao fleetToolConditionDao;
	@Autowired
	private ReferenceDocumentDao referenceDocumentDao;
	@Autowired
	private FleetProfileService fleetProfileService;
	@Autowired
	private ReferenceDocumentService referenceDocumentService;

	public FleetToolDto getFleetToolDto(Integer refObjectId, Integer divisionId, int pageNumber, Date asOfDate) {
		FleetToolDto fleetToolDto = new FleetToolDto();
		Collection<FleetToolCondition> fleetToolConditions = new ArrayList<>();
		Page<FleetItemConsumedDto> itemsConsumed = fleetProfileDao.getFleetItemsConsumed(divisionId, null, asOfDate,
				null, null, true, FleetToolDto.TOOL_ITEM_CATEGORY_ID, new PageSetting(pageNumber, PageSetting.MAX_ADMIN_RECORD));
		if (!itemsConsumed.getData().isEmpty()) {
			int totalSize = itemsConsumed.getTotalRecords();
			FleetToolCondition ftc = null;
			for (FleetItemConsumedDto ic : itemsConsumed.getData()) {
				ftc = fleetToolConditionDao.getByWSItem(ic.getEbObjectId());
				if (ftc == null) {
					ftc = new FleetToolCondition();
				}
				ftc.setStockCode(ic.getStockCode());
				ftc.setFleetItemConsumedDto(ic);
				ftc.setItemId(ic.getItemId());
				ic = null;
				fleetToolConditions.add(ftc);
			}
			itemsConsumed = null;
			fleetToolDto.setFleetToolConditions(new Page<>(new PageSetting(pageNumber, PageSetting.MAX_ADMIN_RECORD),
					fleetToolConditions, totalSize));
			fleetToolConditions = null;
		}
		if (refObjectId != null) {
			fleetToolDto.setReferenceDocuments(referenceDocumentDao.getRDsByEbObject(refObjectId, FleetToolDto.OR_TYPE_ID));
		}
		return fleetToolDto;
	}

	/**
	 * Save the fleet conditions and documents.
	 * @param fleetToolDto The DTO Object that contains the list of conditions to be saved.
	 * @param user The logged user.
	 */
	public void saveFleetTool(FleetToolDto fleetToolDto, User user) {
		Date currentDate = new Date();
		int refObjectId = fleetToolDto.getFpEbObjectId();
		fleetToolDto.deserializeFleetToolConditions();
		List<FleetToolCondition> fleetToolConditions = fleetToolDto.getLsFleetToolConditions();
		List<Domain> toBeSaved = new ArrayList<>();
		List<Domain> o2os = new ArrayList<>();

		// Set to inactive the previous fleet tools.
		List<FleetToolCondition> ftcs = new ArrayList<>(fleetToolConditions);
		List<FleetToolCondition> savedFTools = fleetToolConditionDao.getByRefObject(refObjectId);
		ftcs.addAll(savedFTools);

		Map<Integer, FleetToolCondition> hmConditions = new HashMap<>();
		List<FleetToolCondition> currPageFtcs = new ArrayList<>();
		if(ftcs != null && !ftcs.isEmpty()) {
			for(FleetToolCondition ftc : ftcs) {
				if(hmConditions.containsKey(ftc.getRefObjectId())) {
					currPageFtcs.add(ftc);
				} else {
					hmConditions.put(ftc.getRefObjectId(), ftc);
				}
				ftc = null;
			}
			ftcs = null;
			savedFTools = null;
		}

		if (!currPageFtcs.isEmpty()) {
			toBeSaved = new ArrayList<>();
			for (FleetToolCondition ftc : currPageFtcs) {
				ftc.setUpdatedBy(user.getId());
				AuditUtil.addAudit(ftc, new Audit(user.getId(), false, currentDate));
				ftc.setActive(false);
				toBeSaved.add(ftc);
				ftc = null;
			}
			currPageFtcs = null;
			fleetToolConditionDao.batchSaveOrUpdate(toBeSaved);
			toBeSaved = null;
		}

		if (fleetToolConditions != null && !fleetToolConditions.isEmpty()) {
			toBeSaved = new ArrayList<>();

			for (FleetToolCondition ft : fleetToolConditions) {
				int ebObjectId = fleetProfileService.saveAndGetEbObject(user, FleetToolCondition.OBJECT_TYPE_ID, currentDate);
				AuditUtil.addAudit(ft, new Audit(user.getId(), true, new Date()));
				ft.setEbObjectId(ebObjectId);
				ft.setActive(true);
				toBeSaved.add(ft);

				// Saving the Object To Object - Withdrawal slip item and fleet Profile.
				o2os.add(ObjectToObject.getInstanceOf(refObjectId, ebObjectId,
						ORType.PARENT_OR_TYPE_ID, user, currentDate));

				// Saving the Object To Object - Withdrawal slip item and fleet tool condition.
				o2os.add(ObjectToObject.getInstanceOf(ft.getRefObjectId(), ebObjectId,
						FleetToolCondition.OR_TYPE_ID, user, currentDate));
				ft = null;
			}
			fleetToolConditions = null;
			fleetProfileService.saveDomains(o2os);
			o2os = null;
			fleetToolConditionDao.batchSave(toBeSaved);
			toBeSaved = null;
		}

		// Deleting the old reference documents.
		List<Domain> toBeDeleted = new ArrayList<>();
		List<ReferenceDocument> savedRefDocs = referenceDocumentDao.getRDsByEbObject(refObjectId, FleetToolDto.OR_TYPE_ID);
		if (!savedRefDocs.isEmpty()) {
			toBeDeleted.addAll(savedRefDocs);
			savedRefDocs = null;
			referenceDocumentDao.batchDelete(toBeDeleted);
			toBeDeleted = null;
		}

		// Saving the new reference documents.
		toBeSaved = new ArrayList<>();
		o2os = new ArrayList<>();
		List<ReferenceDocument> referenceDocuments = fleetToolDto.getReferenceDocuments();
		if (!referenceDocuments.isEmpty()) {
			for (ReferenceDocument rd : referenceDocuments) {
				int ebObjectId = fleetProfileService.saveAndGetEbObject(user, ReferenceDocument.OBJECT_TYPE_ID, currentDate);
				rd.setEbObjectId(ebObjectId);
				toBeSaved.add(rd);
				rd = null;
				o2os.add(ObjectToObject.getInstanceOf(refObjectId, ebObjectId,
						FleetToolDto.OR_TYPE_ID, user, currentDate));
			}
			referenceDocuments = null;
			referenceDocumentDao.batchSave(toBeSaved);
			fleetProfileService.saveDomains(o2os);
		}
		toBeSaved = null;
		o2os = null;
	}

	/**
	 * Handles validation of fleet tools.
	 * @param fleetToolDto The FleetToolDto.
	 * @param errors The errors to be validated.
	 */
	public void validate(FleetToolDto fleetToolDto, Errors errors) {
		List<FleetToolCondition> conditions = fleetToolDto.getLsFleetToolConditions();
		List<FleetToolCondition> savedFTools = fleetToolConditionDao.getByRefObject(fleetToolDto.getFpEbObjectId());
		boolean hasDupItem = false;
		Map<Integer, FleetToolCondition> hmConditions = null;
		if (conditions != null && !conditions.isEmpty()) {
			conditions.addAll(savedFTools);
			hmConditions = new HashMap<>();
			for(FleetToolCondition ftc : new ArrayList<>(conditions)) {
				if(hmConditions.containsKey(ftc.getRefObjectId())) {
					conditions.remove(ftc);
				} else {
					hmConditions.put(ftc.getRefObjectId(), ftc);
				}
			}

			hmConditions = new HashMap<>();
			FleetToolCondition ft = null;
			for(FleetToolCondition ftc : conditions) {
				if(hmConditions.containsKey(ftc.getItemId())) {
					ft = hmConditions.get(ftc.getItemId());
					if (ft.getStatus().booleanValue() && ftc.getStatus().booleanValue()) {
						hasDupItem = true;
					}
				} else {
					if(ftc.getStatus()) {
						hmConditions.put(ftc.getItemId(), ftc);
					}
				}
			}

			if(hasDupItem) {
				errors.rejectValue("fleetToolsErrMsg", null, null, ValidatorMessages.getString("FleetToolService.0"));
			}
		}
		referenceDocumentService.validateReferences(fleetToolDto.getReferenceDocuments(), errors);
	}
}
