package eulap.eb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.eb.dao.UserLoginStatusDao;
import eulap.eb.domain.hibernate.UserLoginStatus;

/**
 * The user login status service that handle the business layer of the  user login status object.

 *
 */
@Service
public class UserLoginStatusService {
	@Autowired
	private UserLoginStatusDao userLoginStatusDao;
	
	public void setuserLoginStatusDao(UserLoginStatusDao userLoginStatusDao) {
		this.userLoginStatusDao = userLoginStatusDao;
	}
	
	public void save(UserLoginStatus userLoginStatus) {
		userLoginStatusDao.saveOrUpdate(userLoginStatus);
	}
	
	public boolean isExisting(int userId) {
		return userLoginStatusDao.isExisting(userId);
	}
	
	public int getFailedLoginAttempts(int userId) {
		return userLoginStatusDao.getFailedLoginAttempts(userId);
	}
	
	public UserLoginStatus getUserLoginStatus (int userId) {
		UserLoginStatus loginStatus = userLoginStatusDao.getUserLoginStatus(userId);
		if (loginStatus != null)
			return loginStatus;
		loginStatus = new UserLoginStatus();
		loginStatus.setUserId(userId);
		return loginStatus;
	}
}
