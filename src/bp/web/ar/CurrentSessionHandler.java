package bp.web.ar;

import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpSession;

import org.apache.commons.configuration2.ex.ConfigurationException;

import eulap.common.dto.LoginCredential;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.UserCompany;
import eulap.eb.domain.hibernate.UserGroup;
import eulap.eb.service.UserGroupAccessRightService;
import eulap.eb.service.UserService;

/**
 * Utility class that handles the information of the logged in user. 

 *
 */
public class CurrentSessionHandler {
	private static final String LOGIN_USER_KEY = "user";
	private static final String USER_COMPANY_NAME = "userCompanyName";
	private static final String SELECTED_COMPANY = "selectedcompany";
	/**
	 * Save the user in session.
	 * @param session The http current session.
	 * @param userService The user service object.
	 * @param credential The userCredential
	 */
	protected static void saveUser (HttpSession session, UserService userService, UserGroupAccessRightService ugAccessRightService,
			LoginCredential credential) {
		User user;
		try {
			user = userService.getUser(credential);
			UserGroup userGroup = user.getUserGroup();
			userGroup.setHasAdminAccess(ugAccessRightService.hasAdminModule(userGroup.getId()));
			user.setUserGroup(userGroup);
			session.setAttribute(LOGIN_USER_KEY, user);
			session.setAttribute(USER_COMPANY_NAME, user.getCompany().getName());
			session.setAttribute(SELECTED_COMPANY, -1);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new RuntimeException("Invalid algorithmException", e);
		} catch (ConfigurationException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * Clear the logged user information in the http session.
	 * @param session The current http session.
	 */
	protected static void clearLoggedUser (HttpSession session) {
		session.removeAttribute(USER_COMPANY_NAME);
		session.removeAttribute(LOGIN_USER_KEY);
	}

	/**
	 * Validate if the user has access to a company. This will throw an exception if the user has now access to passed company. 
	 * @param user The logged in user. 
	 * @param companyId The company id
	 */
	public static void validateUserCompanyAccessRight (User user, int companyId) {
		for (UserCompany us : user.getUserCompanies()) {
			if (us.getCompanyId() == companyId)
				return;
		}
		throw new SecurityException("You don't have access to this report, please contact administrator");
	}
	/**
	 * Get the user in the session.
	 * @param session The current http session.
	 * @return The logged in user.
	 */
	public static User getLoggedInUser (HttpSession session) {
		return (User) session.getAttribute(LOGIN_USER_KEY);
	}
	
	public static int getSelectedCompany (HttpSession session) {
		return (Integer) session.getAttribute(SELECTED_COMPANY);
	}

	/**
	 * Check current user if admin.
	 * @param session The current user session.
	 * @return True if user is admin, otherwise false.
	 */
	public static boolean isAdmin (HttpSession session) {
		User user = getLoggedInUser(session);
		boolean isAdmin = user.isAdmin();
		if(isAdmin)
			return true;
		return false;
	}
}
