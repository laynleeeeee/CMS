package eulap.eb.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.PositionDao;
import eulap.eb.domain.hibernate.Position;
import eulap.eb.domain.hibernate.User;

/**
 * Class that handles the business logic of Position.

 */
@Service
public class PositionService {
	@Autowired
	private PositionDao posDao;

	/**
	 * Get all position.
	 * @return The list of positions.
	 */
	public List<Position> getAllPositions(){
		return posDao.getAllPositions();
	}
	
	/**
	 * Get the list of active positions.
	 * @return The list of positions.
	 */
	public List<Position> getAllPosition(String name){
		return posDao.getPositions(name.trim());
	}

	/**
	 * Get Position by name.
	 * @param name The name of position.
	 * @return The position object.
	 */
	public Position getPositionByName(String name){
		return posDao.getPositionByName(name);
	}

	/**
	 * Filter / Search Position
	 * @param name The name of position criteria.
	 * @param status The status (Active,Inactive,All)
	 * @param pageNumber The page number.
	 * @return The paged result of position.
	 */
	public Page<Position> searchPosition (String name, Integer status, Integer pageNumber){
		return posDao.searchPosition(name.trim(), status, new PageSetting(pageNumber, 25));
	}

	/**
	 * Get Position by id.
	 * @param id The unique id of the position.
	 * @return The Position object.
	 */
	public Position getPosition(Integer id){
		return posDao.get(id);
	}

	/**
	 * Saving of Position.
	 * @param position The position object.
	 * @param user The current logged in user.
	 */
	public void savePosition(Position position, User user){
		boolean isNew = position.getId() == 0 ? true : false;
		AuditUtil.addAudit(position, new Audit(user.getId(), isNew, new Date()));
		position.setName(position.getName().trim());
		posDao.saveOrUpdate(position);
	}

	/**
	 * Validate if position is unique or not.
	 * @param position The position object to be evaluated.
	 * @return True if unique, otherwise false.
	 */
	public boolean isUnique (Position position){
		if (position.getId() == 0)
			return posDao.isUniquePosition(position);
		Position oldPosition = posDao.get(position.getId());
		// User did not change the name
		if (position.getName().trim().equalsIgnoreCase(oldPosition.getName().trim()))
			return true;
		return posDao.isUniquePosition(position);
	}

	/**
	 * Get position by employee id
	 * @param employeeId The employee id
	 * @return The position name
	 */
	public Position getEmployeePosition(Integer employeeId) {
		return posDao.getPositionByEmployeeId(employeeId);
	}
}
