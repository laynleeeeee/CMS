package eulap.eb.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.dao.UserCustodianDao;
import eulap.eb.dao.UserCustodianLinesDao;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.UserCustodian;
import eulap.eb.domain.hibernate.UserCustodianLines;
import eulap.eb.validator.ValidatorMessages;

/**
 * Class that handles the business logic {@link UserCustodian}

 *
 */
@Service
public class UserCustodianService {
	@Autowired
	private UserCustodianDao userCustodianDao;
	@Autowired
	private UserCustodianLinesDao userCustodianLinesDao;
	@Autowired
	private UserService userService;

	/**
	 * Get the list of product list.
	 * @param productLine The main item stock code.
	 * @param rawMaterial The raw material stock code.
	 * @param status The search status.
	 * @param pageNumber The page number.
	 * @return The list of product line.
	 */
	public Page<UserCustodian> searchUserCustodians(Integer companyId, Integer divisionId, String userCustodianName, String status, int pageNumber) {
		SearchStatus searchStatus = SearchStatus.getInstanceOf(status);
		Page<UserCustodian> userCustodians = userCustodianDao.getUserCustodians(companyId, divisionId, userCustodianName, searchStatus,
				new PageSetting(pageNumber, PageSetting.MAX_ADMIN_RECORD));
		for (UserCustodian uc : userCustodians.getData()) {
			uc.setUserCustodianLines(userCustodianLinesDao.getAllByRefId("userCustodianId", uc.getId()));
		}
		return userCustodians;
	}

	/**
	 * Get user custodian by id.
	 * @param userCustodianId The user custodian id.
	 * @return The user custodian object
	 */
	public UserCustodian getUserCustodianObj(Integer userCustodianId) {
		return userCustodianDao.get(userCustodianId);
	}

	/**
	 * Get user custodian by id.
	 * @param userCustodianId The user custodian id.
	 * @return The user custodian object
	 */
	public UserCustodian getUserCustodian(Integer userCustodianId) {
		UserCustodian userCustodian = getUserCustodianObj(userCustodianId);
		List<UserCustodianLines> userCustodianLines = userCustodianLinesDao.getAllByRefId("userCustodianId", userCustodianId);
		for (UserCustodianLines ucl : userCustodianLines) {
			User user = userService.getUser(ucl.getUserId());
			ucl.setUserName(user.getFirstName() +" "+ user.getLastName());
		}
		userCustodian.setUserCustodianLines(userCustodianLines);;
		return userCustodian;
	}

	/**
	 * Save the user custodian.
	 * @param userCustodian The user custodian.
	 * @param user The user currently logged.
	 */
	public void saveUserCustodian(UserCustodian userCustodian, User user) {
		int userCustodianId = userCustodian.getId();
		boolean isNew = userCustodianId == 0;
		AuditUtil.addAudit(userCustodian, new Audit (user.getId(), isNew, new Date ()));
		if (!isNew) {
			List<UserCustodianLines> userCustodianLines = userCustodianLinesDao.getAllByRefId("userCustodianId", userCustodianId);
			if (userCustodianLines != null && !userCustodianLines.isEmpty()){
				List<Integer> toBeDeleteLineItemIds = new ArrayList<Integer>();
				for (UserCustodianLines userCustodianLine : userCustodianLines) {
					toBeDeleteLineItemIds.add(userCustodianLine.getId());
				}
				if (!toBeDeleteLineItemIds.isEmpty()) {
					userCustodianLinesDao.delete(toBeDeleteLineItemIds);
				}
				toBeDeleteLineItemIds = null;
			}
		}
		userCustodianDao.saveOrUpdate(userCustodian);
		for (UserCustodianLines userCustodianLine : userCustodian.getUserCustodianLines()) {
			userCustodianLine.setUserCustodianId(userCustodian.getId());
			userCustodianLinesDao.save(userCustodianLine);
		}
	}

