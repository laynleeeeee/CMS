package eulap.eb.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.dao.CompanyDao;
import eulap.eb.dao.ItemDao;
import eulap.eb.dao.ItemVolumeConversionDao;
import eulap.eb.domain.hibernate.Item;
import eulap.eb.domain.hibernate.ItemVolumeConversion;
import eulap.eb.domain.hibernate.User;

/**
 * Business logic for Item Volume Conversion Service.

 */
@Service
public class ItemVolumeConversionService {
	@Autowired
	private ItemVolumeConversionDao volumeConversionDao;
	@Autowired
	private ItemDao itemDao;
	@Autowired
	CompanyDao companyDao;

	/**
	 * Save Item Volume Conversion.
	 * @param volumeConversion The Item Volume Conversion. 
	 * @param user The user logged.
	 */
	public void saveVolumeConversion(ItemVolumeConversion volumeConversion,
			User user) {
		Integer volumeConversionId = volumeConversion.getId();
		boolean isNew = volumeConversionId == 0;
		AuditUtil.addAudit(volumeConversion, new Audit(user.getId(), isNew, new Date()));
		volumeConversionDao.saveOrUpdate(volumeConversion);
	}

	/**
	 * Get the item volume conversion by id.
	 * @param volumeConversionId The item volume conversion id.
	 * @return The item volume conversion. 
	 */
	public ItemVolumeConversion getItemVolumeConversion(
			Integer volumeConversionId) {
		ItemVolumeConversion volumeConversion = volumeConversionDao.get(volumeConversionId);
		Item item = itemDao.get(volumeConversion.getItemId());
		volumeConversion.setItem(item);
		volumeConversion.setStockCode(item.getStockCode());
		return volumeConversion;
	}

	/**
	 * Get all Item Volume Conversion By search criteria.
	 * @param itemId The item Id.
	 * @param companyId THe company id.
	 * @param status The status
	 * @param pageNumber The page number.
	 * @return All filtered item Volume Conversion.
	 */
	public Page<ItemVolumeConversion> getAllItemVolumeConversion(
			Integer itemId, Integer companyId, String status, Integer pageNumber) {
		SearchStatus searchStatus = SearchStatus.getInstanceOf(status);
		Page<ItemVolumeConversion> volumeConversion = volumeConversionDao.getAllItemVolumeConversion(itemId, companyId,
				searchStatus, new PageSetting(pageNumber, 25));
		for (ItemVolumeConversion conversion : volumeConversion.getData()) {
			conversion.setItem(itemDao.get(conversion.getItemId()));
			conversion.setCompanyName(companyDao.getCompanyName(conversion.getCompanyId()));
		}
		return volumeConversion;
	}

	/**
	 * Check if has duplicate item per company;
	 * @param volumeConversion The Item Volume Conversion.
	 * @return True if has Duplicate Item otherwise false.
	 */
	public boolean hasDuplicateItem(ItemVolumeConversion volumeConversion) {
		return volumeConversionDao.hasDuplicateItem(volumeConversion);
	}
}
