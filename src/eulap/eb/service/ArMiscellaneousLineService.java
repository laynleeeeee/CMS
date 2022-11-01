package eulap.eb.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.eb.dao.UnitMeasurementDao;
import eulap.eb.domain.hibernate.ArMiscellaneousLine;
import eulap.eb.domain.hibernate.ServiceSetting;
import eulap.eb.domain.hibernate.UnitMeasurement;

@Service
public class ArMiscellaneousLineService{
	@Autowired
	private UnitMeasurementDao unitMeasurementDao;
	@Autowired
	private ServiceSettingService serviceSettingService;

	/**
	 * Processes the Ar Miscellaneous Lines that has not set up.
	 * @param arLines The Ar Miscellaneous Lines.
	 * @param companyId The company id.
	 * @return List of AR Miscellaneous Lines.
	 */	
	public List<ArMiscellaneousLine> processArLines (List<ArMiscellaneousLine> arLines, Integer companyId) {
		List<ArMiscellaneousLine> ret = new ArrayList<ArMiscellaneousLine>();
		if (arLines != null) {
			for (ArMiscellaneousLine arL : arLines) {
				if (arL.getServiceSettingName() != null && !arL.getServiceSettingName().trim().isEmpty()
						|| (arL.getAmount() != null && arL.getAmount() != 0.00)) {
					ServiceSetting serviceSetting = null;
					if(arL.getServiceSettingId() != null) {
						serviceSetting = serviceSettingService.getServiceSetting(arL.getServiceSettingId());
						arL.setServiceSettingName(serviceSetting.getName());
						arL.setServiceSettingId(serviceSetting.getId());
						arL.setServiceSetting(serviceSetting);
					}
					UnitMeasurement unitMeasurement = null;
					if (arL.getUnitOfMeasurementId() != null) {
						unitMeasurement = unitMeasurementDao.get(arL.getUnitOfMeasurementId());
						arL.setUnitMeasurementName(unitMeasurement.getName());
						arL.setUnitMeasurement(unitMeasurement);
					}
					ret.add(arL);
				}
			}
		}
		return ret;
	}
}
