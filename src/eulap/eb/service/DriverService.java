package eulap.eb.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.dao.DriverDao;
import eulap.eb.domain.hibernate.Driver;
import eulap.eb.domain.hibernate.User;
import eulap.eb.validator.ValidatorMessages;

/**
 * Class that will handle business logic for {@link Driver}

 *
 */
@Service
public class DriverService {
	@Autowired
	private DriverDao driverDao;

	/**
	 * Saving method for driver.
	 * @param driver The driver object.
	 * @param user The user currently logged in.
	 * @throws IOException 
	 */
	public void saveDriver(Driver driver, User user, BindingResult result) throws IOException {
		Integer userId = user.getId();
		Date currDate = new Date();
		boolean isNew = driver.getId() == 0;
		AuditUtil.addAudit(driver, new Audit(userId, isNew, currDate));
		driverDao.saveOrUpdate(driver);
	}

	/**
	 * Validation method for Driver.
	 * @param driver The driver object.
	 * @param errors The form errors.
	 */
	public void validateDriver(Driver driver, Errors errors) {
		if(driver.getCompanyId() == null) {
			//Company is required.
			errors.rejectValue("companyId", null, null, ValidatorMessages.getString("DriverService.0"));
		}
		if(driver.getFirstName() == null || driver.getFirstName().trim().isEmpty()) {
			//First name is required.
			errors.rejectValue("firstName", null, null, ValidatorMessages.getString("DriverService.1"));
		} else if(driver.getFirstName().length() > Driver.MAX_FIRST_NAME) {
			//First name must not exceed %d characters.
			errors.rejectValue("firstName", null, null, String.format(ValidatorMessages.getString("DriverService.2"),
					Driver.MAX_FIRST_NAME));
		}
		if(driver.getMiddleName() != null && !driver.getMiddleName().trim().isEmpty() 
				&& driver.getMiddleName().length() > Driver.MAX_MIDDLE_NAME) {
			//Middle name must not exceed %d characters.
			errors.rejectValue("middleName", null, null, String.format(ValidatorMessages.getString("DriverService.3"),
					Driver.MAX_MIDDLE_NAME));
		}
		if(driver.getLastName() == null || driver.getLastName().trim().isEmpty()) {
			//Last name is required.
			errors.rejectValue("lastName", null, null, ValidatorMessages.getString("DriverService.4"));
		} else if(driver.getLastName().length() > Driver.MAX_LAST_NAME) {
			//Last name must not exceed %d characters.
			errors.rejectValue("lastName", null, null, String.format(ValidatorMessages.getString("DriverService.5"),
					Driver.MAX_LAST_NAME));
		}
		if(driver.getGenderId() == null) {
			//Gender is required.
			errors.rejectValue("genderId", null, null, ValidatorMessages.getString("DriverService.6"));
		}
		if(driver.getBirthDate() == null) {
			//Birthdate is required.
			errors.rejectValue("birthDate", null, null, ValidatorMessages.getString("DriverService.7"));
		}
		if(driver.getCivilStatusId() == null) {
			//Civil status is required.
			errors.rejectValue("civilStatusId", null, null, ValidatorMessages.getString("DriverService.8"));
		}
		if(driver.getAddress() == null || driver.getAddress().trim().isEmpty()) {
			//Address is required.
			errors.rejectValue("address", null, null, ValidatorMessages.getString("DriverService.9"));
		} else if(driver.getAddress().length() > Driver.MAX_ADDRESS) {
			//Address must not exceed %d characters.
			errors.rejectValue("address", null, null, String.format(ValidatorMessages.getString("DriverService.10"),
					Driver.MAX_ADDRESS));
		}
		if(driver.getContactNo() != null && !driver.getContactNo().trim().isEmpty()
				&& driver.getContactNo().length() > Driver.MAX_CONTACT_NO) {
			//Contact number must not exceed %d characters.
			errors.rejectValue("contactNo", null, null, String.format(ValidatorMessages.getString("DriverService.11"),
					Driver.MAX_CONTACT_NO));
		}

		if((driver.getFirstName() != null && !driver.getFirstName().trim().isEmpty()) 
				&& (driver.getLastName() != null && !driver.getLastName().trim().isEmpty())
				&& (!driverDao.isUniqueName(driver.getId(), driver.getCompanyId(),
						driver.getFirstName(), driver.getMiddleName(), driver.getLastName()))) {
			errors.rejectValue("lastName", null, null, ValidatorMessages.getString("DriverService.12"));
		}
	}

	/**
	 * Search the drivers with the specified search criteria.
	 * @param name The driver name.
	 * @param companyId The company id.
	 * @param status The driver status.
	 * @param pageNumber The page number.
	 * @return The paged list of drivers.
	 */
	public Page<Driver> searchDrivers(String name, Integer companyId, String status, Integer pageNumber) {
		SearchStatus searchStatus = status != null ? SearchStatus.getInstanceOf(status) : null;
		return driverDao.searchDrivers(name, companyId, searchStatus, new PageSetting(pageNumber, PageSetting.MAX_ADMIN_RECORD));
	}

	/**
	 * Get the driver object.
	 * @param driverId The driver id.
	 * @return The driver object.
	 */
	public Driver getDriver(Integer driverId) {
		return driverDao.get(driverId);
	}

	/**
	 * Get the list of drivers base on company and the driver name.
	 * @param name The driver name.
	 * @param companyId The company id.
	 * @return List of {@link Driver}.
	 */
	public List<Driver> getDriversByName(Integer companyId, String name) {
		return driverDao.getDriversByName(companyId, name);
	}
}
