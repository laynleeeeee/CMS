package eulap.eb.service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bp.web.ar.AuditUtil;
import eulap.common.domain.AccessRight;
import eulap.common.domain.Audit;
import eulap.common.dto.LoginCredential;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.common.util.SimpleCryptoUtil;
import eulap.eb.dao.CompanyProductDao;
import eulap.eb.dao.EBObjectDao;
import eulap.eb.dao.UgMAccessRightDao;
import eulap.eb.dao.UgMFAccessRightDao;
import eulap.eb.dao.UserCompanyDao;
import eulap.eb.dao.UserDao;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.CompanyProduct;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.UgMAccessRight;
import eulap.eb.domain.hibernate.UgMFAccessRight;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.UserCompany;
import eulap.eb.domain.hibernate.UserLoginStatus;
import eulap.eb.webservice.WebServiceCredential;

/**
 * The user service that handle the business layer of the user object.

 *
 */
@Service
public class UserService {
	private final UserDao userDao;
	@Autowired
	private UserCompanyDao userCompanyDao;
	@Autowired
	private UserLoginStatusService loginStatusService;
	private final CompanyProductDao companyProductDao;
	private final UgMAccessRightDao uGMAccessRightDao;
	private final UgMFAccessRightDao uGMFAccessRightDao;
	@Autowired
	private EBObjectDao ebObjectDao;


	@Autowired
	public UserService (UserDao userDao, CompanyProductDao companyProductDao, 
			UgMAccessRightDao uGMAccessRightDao, UgMFAccessRightDao uGMFAccessRightDao) {
		this.userDao = userDao;
		this.companyProductDao = companyProductDao;
		this.uGMAccessRightDao = uGMAccessRightDao;
		this.uGMFAccessRightDao = uGMFAccessRightDao;
	}

	/**
	 * Get all the users.
	 * @return The list of users wrapped in a page.
	 */
	public Page<User> getALLUsers () {
		return userDao.getUsers();
	}

	public Page<User> searchUsers (String criteria, String field, String status, PageSetting pageSetting){
		LabelName2FieldName ln2Fn = LabelName2FieldName.getInstanceOf(field);
		SearchStatus ss = SearchStatus.getInstanceOf(status);
		return userDao.searchUsers(ln2Fn, ss, criteria, pageSetting);
	}

	/**
	 * Get the list of users.
	 * @param searchCriteria The search criteria.
	 * @param pageNumber The page number
	 * @return The paged date of users.
	 */

	public Page<User> getUsers (String searchCriteria, String searchCategory, int pageNumber) {
		PageSetting pageSetting = new PageSetting(pageNumber);
		return getUsers(searchCriteria, searchCategory, pageSetting);
	}

	private Page<User> getUsers (String searchCriteria, String searchCategory, PageSetting pageSetting) {
		// * means search all
		if (searchCriteria.equals("*"))
			searchCriteria = "";
		return userDao.getUsers(pageSetting, searchCriteria, searchCategory);
	}

	/**
	 * Get the list of all users.
	 * @return The list of users.
	 */
	public List<User> getAll () {
		return new ArrayList<User>(userDao.getAll());
	}

	/**
	 * Save the user.
	 * @param user The user to be saved.
	 * @param isNewRecord True if new record, otherwise false.
	 * @param ui user information
	 */
	public void save (User user, User loggedUser, boolean isNewRecord){
		AuditUtil.addAudit(user, new Audit (loggedUser.getId(), isNewRecord, new Date ()));
		userDao.saveOrUpdate(user);
	}
	
	public User getUser (int userId) {
		User user = userDao.get(userId);
		if (user != null)
			user.setUserCompanies(userCompanyDao.getUserCompanies(user.getId()));
		return user;
	}

	/**
	 * validate if the user name is unique
	 * @param userName The user name
	 * @return True if the username is unique, otherwise false.
	 */
	public boolean isUniqueUserName (String userName) {
		return userDao.isUniqueUserName(userName);
	}

	/**
	 * get the companies of the user.
	 * @param user The user company
	 * @return The list of companies.
	 */
	public Collection<Company> getUserCompanies (User user) {
		Collection<Company> cs = new ArrayList<Company>();
		for (UserCompany uc : user.getUserCompanies()) 
			cs.add(uc.getCompany());
		return cs;
	}

	/**
	 * Validate the user give the credential
	 * @param credential The user's credential
	 * @return True if valid user, otherwise false.
	 */
	public boolean validateUser (LoginCredential credential) {
		return userDao.validateUser(credential);
	}

