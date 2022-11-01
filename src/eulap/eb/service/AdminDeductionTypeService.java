package eulap.eb.service;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.DeductionTypeDao;
import eulap.eb.domain.hibernate.DeductionType;
import eulap.eb.domain.hibernate.User;
import eulap.eb.validator.ValidatorMessages;

/**
 * Service class that will handle business logic for {@link DeductionType} admin setting.

 *
 */
@Service
public class AdminDeductionTypeService {
	private final Logger logger = Logger.getLogger(AdminDeductionTypeService.class);
	@Autowired
	private DeductionTypeDao deductionTypeDao;

	/**
	 * Method for saving deduction type.
	 */
	public void saveDeductionType(User user, DeductionType deductionType, BindingResult result) {
		logger.info("Validating deduction type");
		validate(deductionType, result);
		if(!result.hasErrors()) {
			logger.info("Saving deduction type.");
			int deductionTypeId = deductionType.getId();
			boolean isNewRecord = deductionTypeId == 0 ? true : false;
			AuditUtil.addAudit(deductionType, new Audit(user.getId(), isNewRecord, new Date()));
			deductionType.setName(StringFormatUtil.removeExtraWhiteSpaces(deductionType.getName()));
			deductionTypeDao.saveOrUpdate(deductionType);
		}
	}

	/**
	 * Search method for deduction type.
	 */
	public Page<DeductionType> searchDeductionTypes(User user, String name, String status, Integer pageNumber) {
		SearchStatus searchStatus = SearchStatus.getInstanceOf(status);
		return deductionTypeDao.searchDeductionType(user, name, searchStatus,
				new PageSetting(pageNumber, PageSetting.MAX_ADMIN_RECORD));
	}

	/**
	 * Get the deduction type object by id.
	 */
	public DeductionType getDeductionType(Integer deductionTypeId) {
		return deductionTypeDao.get(deductionTypeId);
	}

	public boolean isDuplicate(DeductionType deductionType) {
		if(deductionType.getId() == 0)
			return deductionTypeDao.isDuplicate(deductionType);
		DeductionType oldTerm = deductionTypeDao.get(deductionType.getId());
		if(StringFormatUtil.removeExtraWhiteSpaces(deductionType.getName()).equalsIgnoreCase(StringFormatUtil.removeExtraWhiteSpaces(oldTerm.getName())))
			return false;
		return deductionTypeDao.isDuplicate(deductionType);
	}

	/**
	 * Method for validating deduction types.
	 */
	private void validate(DeductionType deductionType, Errors errors) {
		String deductionTypeName = StringFormatUtil.removeExtraWhiteSpaces(deductionType.getName());
		if(deductionTypeName.isEmpty()) {
			errors.rejectValue("name", null, null, ValidatorMessages.getString("AdminDeductionTypeService.0"));
		} else if(deductionTypeName.length() > DeductionType.MAX_CHAR_NAME) {
			errors.rejectValue("name", null, null,
					ValidatorMessages.getString("AdminDeductionTypeService.1")+DeductionType.MAX_CHAR_NAME+ValidatorMessages.getString("AdminDeductionTypeService.2"));
		} else if(isDuplicate(deductionType)) {
			errors.rejectValue("name", null, null, ValidatorMessages.getString("AdminDeductionTypeService.3"));
		}
	}
}
