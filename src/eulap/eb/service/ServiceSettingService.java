package eulap.eb.service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.dao.AccountCombinationDao;
import eulap.eb.dao.AccountDao;
import eulap.eb.dao.CompanyDao;
import eulap.eb.dao.DivisionDao;
import eulap.eb.dao.ServiceSettingDao;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.ServiceSetting;
import eulap.eb.domain.hibernate.User;
import eulap.eb.validator.ValidatorMessages;

/**
 * Service class that will handle all the business logic for Service settings.

 *
 */
@Service
public class ServiceSettingService {
	@Autowired
	private AccountCombinationDao acctCombiDao;
	@Autowired
	private CompanyDao companyDao;
	@Autowired
	private DivisionDao divisionDao;
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private ServiceSettingDao serviceSettingDao;

	/**
	 * Get {@link ServiceSetting} object.
	 * @param serviceSettingId The service setting id.
	 * @return The {@link ServiceSetting} object.
	 */
	public ServiceSetting getServiceSetting(Integer serviceSettingId) {
		return serviceSettingDao.get(serviceSettingId);
	}
	/**
	 * Save {@link ServiceSetting}.
	 * @param user The {@link User}.
	 * @param service The {@link ServiceSetting}.
	 */
	public void save(User user, ServiceSetting service) {
		boolean isNew = service.getId() == 0;
		AuditUtil.addAudit(service, new Audit(user.getId(), isNew, new Date()));
		service.setAccountCombinationId(getAcctCombi(service).getId());
		service.setName(service.getName().trim());
		serviceSettingDao.saveOrUpdate(service);
	}

	/**
	 * Validate {@link ServiceSetting} data.
	 * @param service The {@link ServiceSetting}.
	 * @param errors The {@link Errors}.
	 */
	public void validate(ServiceSetting service, Errors errors) {
		if(service.getName() == null || service.getName().isEmpty()) {
			//Name is required.
			errors.rejectValue("name", null, null, ValidatorMessages.getString("ServiceSettingsService.0"));
		} else if(service.getName().length() > ServiceSetting.MAX_NAME_CHAR) {
			//Name should not exceed %d characters.
			errors.rejectValue("name", null, null, String.format(ValidatorMessages.getString("ServiceSettingsService.1"),
							ServiceSetting.MAX_NAME_CHAR));
		} else if(!serviceSettingDao.isUniqueService(service)) {
			errors.rejectValue("name", null, null, ValidatorMessages.getString("ServiceSettingsService.2"));
		}

		AccountCombination acctCombi = getAcctCombi(service);
		if(acctCombi == null) {
			//Account combination is required.
			errors.rejectValue("accountCombinationId", null, null, ValidatorMessages.getString("ServiceSettingsService.3"));
		} else {
			if(!acctCombi.isActive()) {
				//Account combination is inactive.
				errors.rejectValue("accountCombinationId", null, null, ValidatorMessages.getString("ServiceSettingsService.4"));
			}
			if(!companyDao.get(acctCombi.getCompanyId()).isActive()) {
				//Company is inactive.
				errors.rejectValue("companyId", null, null, ValidatorMessages.getString("ServiceSettingsService.5"));
			}
			if(!divisionDao.get(acctCombi.getDivisionId()).isActive()) {
				//Division is inactive.
				errors.rejectValue("divisionId", null, null, ValidatorMessages.getString("ServiceSettingsService.6"));
			}
			if(!accountDao.get(acctCombi.getAccountId()).isActive()) {
				//Account is inactive.
				errors.rejectValue("accountId", null, null, ValidatorMessages.getString("ServiceSettingsService.7"));
			}
		}
	}

	/**
	 * Get the {@link AccountCombination} object.
	 * @param service The {@link ServiceSetting} object/
	 * @return The {@link AccountCombination} object.
	 */
	public AccountCombination getAcctCombi(ServiceSetting service) {
		if(service.getCompanyId() != null && service.getDivisionId() != null && service.getAccountId() != null) {
			AccountCombination acctCombi = acctCombiDao.getAccountCombination(service.getCompanyId(),
					service.getDivisionId(), service.getAccountId());
			return acctCombi;
		}
		return null;
	}

	/**
	 * Get the list of {@link ServiceSetting} in page format.
	 * @param name The service setting name.
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @param status The status.
	 * @param pageNumber The page number.
	 * @return The list of {@link ServiceSetting} in page format.
	 */
	public Page<ServiceSetting> searchServiceSettings(String name, Integer companyId, Integer divisionId,
				String status,Integer pageNumber) {
		SearchStatus searchStatus = SearchStatus.getInstanceOf(status);
		return serviceSettingDao.searchServiceSettings(name, companyId, 
				divisionId, searchStatus, new PageSetting(pageNumber, PageSetting.MAX_ADMIN_RECORD));
	}

	/**
	 * Get the list of service setups based on its name.
	 * @param name The name of the service setup.
	 * @param arCustAcctId The unique id of Ar Customer Account.
	 * @param id The unique id of service Setup.
	 * @param noLimit True if display all unit of measurements.
	 * @param isExact True if the name must be exact.
	 * @param divisionId The division id
	 * @return The list of service setups.
	 */
	public List<ServiceSetting> getServiceSettings (String name, Integer arCustAcctId, Integer id, Boolean noLimit,
			Boolean isExact, Integer divisionId) {
		List<ServiceSetting> services = serviceSettingDao.getServiceSettings(name, arCustAcctId, id, noLimit,
				isExact, divisionId);
		if (services == null || services.isEmpty()) {
			return Collections.emptyList();
		}
		return services;
	}

	/**
	 * Get the {@link ServiceSetting} by name.
	 * @param serviceSettingName The service setting name.
	 * @return The {@link ServiceSetting}
	 */
	public ServiceSetting getServiceSettingByName(String serviceSettingName) {
		return serviceSettingDao.getServiceSettingByName(serviceSettingName);
	}

	/**
	 * Get the {@link ServiceSetting} by division.
	 * @param name The name of the service settings.
	 * @param noLimit True to display all service settings, otherwise show 10 result.
	 * @param isExact True to search for exact name (Disregard wild card search) in the service setting table.
	 * @param divisionId the division id.
	 * @param companyId the company id.
	 * @return The {@link ServiceSetting}
	 */
	public List<ServiceSetting> getServiceSettingByDivision(String name, Boolean noLimit, Boolean isExact, Integer divisionId, Integer companyId){
		return serviceSettingDao.getServiceSettingByDivision(name,noLimit, isExact, divisionId,companyId);
	}
}
