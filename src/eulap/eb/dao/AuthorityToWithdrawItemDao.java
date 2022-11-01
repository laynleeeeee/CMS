package eulap.eb.dao;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.AuthorityToWithdrawItem;
import eulap.eb.web.dto.AtwItemDto;

/**
 * Data access object interface for {@link AuthorityToWithdrawItem}

 */

public interface AuthorityToWithdrawItemDao extends Dao<AuthorityToWithdrawItem> {

	/**
	 * Get {@code AuthorityToWithdrawItem} DTO by item reference object id
	 * @param refItemObjectId The item reference object id
	 * @return The authority to withdraw item object
	 */
	AtwItemDto getAtwItemByRefItemObjectId(Integer refItemObjectId);

	/**
	 * Get the remaining sales order item quantity.
	 * @param authorityToWithdrawItemId The authority to withdraw item id.
	 * @param referenceObjectId The reference object id.
	 * @return The remaining sales order item quantity.
	 */
	double getRemainingQty(Integer authorityToWithdrawItemId, Integer referenceObjectId);
}
