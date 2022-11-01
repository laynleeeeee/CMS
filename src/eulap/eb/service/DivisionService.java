package eulap.eb.service;

import java.util.ArrayList;
import java.util.Collection;
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
import eulap.eb.dao.DivisionDao;
import eulap.eb.dao.EBObjectDao;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.DivisionDto;

/**
 * The business logic of division.

 */
@Service
public class DivisionService {
	@Autowired
	private DivisionDao divisionDao;
	@Autowired
	private EBObjectDao ebObjectDao;

	/**
	 * Get all the divisions.
	 * @return The collection of all divisions.
	 */
	public Page<Division> getAllDivisions(User user) {
		return divisionDao.getAllDivisions(user);
	}

	/**
	 * Search division.
	 * @param number The division number.
	 * @param name The division name.
	 * @param status The status.
	 * @param pageNumber The page number.
	 * @return The paged divisions.
	 */
	public Page<Division> searchDivisions(String number, String name, User user, String status, int pageNumber) {
		SearchStatus searchStatus = SearchStatus.getInstanceOf(status);
		return divisionDao.searchDivisions(number, name.trim(), user, searchStatus, new PageSetting(pageNumber));
	}

	/**
	 * Get the division object using the Id.
	 * @param divisionId The Id of the division.
	 * @param user The logged in user.
	 * @return The division object.
	 */
	public Division getDivisionById(int divisionId, User user) {
		return divisionDao.getDivisionById(divisionId, user);
	}

	/**
	 * Get the division object.
	 * @param divisionId The unique id of the division.
	 * @return The division.
	 */
	public Division getDivision(int divisionId) {
		return divisionDao.get(divisionId);
	}

	/**
	 * Save the division object in the database.
	 * @param division The division object.
	 */
	public void saveDivision(Division division, User user) {
		boolean isNewRecord = division.getId() == 0;
		if(isNewRecord){
			EBObject eb = new EBObject();
			AuditUtil.addAudit(eb, new Audit(user.getId(), true, new Date()));
			eb.setObjectTypeId(Division.OBJECT_TYPE_ID);
			ebObjectDao.save(eb);
			division.setEbObjectId(eb.getId());
		}
		AuditUtil.addAudit(division, new Audit(user.getId(), isNewRecord, new Date()));
		//Remove white spaces before saving.
		division.setName(division.getName().trim());
		division.setDescription(division.getDescription().trim());
		division.setServiceLeaseKeyId(user.getServiceLeaseKeyId());
		divisionDao.saveOrUpdate(division);
	}

	/**
	 * Evaluate the division field for duplications.
	 * @param division The division object.
	 * @param divisionField 1=number, 2=name.
	 * @param user The logged in user.
	 * @return True if unique, otherwise there is an already existing data.
	 */
	public boolean isUnique(Division division, int divisionField, User user) {
		if(division.getId() == 0)
			return divisionDao.isUniqueDivisionField(division, divisionField, user);
		Division existingDivision = divisionDao.get(division.getId());
		if(divisionField == 1) {
			if(division.getNumber().trim().equalsIgnoreCase(existingDivision.getNumber().trim()))
				return true;
		} else if(divisionField == 2) {
			if(division.getName().trim().equalsIgnoreCase(existingDivision.getName().trim()))
				return true;
		}
		return divisionDao.isUniqueDivisionField(division, divisionField, user);
	}

	/**
	 * Get the active divisions.
	 * @return Collection of active divisions.
	 */
	public Collection<Division> getActiveDivsions (User user, int divisionId) {
		Collection<Division> divisions = divisionDao.getActiveDivisions(user);
		return appendInactiveDiv(divisions, divisionId);
	}

	/**
	 * Get the division that was configured in the account combination.
	 * @param companyId The companyId.
	 * @return The list of divisions.
	 */
	public List<Division> getDivisions (int companyId) {
		return new ArrayList<Division>(divisionDao.getDivisionByAcctCombination(companyId));
	}

