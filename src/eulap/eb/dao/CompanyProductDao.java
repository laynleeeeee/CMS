package eulap.eb.dao;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.CompanyProduct;

/**
 * Company product data access object.

 *
 */
public interface CompanyProductDao extends Dao<CompanyProduct>{

	/**
	 * Get the company product given the company id.
	 */
	CompanyProduct getCompanyProduct (int companyId);
}
