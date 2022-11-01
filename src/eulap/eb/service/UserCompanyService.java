package eulap.eb.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.dao.CompanyDao;
import eulap.eb.dao.UserCompanyDao;
import eulap.eb.dao.UserCompanyHeadDao;
import eulap.eb.dao.UserDao;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.UserCompany;
import eulap.eb.domain.hibernate.UserCompanyHead;

/**
 * The user company service that handle the business layer of the user company object.

 */
@Service
public class UserCompanyService {
	@Autowired
	private UserCompanyDao userCompanyDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private CompanyDao companyDao;
	@Autowired
	private UserCompanyHeadDao companyHeadDao;
	@Autowired
	private UserService userService;
	
	/**
	 * Save data in user company
	 * @param user The user name.
	 */
	public void save(UserCompanyHead userCompanyHead, User user) {
		List<UserCompany> userCompanies = userCompanyHead.getUserCompanies();
		if (userCompanies != null) {
			int userCompanyHeadId = userCompanyHead.getId();
			boolean isNewHead = userCompanyHeadId == 0;
			userCompanyHead.setActive(userCompanyHead.isActive());
			AuditUtil.addAudit(userCompanyHead, new Audit (user.getId(), isNewHead, new Date ()));
			companyHeadDao.saveOrUpdate(userCompanyHead);
			int userId = userCompanyHead.getUsrId();
			List<Integer> toBeDeletedIds = new ArrayList<Integer>();
			List<UserCompany> userComs = userCompanyDao.getUserCompanies(userId);
			if(userComs != null){
				for (UserCompany userCompany : userComs) {
					toBeDeletedIds.add(userCompany.getId());
				}
			}

			if (!toBeDeletedIds.isEmpty()) {
				userCompanyDao.delete(toBeDeletedIds);
				userComs = null;
			}
			for (UserCompany userCompany : userCompanies) {
				userCompany.setUserCompanyHeadId(userCompanyHead.getId());
				AuditUtil.addAudit(userCompany, new Audit (user.getId(), true, new Date ()));
				userCompany.setUserId(userId);
				userCompany.setActive(userCompanyHead.isActive());
				userCompanyDao.save(userCompany);
			}
		}
	}

	/**
	 * Delete the User Company data
	 * @param id The id is user company
	 */
	public void delete(int id) {
		userCompanyDao.delete(id);
	}

	/**
	 *Check if there is Duplicate company
	 * @param userCompanies The user company.
	 * @return True if it is duplicate, otherwise False.
	 */
	public boolean hasDuplicateCompany (List<UserCompany> userCompanies) {
		if (userCompanies != null && !userCompanies.isEmpty()) {
			Map<Integer, UserCompany> hmUserCompany = 
					new HashMap<Integer, UserCompany>();

			UserCompany userCompany = null;
			for (UserCompany uc : userCompanies) {
				if (hmUserCompany.containsKey(uc.getCompanyId())) {
					userCompany = hmUserCompany.get(uc.getCompanyId());
					if (userCompany.getCompanyId() == uc.getCompanyId()) {
						return true;
					}
				} else {
					hmUserCompany.put(uc.getCompanyId(), uc);
				}
			}
		}
		return false;
	}

	/**
	 * Get all the user companies
	 * @param userName The name of a user
	 * @param companyName The name of company.
	 * @param status The status of the User
	 * @param pageNumber pageNumber The page number.
	 * @return The list of User Company in paged format.
	 */
	public Page<UserCompany> getAllUserCompanies(String userName, String companyName, String status, Integer pageNumber) {
		SearchStatus searchStatus = SearchStatus.getInstanceOf(status);
		return userCompanyDao.getAllUserCompanies(userName, companyName, searchStatus, new PageSetting(pageNumber, PageSetting.MAX_ADMIN_RECORD));
	}

	/**
	 * Process the user company by removing lines without user id.
	 * @param userCompanies The list of user companies detail.
	 * @return The process user companies.
	 */
	public List<UserCompany> processUserCompanies (List<UserCompany> userCompanies) {
		List<UserCompany> ret = new ArrayList<UserCompany>();
		if (userCompanies != null && !userCompanies.isEmpty()) {
			for (UserCompany userCompany : userCompanies) {
				if (userCompany.getCompanyId() != 0) {
					userCompany.setCompanyName(companyDao.getCompanyName(userCompany.getCompanyId()));
					ret.add(userCompany);
				}
			}
		}
		return ret;
	}

	/**
	 * Checks if the  user company is active by checking one of the user companies.
	 * @param userId The user id.
	 * @return
	 */
	public boolean isActive (int userId) {
		List<UserCompany> userCompanies = userCompanyDao.getUCByUserAndCompany(userId, null);
		if (userCompanies != null && !userCompanies.isEmpty()) {
			return userCompanies.iterator().next().isActive();
		}
		return false;
	}

	/**
	 * Get the user Company Head per user.
	 * @param userId The user id.
	 * @return The User Company Head per user.
	 */
	public UserCompanyHead getUserCompanyHeadPerUser(Integer userId) {
		UserCompanyHead userCompanyHead = companyHeadDao.getUserCompanyHeadPerUser(userId);
		return userCompanyHead;
	}

	/**
	 * Get the User by search criteria.
	 * @param userName The user name.
	 * @param companyName The company name.
	 * @param searchStatus The status.
	 * @param pageNumber The page number.
	 * @return The User per serch criteria.
	 */
	public Page<User> getUserCompanies(String userName, String companyName,
			String searchStatus, Integer pageNumber) {
		Page<User> users =  userService.getUsersWithCompanies(userName, companyName, searchStatus, pageNumber);
		for (User user : users.getData()) {
			UserCompanyHead userCompanyHead = getUserCompanyHeadPerUser(user.getId());
			if(userCompanyHead != null){
				user.setActive(userCompanyHead.isActive());
			}
		}
		return users;
	}
}
