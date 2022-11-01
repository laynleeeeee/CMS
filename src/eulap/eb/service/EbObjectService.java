package eulap.eb.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.eb.dao.EBObjectDao;
import eulap.eb.dao.ObjectToObjectDao;
import eulap.eb.domain.hibernate.EBObject;

/**
 * Service class that handles {@link EBObject}

 *
 */
@Service
public class EbObjectService {
	@Autowired
	private EBObjectDao ebObjectDao;

	/**
	 * Save the eb object and return the id of the saved eb object.
	 * @param userId The user id of the logged user.
	 * @param objectTypeId The object type id.
	 * @param date The date.
	 * @return The id of the newly saved eb object.
	 */
	public Integer saveAndGetEbObjectId(Integer userId, Integer objectTypeId, Date date) {
		// Create new Eb Object.
		EBObject ebObject = new EBObject();
		AuditUtil.addAudit(ebObject, new Audit(userId, true, date));
		ebObject.setObjectTypeId(objectTypeId);
		ebObjectDao.save(ebObject);
		return ebObject.getId();
	}

	
}
