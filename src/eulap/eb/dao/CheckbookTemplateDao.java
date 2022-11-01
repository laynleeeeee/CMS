package eulap.eb.dao;

import java.util.Collection;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.CheckbookTemplate;

/**
 * Data Access Object for {@link CheckbookTemplate}

 *
 */
public interface CheckbookTemplateDao extends Dao<CheckbookTemplate>{
	/**
	 * Returns ordered CheckbookTemplate by name in ascending order.
	 */
	Collection<CheckbookTemplate> getCheckbookTemplates();
}
