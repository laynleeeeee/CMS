package eulap.eb.service;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.SalesPersonnelDao;
import eulap.eb.domain.hibernate.SalesPersonnel;
import eulap.eb.domain.hibernate.User;
import eulap.eb.validator.ValidatorMessages;
import eulap.eb.validator.ValidatorUtil;

/**
 * Service class that will handle business logic for {@link SalesPersonnel}

 */

@Service
public class SalesPersonnelService {
	private static final Logger logger = Logger.getLogger(SalesPersonnelService.class);
	@Autowired
	private SalesPersonnelDao salesPersonnelDao;
	@Autowired
	private CompanyService companyService;

	/**
	 * Get the {@link SalesPersonnel} object by id.
	 * @param salesPersonnelId The sales personnel id.
	 * @return The {@link SalesPersonnel} object.
	 */
	public SalesPersonnel getSalesPersonnel(Integer salesPersonnelId) {
		return salesPersonnelDao.get(salesPersonnelId);
	}

	/**
	 * Save {@link SalesPersonnel}.
	 * @param user The logged-in{@link User}
	 * @param salesPersonnel The {@link SalesPersonnel} object.
	 */
	public void saveSalesPersonnel(User user, SalesPersonnel salesPersonnel) {
		logger.debug("Saving the sales personnel.");
		boolean isNewRecord = salesPersonnel.getId() == 0 ? true : false;
		AuditUtil.addAudit(salesPersonnel, new Audit(user.getId(), isNewRecord, new Date()));
		salesPersonnel.setName(StringFormatUtil.removeExtraWhiteSpaces(salesPersonnel.getName()).trim());
		salesPersonnel.setContactNumber(StringFormatUtil.removeExtraWhiteSpaces(salesPersonnel.getContactNumber()).trim());
		salesPersonnel.setAddress(StringFormatUtil.removeExtraWhiteSpaces(salesPersonnel.getAddress()).trim());
		salesPersonnelDao.saveOrUpdate(salesPersonnel);
		logger.info("Successfully saved the sales personnel.");
	}

	/**
	 * Check for incorrect data for {@link SalesPersonnel}.
	 * @param salesPersonnel The {@link SalesPersonnel}.
	 * @param errors The {@link Error}.
	 */
	public void validateForm(SalesPersonnel salesPersonnel, Errors errors) {
		ValidatorUtil.validateCompany(salesPersonnel.getCompanyId(), companyService, errors, "companyId");

		if(salesPersonnel.getName() == null || salesPersonnel.getName().trim().isEmpty()) {
			errors.rejectValue("name", null, null, ValidatorMessages.getString("SalesPersonnelService.0"));
		} else if(salesPersonnel.getName().length() > SalesPersonnel.MAX_CHARACTER_NAME) {
			errors.rejectValue("name", null, null,
				String.format(ValidatorMessages.getString("SalesPersonnelService.4"),
						SalesPersonnel.MAX_CHARACTER_NAME));
		} else if(!salesPersonnelDao.isUnique(salesPersonnel)) {
			errors.rejectValue("name", null, null, ValidatorMessages.getString("SalesPersonnelService.3"));
		}

		if(salesPersonnel.getContactNumber() == null || salesPersonnel.getContactNumber().trim().isEmpty()){
			errors.rejectValue("contactNumber", null, null, ValidatorMessages.getString("SalesPersonnelService.1"));
		} else if(salesPersonnel.getContactNumber().length() > SalesPersonnel.MAX_CHARACTER_CONTACT_NUMBER) {
			errors.rejectValue("contactNumber", null, null,
				String.format(ValidatorMessages.getString("SalesPersonnelService.5"),
						SalesPersonnel.MAX_CHARACTER_CONTACT_NUMBER));
		}

		if(salesPersonnel.getAddress() == null || salesPersonnel.getAddress().trim().isEmpty()){
			errors.rejectValue("address", null, null, ValidatorMessages.getString("SalesPersonnelService.2"));
		}
	}

	/**
	 * Get the list of {@link SalesPersonnel} in {@link Page} format.
	 * @param companyId The company.
	 * @param name The sales personnel name.
	 * @param status The sales personnel status.
	 * @param pageNumber The pagenumber.
	 * @return The list of {@link SalesPersonnel} in {@link Page} format.
	 */
	public Page<SalesPersonnel> searchSalesPersonnel(Integer companyId, String name, String status, Integer pageNumber) {
		SearchStatus searchStatus = SearchStatus.getInstanceOf(status);
		return salesPersonnelDao.searchSalesPersonnel(companyId, name, searchStatus, new PageSetting(pageNumber, PageSetting.MAX_ADMIN_RECORD));
	}

	/**
	 * Get the list of {@link SalesPersonnel} by name.
	 * @param companyId The company id.
	 * @param name The sales personnel name.
	 * @param isExact True if exact name, otherwise false.
	 * @return The list of {@link SalesPersonnel}.
	 */
	public List<SalesPersonnel> getSalesPersonnelByName(Integer companyId, String name, Boolean isExact) {
		return salesPersonnelDao.getSalesPersonnelByName(companyId, name, isExact);
	}
}
