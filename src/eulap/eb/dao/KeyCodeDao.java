package eulap.eb.dao;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.KeyCode;

/**
 * DAO Layer of {@link KeyCode}

 *
 */
public interface KeyCodeDao extends Dao<KeyCode>{

	/**
	 * Get the Key Code using its code.
	 * @param code The unique code.
	 * @return {@link KeyCode}
	 */
	KeyCode getKeyCode(String code);
}
