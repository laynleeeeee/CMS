package bp.web.ar;

import java.util.Date;

import eulap.common.domain.Audit;
import eulap.common.domain.Domain;
import eulap.eb.domain.hibernate.User;

public class AuditUtil {
	
	/**
	 * Add audit to the domain.
	 * @param domain The domain to updated.
	 * @param audit the audit logs of the domain.
	 */
	public static void addAudit (Domain domain, Audit audit){
		if (audit.isNew()) {
			domain.setCreatedBy(audit.getUserId());
			domain.setCreatedDate(audit.getDate());	
		}
		domain.setUpdatedBy(audit.getUserId());
		domain.setUpdatedDate(audit.getDate());
	}

	public static void addAudit (Domain domain, User user, Date date) {
		if (domain.getId() == 0){
			domain.setCreatedBy(user.getId());
			domain.setCreatedDate(date);	
		}
		domain.setUpdatedBy(user.getId());
		domain.setUpdatedDate(date);
	}
}