	/**
	 * Get the logged in user based on the credential
	 * @param credential The credential of logged in user.
	 * @return The logged in user information
	 */
	public User getUser (LoginCredential credential) throws NoSuchAlgorithmException {
		String shaEncryptedPassword = SimpleCryptoUtil.convertToSHA1(credential.getPassword());
		User user = userDao.getUser(credential.getUserName(), shaEncryptedPassword);
		//TODO: EASY FIX. The root problem is in the hibernate mapping for user companies.
		//TODO: It might be the mapping for one-to-many relationship is wrong. Need further investigation. 
		if (user != null) {
			user.setUserCompanies(userCompanyDao.getUserCompanies(user.getId()));
			CompanyProduct cp = companyProductDao.getCompanyProduct(user.getCompanyId());
			user.setAccessCode(cp.getCode());	
			Collection<UgMAccessRight> uGMAccessRight = uGMAccessRightDao.getUgMAccessRight(user);
			Map<Integer, Integer> product2MAFlag = new HashMap<Integer, Integer>();
			for (UgMAccessRight uar : uGMAccessRight) 
				product2MAFlag.put(uar.getProductCodeId(), uar.getAccessRightFlag());

			Collection<UgMFAccessRight> uGMFAccessRight = uGMFAccessRightDao.getUGMFAccessRight(user);
			Map<Integer, Integer> module2MFAFlag = new HashMap<Integer, Integer>();
			for (UgMFAccessRight umfar : uGMFAccessRight)
				module2MFAFlag.put(umfar.getModuleCodeId(), umfar.getAccessRightFlag());

			AccessRight acessRight = new AccessRight(cp.getCode(), product2MAFlag, module2MFAFlag);
			user.setAccessRight(acessRight);
		}
		return user;
	}

	public int getUserIdByUsername(String userName) {
		return userDao.getUserIdbyUserName(userName);
	}

	public void saveUser(User user, User loggedInUser) throws NoSuchAlgorithmException{
		boolean isNew = user.getId() == 0;
		AuditUtil.addAudit(user, new Audit(loggedInUser.getId(), isNew, new Date()));
		int ebObjectId = 0;
		user.setEbObjectId(ebObjectId);
		if(isNew){
			user.setPassword(SimpleCryptoUtil.convertToSHA1(user.getPassword()));
			// Company Id and Service Lease key to 1 for now.
			user.setCompanyId(1);
			user.setServiceLeaseKeyId(1);
			EBObject eb = new EBObject();
			AuditUtil.addAudit(eb, new Audit(loggedInUser.getId(), true, new Date()));
			eb.setObjectTypeId(User.USER_OBJECT_TYPE_ID);
			ebObjectDao.save(eb);
			user.setEbObjectId(eb.getId());
			userDao.saveOrUpdate(user);

			UserLoginStatus userLoginStatus = new UserLoginStatus();
			userLoginStatus.setUserId(user.getId());
			userLoginStatus.setSuccessfulLoginAttempt(0);
			userLoginStatus.setFailedLoginAttempt(0);
			userLoginStatus.setBlockUser(false);
			loginStatusService.save(userLoginStatus);
		} else {
			User oldUser = getUser(user.getId());
			user.setEbObjectId(oldUser.getEbObjectId());
			if(!user.getPassword().equals(oldUser.getPassword())){
				user.setPassword(SimpleCryptoUtil.convertToSHA1(user.getPassword()));
			}
			UserLoginStatus currentLoginStatus = loginStatusService.getUserLoginStatus(user.getId());
			currentLoginStatus.setBlockUser(user.getUserLoginStatus().isBlockUser());
			loginStatusService.save(currentLoginStatus);

			// Company Id and Service Lease key to 1 for now.
			user.setCompanyId(1);
			user.setServiceLeaseKeyId(1);
			user.setUserCompanies(getUserCompaniesPerUser(user));
			userDao.saveOrUpdate(user);
		}
	}

	/**
	 * Search/filter cost center.
	 * @param userName The username of the user.
	 * @param firstName The first name of the user.
	 * @param lastName The last name of the user.
	 * @param userGroupId The user group id of the user.
	 * @param positionId The position id of the user.
	 * @param status True if active, otherwise false.
	 * @param pageNumber The page number.
	 * @return The paged result of the filtered user.
	 */
	public Page<User> searchUser(String userName, String firstName,String lastName, Integer userGroupId,
			Integer positionId, Integer loginStatus, Integer status, Integer pageNumber){
		Page<User> pUsers = userDao.searchUser(userName, firstName, lastName, userGroupId, positionId, loginStatus, status, new PageSetting(pageNumber, 25));
		for (User user : pUsers.getData()) {
			user.setUserLoginStatus(loginStatusService.getUserLoginStatus(user.getId()));
		}
		return pUsers;
	}

	/**
	 * Get List of users.
	 * @return The list of users.
	 */
	public List<User> getAllUsers(){
		return userDao.getAllUsers();
	}

	/**
	 * Get the list of users by position.
	 * @param positionId The position id.
	 * @return The list of users.
	 */
	public List<User> getUsersByPosition (int positionId, Integer companyId) {
		return userDao.getUsersByPosition(positionId, companyId);
	}

	/**
	 * Get the list of users by name.
	 * @param name The name of user.
	 * @return The list of users.
	 */
	public List<User> getUsersByName (String name) {
		return userDao.getUsersByName(name);
	}

