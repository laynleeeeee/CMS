package eulap.eb.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.KeyCodeDao;
import eulap.eb.domain.hibernate.KeyCode;

/**
 * Implementing class of {@link KeyCodeDao}

 *
 */
public class KeyCodeDaoImpl extends BaseDao<KeyCode> implements KeyCodeDao {

	@Override
	protected Class<KeyCode> getDomainClass() {
		return KeyCode.class;
	}

	@Override
	public KeyCode getKeyCode(String code) {
		DetachedCriteria keyCodeDc = getDetachedCriteria();
		keyCodeDc.add(Restrictions.like(KeyCode.FIELD.keyCode.name(), code));
		return get(keyCodeDc);
	}
}
