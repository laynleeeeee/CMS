package eulap.eb.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.eb.dao.ArServiceLineDao;
import eulap.eb.dao.UnitMeasurementDao;
import eulap.eb.domain.hibernate.ArServiceLine;
import eulap.eb.domain.hibernate.ServiceSetting;
import eulap.eb.domain.hibernate.UnitMeasurement;

/**
 * A class that handles all the business logic of {@link ArServiceLine}

 *
 */
@Service
public class ArServiceLineService {
	@Autowired
	private ArServiceLineDao arServiceLineDao;
	@Autowired
	private UnitMeasurementDao unitMeasurementDao;
	@Autowired
	private ServiceSettingService serviceSettingService;
	
	/**
	 * Get the list of AR Lines.
	 * @param arTransactionId The unique ar transaction id.
	 * @return The list of AR Lines.
	 */
	public List<ArServiceLine> getArServiceLines(int arTransactionId) {
		return arServiceLineDao.getArServiceLines(arTransactionId);
	}

	/**
	 * Processes the Ar Lines that has not set up.
	 * @param arServiceLines The Ar Lines.
	 * @param companyId The company id.
	 * @return List of AR Lines.
	 */	
	public List<ArServiceLine> processArServiceLines (List<ArServiceLine> arServiceLines, Integer companyId) {
		List<ArServiceLine> ret = new ArrayList<ArServiceLine>();
		if (arServiceLines != null && !arServiceLines.isEmpty()) {
			for (ArServiceLine arl : arServiceLines) {
				if (arl.getServiceSettingName() != null && !arl.getServiceSettingName().trim().isEmpty()
						|| (arl.getAmount() != null && arl.getAmount() != 0.00)) {
						ServiceSetting serviceSetting = null;
						if(arl.getServiceSettingId() != null) {
							serviceSetting = serviceSettingService.getServiceSetting(arl.getServiceSettingId());
							arl.setServiceSettingName(serviceSetting.getName());
							arl.setServiceSettingId(serviceSetting.getId());
							arl.setServiceSetting(serviceSetting);
					}
					UnitMeasurement unitMeasurement = null;
					if (arl.getUnitOfMeasurementId() != null) {
						unitMeasurement = unitMeasurementDao.get(arl.getUnitOfMeasurementId());
						arl.setUnitMeasurementName(unitMeasurement.getName());
						arl.setUnitMeasurement(unitMeasurement);
					}
					ret.add(arl);
				}
			}
		}
		return ret;
	}
}
