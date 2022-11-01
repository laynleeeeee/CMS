package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.Position;

/**
 * Data access object of {@link Position}

 */
public interface PositionDao extends Dao<Position>{

	/**
	 * Get all positions.
	 * @return The list of all position.
	 */
	List<Position> getAllPositions();
	
	/**
	 * Get the list of active positions by name.
	 * @return The list of Positions.
	 */
	List<Position> getPositions(String name);

	/**
	 * Get active position by name.
	 * @param name The name criteria.
	 * @return The position object.
	 */
	Position getPositionByName(String name);

	/**
	 * Search / filter Position.
	 * @param name The name of position.
	 * @param status The status (Active,Inactive,All)
	 * @return The paged result of filtered Position.
	 */
	Page<Position> searchPosition (String name, Integer status, PageSetting pageSetting);

	/**
	 * Validate if position is unique.
	 * @param position The position object.
	 * @return True if unique, otherwise false.
	 */
	boolean isUniquePosition (Position position);

	/**
	 * Get position by employee id
	 * @param employeeId The employee id
	 * @return The position name of the employee
	 */
	Position getPositionByEmployeeId(Integer employeeId);
}