	/**
	 * Get the paged list of users that are existing in the user companies.
	 * @param userName The name of a user
	 * @param companyName The name of a company
	 * @param status The status of company if active or inactive
	 * @param pageSetting
	 * @return The list of the companies.
	 */
	public Page<User> getUsersWithCompanies (String userName,String companyName, String status, Integer pageNumber) {
		SearchStatus searchStatus = SearchStatus.getInstanceOf(status);
		PageSetting pageSetting = new PageSetting(pageNumber, PageSetting.MAX_ADMIN_RECORD);
		Page<User> users = userDao.getUsersWithCompanies(userName, companyName, searchStatus, pageSetting);
		if (users.getDataSize() > 0) {
			for (User user : users.getData()) {
				user.setUserCompanies(userCompanyDao.getUCByUserAndCompany(user.getId(), companyName));
			}
		}
		return users;
	}
	

	/**
	 * Get the list of users by name.
	 * @param userName The name of a user.
	 * @param isActiveOnly Check if the user is active.
	 * @param limit The limit of number of users.
	 * @param userId The user id.
	 * @return The list of the users
	 */
	public List<User> getUsersByUsername (String userName, boolean isActiveOnly, Integer limit, Integer userId) {
		return userDao.getUsersByUsername(userName, isActiveOnly, limit, userId);
	}

	/**
	 * Get the list user company.
	 * @param user The user.
	 * @return The list user company.
	 */
	public List<UserCompany> getUserCompaniesPerUser(User user){
		List<UserCompany> userCompanies = userCompanyDao.getUserCompanies(user.getId());
		return userCompanies;
	}

	/**
	 * Handle the checking of user credential.
	 * @param request
	 * @param response
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public void processCredential(HttpServletRequest request, HttpServletResponse response) throws IOException, ClassNotFoundException {
		ObjectInputStream in = null;
		ObjectOutputStream oos = null;
		try {
			in = new ObjectInputStream(request.getInputStream());
			WebServiceCredential credential = (WebServiceCredential) in.readObject();
			// Check credential
			LoginCredential loginCredential = new LoginCredential();
			loginCredential.setUserName(credential.getUserName());
			loginCredential.setPassword(credential.getPassword());
			oos = new ObjectOutputStream(response.getOutputStream());
			if (!isUniqueUserName(credential.getUserName())) {
				if (credential.getPassword() == null || credential.getPassword().isEmpty() || !validateUser(loginCredential)) {
					int userId = getUserIdByUsername(credential.getUserName()); 
					UserLoginStatus userLoginStatus = loginStatusService.getUserLoginStatus(userId);
					if (userLoginStatus.getFailedLoginAttempt() < 3) {
						userLoginStatus.setFailedLoginAttempt(userLoginStatus.getFailedLoginAttempt() + 1);
						loginStatusService.save(userLoginStatus);
						oos.writeObject("Invalid credential");
						return;
					} else {
						userLoginStatus.setBlockUser(true);
						loginStatusService.save(userLoginStatus);
						oos.writeObject("Your account has been blocked for exceeding 3 failed login attempts. Contact the administrator to unblock your account.");
						return;
					}
				}
			} else {
				oos.writeObject("Invalid credential");
				return;
			}
			int userId = getUserIdByUsername(credential.getUserName()); 
			UserLoginStatus userLoginStatus = loginStatusService.getUserLoginStatus(userId);

			if (userLoginStatus.isBlockUser()) {
				oos.writeObject("Your account has been blocked for exceeding 3 failed login attempts. Contact the administrator to unblock your account.");
				return;
			} else {
				userLoginStatus.setSuccessfulLoginAttempt(userLoginStatus.getSuccessfulLoginAttempt() + 1);
				userLoginStatus.setFailedLoginAttempt(0);
				userLoginStatus.setLastLogin(new Date());
				loginStatusService.save(userLoginStatus);
				oos.writeObject("Successfully Login");
				return;
			}
		} finally {
			if (in != null)
				in.close();
			if (oos != null)
				oos.close();
		}
	}

	/**
	 * Get the user by username and password.
	 * @param userName The username.
	 * @param pasword The password.
	 * @return The user.
	 */
	public User getUser(String userName, String pasword){
		return userDao.getUser(userName, pasword);
	}

	/**
	 * Get the User By name.
	 * @param name The name of the user.
	 * @return The user.
	 */
	public User getUserByName(String name) {
		User user = userDao.getUserByName(name);
		if (user != null) {
			user.setUserCompanies(userCompanyDao.getUserCompanies(user.getId()));
		}
		return user;
	}

	/**
	 * Check if the user has a unique first and last name
	 * @param firstName The user's first name
	 * @param lastName The user's last name
	 * @param userId The user id
	 * @return True if the name is unique, otherwise false
	 */
	public boolean isUniqueName(String firstName, String lastName, Integer userId) {
		return userDao.isUniqueName(firstName, lastName, userId);
	}
}