	/**
	 * Validate the user custodian form
	 * @param userCustodian The user custodian object
	 * @param errors The validation errors
	 */
	public void validate(UserCustodian userCustodian, Errors errors) {
		if (userCustodian.getCustodianAccountId() == null) {
			errors.rejectValue("custodianAccount.custodianName", null, null, ValidatorMessages.getString("UserCustodianService.0"));
		} else if(userCustodianDao.isDuplicateUserCustodian(userCustodian)){
				errors.rejectValue("custodianAccount.custodianName", null, null, ValidatorMessages.getString("UserCustodianService.1"));
		}
		List<UserCustodianLines> userCustodianLines = userCustodian.getUserCustodianLines();
		if (userCustodianLines.isEmpty() || userCustodianLines == null) {
			errors.rejectValue("errorMessage", null, null, ValidatorMessages.getString("UserCustodianService.2"));
		} else {
			User user = null;
			int row = 0;
			for (UserCustodianLines ucl: userCustodianLines) {
				row++;
				Integer userId = ucl.getUserId();
				boolean hasUserName = ucl.getUserName() != null && !ucl.getUserName().isEmpty();
				if (hasUserName && userId == null) {
					errors.rejectValue("errorMessage", null, null,
							String.format(ValidatorMessages.getString("UserCustodianService.5"), row));
				}
				if (userId != null) {
					user = userService.getUser(userId);
					if (user != null && !user.isActive()) {
						errors.rejectValue("errorMessage", null, null,
								String.format(ValidatorMessages.getString("UserCustodianService.3"), user.getFullName(), row));
						user = null;
						break;
					}
				}
			}
			if (hasDuplicateUser(userCustodianLines)) {
				errors.rejectValue("errorMessage", null, null, ValidatorMessages.getString("UserCustodianService.4"));
			}
		}
	}

	private Boolean hasDuplicateUser(List<UserCustodianLines> userCustodianLines) {
		List<Integer> userIds = new ArrayList<Integer>();
		for (UserCustodianLines ucl : userCustodianLines) {
			if (ucl.getUserId() != null) {
				userIds.add(ucl.getUserId());
			}
		}
		Set<Integer> set = new HashSet<Integer>(userIds);
		if(set.size() < userIds.size()){
			return true;
		}
		return false;
	}

	/**
	 * Process user custodian line to remove empty lines
	 * @param userCustodian The user custodian object
	 */
	public void processLines(UserCustodian userCustodian) {
		List<UserCustodianLines> ret = new ArrayList<UserCustodianLines>();
		for (UserCustodianLines ucl : userCustodian.getUserCustodianLines()) {
			if (ucl.getUserName() != null) {
				ret.add(ucl);
			}
		}
		userCustodian.setUserCustodianLines(ret);
	}

	/**
	 * Gets the list of {@link UserCustodian}
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @param userCustodianId The user custodian id.
	 * @return List of {@link UserCustodian}
	 */
	public List<UserCustodian> getUserCustodians(Integer companyId, Integer divisionId, Integer userCustodianId){
		return userCustodianDao.getUserCustodians(companyId, divisionId, userCustodianId);
	}

	/**
	 * Gets the list of {@link UserCustodian}
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @param User The user currently logged in.
	 * @param activeOnly If active only.
	 * @return List of {@link UserCustodian}
	 */
	public List<UserCustodian> getUserCustodiansByUser(Integer companyId, Integer divisionId, User user, Integer userCustodianId){
		List<UserCustodian> userCustodians = getUserCustodians(companyId, divisionId, userCustodianId);
		List<UserCustodian> filteredUCs = new ArrayList<UserCustodian>();
		for(UserCustodian uc : userCustodians) {
			List<UserCustodianLines> ucls = userCustodianLinesDao.getUserCustodianLines(uc.getId(), user.getId());
			if(ucls != null && !ucls.isEmpty()) {
				filteredUCs.add(uc);
			}
		}
		return filteredUCs;
	}

	/**
	 * Get the list of user custodian
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @param name The custodian name
	 * @param isExact True if exact value, otherwise false
	 * @return The list of user custodian
	 */
	public List<UserCustodian> getUserCustodianByName(Integer companyId, Integer divisionId,
			Integer userCustodianId, String name, boolean isExact) {
		return userCustodianDao.getUserCustodians(companyId, divisionId, userCustodianId, name, isExact);
	}
}