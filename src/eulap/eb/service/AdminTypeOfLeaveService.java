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
import eulap.eb.dao.TypeOfLeaveDao;
import eulap.eb.domain.hibernate.TypeOfLeave;
import eulap.eb.domain.hibernate.User;
import eulap.eb.validator.ValidatorMessages;

/**
 * Service class that will handle business logic for {@link TypeOfLeave}

 *
 */
@Service
public class AdminTypeOfLeaveService {
	private final Logger logger = Logger.getLogger(AdminTypeOfLeaveService.class);
	@Autowired
	private TypeOfLeaveDao typeOfLeaveDao;

	/**
	 * Service method for saving type of leave.
	 */
	public void saveLeave(User user, TypeOfLeave typeOfLeave, BindingResult result) {
		logger.info("Validating leave type");
		validate(typeOfLeave, result);
		if(!result.hasErrors()) {
			logger.info("Saving leave type");
			int typeOfleaveId = typeOfLeave.getId();
			boolean isNewRecord = typeOfleaveId == 0 ? true : false;
			AuditUtil.addAudit(typeOfLeave, new Audit(user.getId(), isNewRecord, new Date()));
			typeOfLeave.setName(StringFormatUtil.removeExtraWhiteSpaces(typeOfLeave.getName()));
			typeOfLeaveDao.saveOrUpdate(typeOfLeave);
		}
	}

	/**
	 * Service method for searching type of leave.
	 */
	public Page<TypeOfLeave> searchLeaves(User user, String name, String status, Integer pageNumber) {
		SearchStatus searchStatus = SearchStatus.getInstanceOf(status);
		return typeOfLeaveDao.searchLeaves(user, name, searchStatus,
				new PageSetting(pageNumber, PageSetting.MAX_ADMIN_RECORD));
	}

	/**
	 * Get the type of leave object by id.
	 */
	public TypeOfLeave getTypeOfLeave(Integer typeOfLeaveId) {
		return typeOfLeaveDao.get(typeOfLeaveId);
	}

	/**
	 * Check if the leave name is a duplicate.
	 */
	public boolean isDuplicate(TypeOfLeave typeOfLeave) {
		if(typeOfLeave.getId() == 0)
			return typeOfLeaveDao.isDuplicate(typeOfLeave);
		TypeOfLeave oldLeave = typeOfLeaveDao.get(typeOfLeave.getId());
		if(StringFormatUtil.removeExtraWhiteSpaces(typeOfLeave.getName()).equalsIgnoreCase(StringFormatUtil.removeExtraWhiteSpaces(oldLeave.getName())))
			return false;
		return typeOfLeaveDao.isDuplicate(typeOfLeave);
	}

	/**
	 * Validation method for when saving type of leave.
	 */
	private void validate(TypeOfLeave typeOfLeave, Errors errors) {
		String leaveName = StringFormatUtil.removeExtraWhiteSpaces(typeOfLeave.getName());
		if(leaveName.isEmpty()) {
			errors.rejectValue("name", null, null, ValidatorMessages.getString("AdminTypeOfLeaveService.0"));
		} else if(leaveName.length() > TypeOfLeave.MAX_CHAR_NAME) {
			errors.rejectValue("name", null, null,
					ValidatorMessages.getString("AdminTypeOfLeaveService.1")+TypeOfLeave.MAX_CHAR_NAME+ValidatorMessages.getString("AdminTypeOfLeaveService.2"));
		} else if(isDuplicate(typeOfLeave)) {
			errors.rejectValue("name", null, null, ValidatorMessages.getString("AdminTypeOfLeaveService.3"));
		}

		if(typeOfLeave.getDescription().isEmpty()) {
			errors.rejectValue("description", null, null, ValidatorMessages.getString("AdminTypeOfLeaveService.4"));
		}
	}
}
