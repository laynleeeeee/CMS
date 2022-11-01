package eulap.eb.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.DivisionProjectDao;
import eulap.eb.domain.hibernate.ArCustomer;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.DivisionProject;
import eulap.eb.domain.hibernate.User;

/**
 * Service class that will handle business logic for {@link DivisionProject}

 */

@Service
public class DivisionProjectService  {
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private DivisionProjectDao divisionProjectDao;
	@Autowired
	private FleetProfileService fleetProfileService;
	@Autowired
	private AccountCombinationService accountCombinationService;

	/**
	 * Save the {@code DivisionProject}
	 * @param user The logged user.
	 * @param dv The domain to be saved.
	 */
	public void saveProjectDivision(User user, ArCustomer customer) {
		Integer customerId = customer.getId();
		int userId = user.getId();
		Date currentDate = new Date();
		if (!hasExistingDivisionProject(customerId)) {
			DivisionProject dv = new DivisionProject();
			Division division = getExistingDivision(user, customer);
			int divisionId = division.getId();
			dv.setDivisionId(divisionId);
			dv.setProjectId(customerId);
			AuditUtil.addAudit(dv, new Audit(userId, true, currentDate));
			divisionProjectDao.save(dv);

			// Save account combinations
			accountCombinationService.createAccountCombinations(divisionId, userId,
					user.getServiceLeaseKeyId(), currentDate);
		}
	}

	private Division getExistingDivision (User user, ArCustomer customer) {
		String[] names = customer.getName().split("-");
		List<Division> divisions = null;
		String divisionName = null;
		Division division = null;
		for (String name : names) {
			if (divisionName == null) {
				divisionName = name;
			} else {
				divisionName += "-"+name;
			}
			divisions = divisionService.getProjectDivisionsByName(divisionName.trim());
			int size = divisions.size();
			if (size > 1) {
				continue;
			} else if (size == 1) {
				division = divisions.get(0);
				break;
			} else {
				break;
			}
		}
		divisions = null;
		if (division == null) {
			return createDivision(customer, user);
		}
		return division;
	}

	private Division createDivision(ArCustomer arCustomer, User user) {
		String projectName = StringFormatUtil.removeExtraWhiteSpaces(arCustomer.getName());
		Division division = new Division();
		division.setActive(arCustomer.isActive());
		division.setName(projectName);
		division.setDescription(projectName);
		division.setNumber(fleetProfileService.generateDivNumber(user.getId()));
		divisionService.saveDivision(division, user);
		return division;
	}

	private boolean hasExistingDivisionProject(Integer customerId) {
		return divisionProjectDao.hasExistingDivProject(customerId);
	}

	/**
	 * Get the division-project object
	 * @param projectId The project id
	 * @return The division-project object
	 */
	public DivisionProject getDivisionProject(Integer projectId) {
		return divisionProjectDao.getDivisionProject(projectId);
	}

	/**
	 * Get the list of division with associated project.
	 * @param divisionName The division name.
	 * @param companyId The company id.
	 * @param isExact True if name to be evaluated is the exact value, otherwise false
	 * @return The list of divisions.
	 */
	public List<Division> getProjectDivisions(String divisionName, Integer companyId, boolean isExact) {
		return divisionService.getProjectDivisions(divisionName, companyId, isExact);
	}
}
