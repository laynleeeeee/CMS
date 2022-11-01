package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.DocumentType;

/**
 * Data Access Object of {@link DocumentType}

 *
 */
public interface DocumentTypeDao extends Dao<DocumentType>{

	/**
	 * Evaluates whether the Document Type has a corresponding duplicate.
	 * @param documentType The Document Type Object to be evaluated.
	 * @return True if a duplicate occur, otherwise false.
	 */
	boolean isDuplicate(DocumentType documentType);

	/**
	 * Filter Document Types by criteria.
	 * @param name The name of the Document Types.
	 * @param status The status of the Document Type Object.
	 * @param pageSetting The page setting.
	 * @return The paged collection of document types.
	 */
	Page<DocumentType> searchDocumentTypes(String name, Integer status, PageSetting pageSetting);

	/**
	 * Get the list of active {@link DocumentType}
	 * @return List of active {@link DocumentType}
	 */
	List<DocumentType> getActiveDocTypes ();

}
