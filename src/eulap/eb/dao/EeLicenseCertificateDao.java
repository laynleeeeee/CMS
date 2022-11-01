package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.EeLicenseCertificate;

/**
 * Data Access Object interface of {@link EeLicenseCertificate}

 */

public interface EeLicenseCertificateDao extends Dao<EeLicenseCertificate> {

	/**
	 * Get the list of employee licenses and certificates
	 * @param ebObjectId The parent EB object id
	 * @return The list of employee licenses and certificates
	 */
	List<EeLicenseCertificate> getLicCertByEbObjectId(Integer ebObjectId);

}