	/**
	 * Get the list of divisions that are existing in the account combination table.
	 * @param companyId The company id.
	 * @param accountId The accout id.
	 * @param selectedDivisionId The selected division id.
	 * @param isActiveOnly True if only the active companies are to be retrieved, 
	 * otherwise false.
	 * @param user The logged user.
	 * @return The divisions that are configured in the account combination table.
	 */
	public List<Division> getDivisions (int companyId, int accountId, Integer selectedDivisionId, boolean isActiveOnly, User user) {
		return new ArrayList<Division>(divisionDao.getDivisions(companyId, accountId, selectedDivisionId, isActiveOnly, user));
	}

	/**
	 * Get the division object by its number.
	 * @param divisionNumber The division number.
	 * @param user The logged user.
	 * @return Division object.
	 */
	public Division getDivisionByDivNumber (String divisionNumber, User user) {
		return divisionDao.getDivisionByDivNumber(divisionNumber, user.getServiceLeaseKeyId());
	}

	/**
	 * Get the division object by its name.
	 * @param name The unique name of the division.
	 * @return The division object, otherwise null.
	 */
	public Division getDivisionByName(String name, boolean isActiveOnly) {
		return getDivisionByName(name, isActiveOnly, false);
	}

	/**
	 * Get the division object by its name.
	 * @param name The unique name of the division.
	 * @param isActiveOnly True if retrieve active division only, otherwise all
	 * @param excludeDivWithAcctCombi True if exclude division that already
	 * been tagged to account combination, otherwise false
	 * @return The division object
	 */
	public Division getDivisionByName(String name, boolean isActiveOnly, boolean excludeDivWithAcctCombi) {
		return divisionDao.getDivisionByName(name, isActiveOnly, excludeDivWithAcctCombi);
	}

	/**
	 * Get the list of division.
	 * @param companyId The company id.
	 * @param divisionNumber The division number.
	 * @return The list of division.
	 */
	public List<Division> getDivisions(Integer companyId, String divisionNumber, boolean isActive, Integer limit) {
		return divisionDao.getDivisionByCompanyIdAndName(companyId, divisionNumber, isActive, limit);
	}

	/**
	 * Get the collection of active divisions and the selected division if inactive.
	 * @return The collection of divisions filtered by companyId.
	 */
	public Collection<Division> getDivisions(int companyId, int divisionId) {
		Collection<Division> divisions = divisionDao.getDivisionByAcctCombination(companyId);
		return appendInactiveDiv(divisions, divisionId);
	}

	/**
	 * Method to append or add inactive division in the list.
	 * @param divisions The list of divisions.
	 * @param divisionId The id of the division to be added.
	 * @return The list of divisions with the inactive division.
	 */
	private Collection<Division> appendInactiveDiv(Collection<Division> divisions, int divisionId) {
		if(divisionId != 0) {
			Collection<Integer> divisionIds = new ArrayList<Integer>();
			for(Division div : divisions) {
				divisionIds.add(div.getId());
			}
			if(!divisionIds.contains(divisionId))
				divisions.add(divisionDao.get(divisionId));
		}
		return divisions;
	}

	/**
	 * Get all active divisions and append inactives
	 * @param divisionId The division id
	 * @return The list of divisions
	 */
	public Collection<Division> getActiveDivisions(Integer divisionId) {
		Collection<Division> divisions = appendInactiveDiv(divisionDao.getActiveDivisions(), divisionId);
		return divisions;
	}

	/**
	 * Get the division object
	 * @param employeeId The employee id
	 * @return The division object by employee id
	 */
	public Division getEmployeeDivision(Integer employeeId) {
		return divisionDao.getDivisionByEmployeeId(employeeId);
	}

	/**
	 * Get the list of division/s based on either division number or name.
	 * @param numberOrName Parameter that represents either division number or division name.
	 * @param The id of the division to be excluded.
	 * @param True to filter only main level divisions, otherwise false.
	 * @return The list of divisions.
	 */
	public List<Division> getDivisions(String numberOrName, Integer divisionId,  boolean isMainLevelOnly) {
		return divisionDao.getDivisions(numberOrName, divisionId, isMainLevelOnly);
	}

