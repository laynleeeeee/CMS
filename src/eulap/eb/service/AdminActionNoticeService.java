package eulap.eb.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.ActionNoticeDao;
import eulap.eb.domain.hibernate.ActionNotice;
import eulap.eb.domain.hibernate.User;

/**
 * Handles Business logic of Action Notice Settings

 *
 */
@Service
public class AdminActionNoticeService {

	@Autowired
	private ActionNoticeDao actionNoticeDao;

	/**
	 * Handles saving of Action Notice Object.
	 * @param actionNotice The Action Notice Object.
	 * @param user The current logged user.
	 * @param result The binding result to be validated.
	 */
	public void saveActionNotice(ActionNotice actionNotice, User user, BindingResult result){
		boolean isNew = actionNotice.getId() == 0;
		AuditUtil.addAudit(actionNotice, new Audit(user.getId(), isNew, new Date()));
		validate(actionNotice, result);
		if(!result.hasErrors()){
			actionNotice.setName(StringFormatUtil.removeExtraWhiteSpaces(actionNotice.getName()));
			actionNoticeDao.saveOrUpdate(actionNotice);
		}
	}

	/**
	 * Evaluates whether the Action Notice already Exists.
	 * @param actionNotice The Action Notice Object to be evaluated.
	 * @return True if a duplication occurs, otherwise false.
	 */
	public boolean isDuplicate(ActionNotice actionNotice){
		if(actionNotice.getId() == 0)
			return actionNoticeDao.isDuplicate(actionNotice);
		ActionNotice oldActionNotice = actionNoticeDao.get(actionNotice.getId());
		if(StringFormatUtil.removeExtraWhiteSpaces(actionNotice.getName()).equalsIgnoreCase(StringFormatUtil.removeExtraWhiteSpaces(oldActionNotice.getName())))
			return false;
		return actionNoticeDao.isDuplicate(actionNotice);
	}

	/**
	 * Get an Action Notice by Id
	 * @param actionNoticeId The id of an Action Notice Object.
	 * @return The Action Notice Object.
	 */
	public ActionNotice getActionNotice(Integer actionNoticeId){
		return actionNoticeDao.get(actionNoticeId);
	}

	/**
	 * Get Action notices by criteria
	 * @param name The name of an Action Notice
	 * @param status 1 if active, 0 if inactive.
	 * @param pageNumber The pageNumber
	 * @return The Paged Collection of Action Notices Object
	 */
	public Page<ActionNotice> getActionNotices(String name, Integer status, Integer pageNumber){
		return actionNoticeDao.searchActionNotice(name, status, new PageSetting(pageNumber, PageSetting.MAX_ADMIN_RECORD));
	}

	/**
	 * Validates the incorrect Action Notice inputs
	 * @param actionNotice The Action Notice Object.
	 * @param errors The errors to be validated.
	 */
	private void validate(ActionNotice actionNotice, Errors errors){
		actionNotice.setName(StringFormatUtil.removeExtraWhiteSpaces(actionNotice.getName()));
		if(actionNotice.getName() == null || actionNotice.getName().isEmpty()){
			errors.rejectValue("name", null, null, "Name is required.");
		} else {
			if(isDuplicate(actionNotice)){
				errors.rejectValue("name", null, null, "Action Notice already exists.");
			}
			if(actionNotice.getName().length() > ActionNotice.MAX_ACTION_NOTICE_NAME){
				errors.rejectValue("name", null, null, "Name must not exceed 25 characters.");
			}
		}
	}
}
