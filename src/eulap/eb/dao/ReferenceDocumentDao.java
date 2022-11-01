package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.ReferenceDocument;

/**
 * Data Access Object {@link ReferenceDocument}

 */
public interface ReferenceDocumentDao extends Dao<ReferenceDocument>{

	/**
	 * Get the reference document object by the eb object id.
	 * @param ebObjectId The eb object id.
	 */
	ReferenceDocument getRDByEbObject(int ebObjectId);

	/**
	 * Get the reference document object by the eb object id.
	 * @param ebObjectId The eb object id.
	 * @param orTypeId The or type id.
	 */
	ReferenceDocument getRDByEbObject(int ebObjectId, Integer orTypeId);

	/**
	 * Get the list reference document object by the eb object id.
	 * @param ebObjectId The eb object id.
	 */
	List<ReferenceDocument> getRDsByEbObject(int ebObjectId);

	/**
	 * Get the list reference document object by the eb object id.
	 * @param ebObjectId The eb object id.
	 * @param orTypeId The or type id.
	 */
	List<ReferenceDocument> getRDsByEbObject(int ebObjectId, Integer orTypeId);
}