	/**
	 * Search/filter the divisions by criteria.
	 * @param divNumber The division number.
	 * @param name The division name.
	 * @param user The logged in user.
	 * @param searchStatus The search status.
	 * @param isMainDivisionOnly True if to display only the main division.
	 * @param pageNumber The page number.
	 * @return Paged collection of filtered divisions by criteria.
	 */
	public Page<DivisionDto> searchDivisionWithSubs(String divNumber,
			String name, User user, String status, int pageNumber) {
		SearchStatus searchStatus = SearchStatus.getInstanceOf(status);
		boolean isMainDivisionOnly = divNumber.trim().isEmpty() && name.trim().isEmpty();
		return divisionDao.searchDivisionWithSubs(divNumber, name, user, searchStatus, isMainDivisionOnly, 
				new PageSetting(pageNumber, PageSetting.MAX_ADMIN_RECORD));
	}

	/**
	 * Get all the active children division of the specific division given by its id.
	 * @param divisionId The id of the division to be checked.
	 * @return The list of active children.
	 */
	public List<DivisionDto> getAllChildren(int divisionId) {
		return getAllChildren(divisionId, true);
	}

	/**
	 * Get all the children division of the specific division given by its id.
	 * @param divisionId The id of the division to be checked.
	 * @param isActive True if only active children will be retrieved, otherwise false.
	 * @return The list of children.
	 */
	public List<DivisionDto> getAllChildren(int divisionId, boolean isActive) {
		List<DivisionDto> children = divisionDao.getAllChildren(divisionId, isActive);
		addChildren(children, isActive);
		return children;
	}

	private void addChildren(List<DivisionDto> children, boolean isActive) {
		List<DivisionDto> tmpList = null;
		if (!children.isEmpty()) {
			for (DivisionDto l : children) {
				tmpList = divisionDao.getAllChildren(l.getId(), isActive);
				if (!tmpList.isEmpty()) {
					l.setChildrenDivision(tmpList);
					addChildren(tmpList, isActive);
				}
			}
		}
	}

	/**
	 * Get the list of divisions with the parent division.
	 * @param divisionId
	 * @param name
	 * @param isActive
	 * @return
	 */
	public List<DivisionDto> getAllChildrenAndParent(int divisionId, String name, boolean isActive, boolean isExact) {
		return divisionDao.getAllChildren(divisionId, name, isActive, isExact);
	}

	/**
	 * Get all last level accounts.
	 * Last level accounts refer to any account that has no child account.
	 * @return List of all last level accounts.
	 */
	public List<Division> getLastLevelDivisions(Integer divisionId) {
		List<Division> divisions = divisionDao.getLastLevelDivisions(false);
		return (List<Division>) appendInactiveDiv(divisions, divisionId);
	}

	/**
	 * Get the divisionj level based on the parent.
	 * @param parentDivisionId The parent divisionj id.
	 * @return The divisionj level.
	 */
	public int getDivisionLevel(Integer parentDivisionId, Integer divisionId) {
		int level = 1;
		if (parentDivisionId == null) {
			return level;
		}
		Division division = divisionDao.get(parentDivisionId);
		if (division != null) {
			level += getDivisionLevel(division.getParentDivisionId(), null);
			level += countChildLevels(divisionId);
		}
		return level;
	}

	/**
	 * Counts how many levels below is the lowest level child of the selected division if there are child divisions.
	 * @param divisionId The division id.
	 * @return The number of levels below the lowest level child division is from the selected division.
	 */
	public int countChildLevels(Integer divisionId) {
		int divisionLvl = 1;
		if(divisionId != null) {
			List<Division> childAccnts = divisionDao.getAllByRefId(Division.Field.parentDivisionId.name(), divisionId);
			if(childAccnts != null && !childAccnts.isEmpty()) {
				for(Division childAccnt : childAccnts) {
					divisionLvl += countChildLevels(childAccnt.getId());
					// End loop if there is a max level division found.
					if(divisionLvl == Division.MAX_LEVEL) {
						break;
					}
				}
			} else {
				return 0;
			}
		} else {
			return 0;
		}
		return divisionLvl;
	}

	/**
	 * Get the list of divisions by account combination and level.
	 * @param companyId The company id.
	 * @param level The level.
	 * @return List of divisions.
	 */
	public List<DivisionDto> getByCombination(Integer companyId, Integer level) {
		if (level == DivisionDto.MAIN_LEVEL) {
			return divisionDao.getByCombination(companyId);
		}
		List<DivisionDto> divs = divisionDao.getByCombination(companyId);
		if (!divs.isEmpty()) {
			for (DivisionDto dto : divs) {
				if (dto.getPdId().intValue() != 0) {
					dto.setParentDivision(domain2Dto(divisionDao.get(dto.getPdId())));
				}
			}
		}
		return divs;
	}

