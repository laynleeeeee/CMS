package eulap.eb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.eb.dao.KeyCodeDao;
import eulap.eb.domain.hibernate.KeyCode;

/**
 * Business logic for Key Code module.

 *
 */
@Service
public class KeyCodeService {
	@Autowired
	private KeyCodeDao keyCodeDao;

	/**
	 * Get the Key Code using its code.
	 * @param code The unique code.
	 * @return {@link KeyCode}
	 */
	public KeyCode getKeyCode(String code) {
		return keyCodeDao.getKeyCode(code);
	}

}
