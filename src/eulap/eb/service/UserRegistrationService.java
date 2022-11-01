package eulap.eb.service;

import java.security.NoSuchAlgorithmException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.util.NumberFormatUtil;
import eulap.eb.dao.CompanyDao;
import eulap.eb.dao.CompanyProductDao;
import eulap.eb.dao.KeyCodeDao;
import eulap.eb.dao.UserDao;
import eulap.eb.dao.impl.CompanyProductField;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.CompanyProduct;
import eulap.eb.domain.hibernate.KeyCode;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.UserCompany;
import eulap.eb.domain.hibernate.UserCompanyHead;
import eulap.eb.web.dto.UserRegistration;

/**
 * Business logic for new account registration for user.

 *
 */
@Service
public class UserRegistrationService {
	private static final Logger logger = Logger.getLogger(UserRegistrationService.class);
	@Autowired
	private KeyCodeDao keyCodeDao;
	@Autowired
	private CompanyDao companyDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private UserService userService;
	@Autowired
	private CompanyProductDao companyProductDao;

	private final int ONLINE_USER_ID = 2;
	private final int EB_SL_KEY_ID = 1;

	/**
	 * Save the registration form for the new user.
	 */
	public void saveRegistrationForm(UserRegistration registrationForm) throws NoSuchAlgorithmException {
		Date currentDate = new Date();

		Company company = registrationForm.getCompany();
		int companyNumber = companyDao.generateCompanyNumber();
		company.setCompanyNumber(NumberFormatUtil.formatNumber(companyNumber, 5));
		company.setAddress(company.getAddress().trim());
		company.setName(company.getName().trim());
		company.setCreatedDate(currentDate);
		company.setUpdatedDate(currentDate);
		company.setServiceLeaseKeyId(EB_SL_KEY_ID);
		company.setActive(true);
		companyDao.save(company);
		logger.info("Successfully saved company with name: "+company.getName());

		User user = registrationForm.getUser();
		User admin = userDao.get(1);
		user.setPositionId(ONLINE_USER_ID);
		user.setUserGroupId(ONLINE_USER_ID);
		user.setServiceLeaseKeyId(EB_SL_KEY_ID);
		user.setCompanyId(company.getId());
		user.setUsername(user.getUsername().trim());
		user.setFirstName(user.getFirstName().trim());
		user.setMiddleName(user.getMiddleName().trim());
		user.setAddress(user.getAddress().trim());
		user.setActive(true);
		userService.saveUser(user, admin);
		logger.info("Successfully saved user with username: " + user.getUsername());

		//Save User Company
		UserCompanyHead ucHead = new UserCompanyHead();
		ucHead.setActive(true);
		AuditUtil.addAudit(ucHead, new Audit(user.getId(), true, currentDate));
		companyDao.save(ucHead);
		logger.info("Successfully saved User Company Head");

		UserCompany userCompany = new UserCompany();
		userCompany.setActive(true);
		userCompany.setUserId(user.getId());
		userCompany.setCompanyId(company.getId());
		userCompany.setUserCompanyHeadId(ucHead.getId());
		AuditUtil.addAudit(userCompany, new Audit(user.getId(), true, currentDate));
		companyDao.save(userCompany);
		logger.info("Successfully saved User Company");

		//Create Company Product
		CompanyProduct companyProduct = new CompanyProduct();
		int code = companyProductDao.generateSequenceNumber(CompanyProductField.code.name());
		companyProduct.setCode(code);
		companyProduct.setCompanyId(company.getId());
		AuditUtil.addAudit(companyProduct, new Audit(user.getId(), true, currentDate));
		companyProductDao.save(companyProduct);

		//Update Key Code
		KeyCode keyCode = keyCodeDao.getKeyCode(registrationForm.getKeyCode());
		AuditUtil.addAudit(keyCode, new Audit(user.getId(), false, currentDate));
		keyCode.setUsed(true);
		keyCode.setUserId(user.getId());
		keyCodeDao.update(keyCode);
		logger.info("Successfully updated Keycode");

		updateCreatedUpdatedBy(company.getId(), user.getId());
		logger.info("Successfully saved Registration form for user "+user.getUsername());
	}

	/**
	 * Updated createdBy and updatedBy fields for {@link Company} and {@link User}.
	 * @param companyId The id of the company to be updated.
	 * @param userId The id of the user to be updated.
	 */
	private void updateCreatedUpdatedBy(int companyId, int userId) {
		Company company = companyDao.get(companyId);
		company.setCreatedBy(userId);
		company.setUpdatedBy(userId);
		companyDao.update(company);
		logger.info("Successfully updated company for created and updated by.");

		User user = userDao.get(userId);
		user.setCompanyId(companyId);
		user.setCreatedBy(userId);
		user.setUpdatedBy(userId);
		userDao.update(user);
		logger.info("Successfully updated user for created and updated by.");
	}
}