	/**
	 * Get the list of division by account combination company
	 * @param companyId The company id
	 * @param isMainLevelOnly
	 * @return The list of divisions
	 */
	public List<DivisionDto> getDivisionsByCombination(Integer companyId, boolean isMainLevelOnly) {
		List<DivisionDto> ret = new ArrayList<DivisionDto>();
		List<DivisionDto> divs = divisionDao.getByCombination(companyId);
		if (!divs.isEmpty()) {
			if(isMainLevelOnly) {
				Map<Integer, DivisionDto> hmDivisionDto = new HashMap<Integer, DivisionDto>();
				for (DivisionDto dto : divs) {
					if(dto.getPdId() != 0) {
						DivisionDto toBeAdded = domain2Dto(divisionDao.get(dto.getPdId()));
						if(hmDivisionDto.containsKey(toBeAdded.getId())) {
							continue;
						}
						ret.add(toBeAdded);
						hmDivisionDto.put(toBeAdded.getId(), toBeAdded);
					} else {
						ret.add(dto);
					}
					dto = null; // free up memory
				}
				hmDivisionDto = null;
			} else {
				return divs;
			}
		} 
		return ret;
	}

	/**
	 * Convert the domain division to dto.
	 * @param division The domain division.
	 * @return The division dto.
	 */
	public DivisionDto domain2Dto(Division division) {
		return DivisionDto.getInstanceOf(division.getId(), division.getNumber(), division.getName(), 
				division.getDescription(), division.getParentDivisionId() != null ? division.getParentDivisionId() : 0, 
				division.getParentDivision() != null ? division.getParentDivision().getName() : "", 
				division.isActive(), divisionDao.isLastLevel(division.getId(), 1));
	}

	/**
	 * Check if division id is used as parent reference division
	 * @param divisionId The division id
	 * @return True if the account is used as parent reference division, otherwise false
	 */
	public boolean isUsedAsParentDivision(Integer divisionId) {
		return divisionDao.isUsedAsParentDivision(divisionId);
	}

	/**
	 * Get the list of division by account combination company excluding first level divisions.
	 * @param companyId The company id
	 * @return The list of divisions
	 */
	public List<DivisionDto> getLastDivsByCombi(Integer companyId) {
		List<DivisionDto> ret = new ArrayList<DivisionDto>();
		List<DivisionDto> divs = divisionDao.getByCombination(companyId);
		if (!divs.isEmpty()) {
			for (DivisionDto dto : divs) {
				if (dto.getPdId().intValue() != 0) {
					ret.add(dto);
				}
				dto = null; // free up memory
			}
		} 
		return ret;
	}

	/**
	 * Get the list of division by account combination company excluding first level divisions.
	 * @param companyId The company id
	 * @return The list of divisions
	 */
	public List<DivisionDto> getDivsByCombi(Integer companyId) {
		return divisionDao.getByCombination(companyId);
	}

	/**
	 * Retrieve the list of divisions
	 * @param divisionName The division name
	 * @param divisionId The division id
	 * @param isExact True if get the exact value, otherwise false
	 * @param limit The limit of item to be displayed
	 * @return The list of divisions
	 */
	public List<Division> retrieveDivisions(String divisionName, Integer divisionId, boolean isExact, Integer limit) {
		return divisionDao.retrieveDivisions(divisionName, divisionId, isExact, limit);
	}

	/**
	 * Get the list of divisions.
	 * @param name The division name.
	 * @return The list of divisions.
	 */
	public List<Division> getProjectDivisionsByName(String name) {
		return divisionDao.getProjectDivisionsByName(name);
	}

	/**
	 * Get the list of division with associated project.
	 * @param divisionName The division name.
	 * @param companyId The company id.
	 * @param isExact True if name to be evaluated is the exact value, otherwise false
	 * @return The list of divisions.
	 */
	public List<Division> getProjectDivisions(String divisionName, Integer companyId, boolean isExact) {
		return divisionDao.getProjectDivisions(divisionName, companyId, isExact);
	}
}