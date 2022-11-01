package eulap.eb.service.inventory;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.util.NumberFormatUtil;
import eulap.eb.dao.ItemWeightedAveDao;
import eulap.eb.domain.hibernate.ItemWeightedAverage;

/**
 * Service class that will handle business logic for item weighted average

 */

@Service
public class ItemWeightedAveService {
	@Autowired
	private ItemWeightedAveDao itemWeightedAveDao;

	/**
	 * Compute and save weighted average
	 * @param warehouseId The warehouse id
	 * @param itemId The item id
	 * @param quantity The quantity
	 * @param unitCost The unit cost
	 */
	public synchronized void saveWeigthedAverage(int userId, int warehouseId, int itemId,
			Double origQuantity, double quantity, Double unitCost, boolean isStockOut) {
		ItemWeightedAverage itemWeightedAve = itemWeightedAveDao.getItemWeightedAverage(warehouseId, itemId);
		boolean isNew = itemWeightedAve == null;
		if (isNew) {
			itemWeightedAve = new ItemWeightedAverage();
			itemWeightedAve.setItemId(itemId);
			itemWeightedAve.setWarehouseId(warehouseId);
		}
		AuditUtil.addAudit(itemWeightedAve, new Audit(userId, isNew, new Date()));
		itemWeightedAve.setWeightedAve(computeWeightedAve(itemWeightedAve, origQuantity != null ? origQuantity : 0,
				quantity, unitCost != null ? unitCost : 0, isStockOut));
		itemWeightedAveDao.saveOrUpdate(itemWeightedAve);
	}

	private double computeWeightedAve(ItemWeightedAverage itemWeightedAve, double origQty,
			double quantity, double unitCost, boolean isStockOut) {
		double diff = Math.abs(quantity) - Math.abs(origQty);
		double totalQuantity = itemWeightedAve.getQuantity() + (isStockOut ? -diff : diff);
		double totalAmount = NumberFormatUtil.multiplyWFP(itemWeightedAve.getQuantity(), itemWeightedAve.getWeightedAve());
		totalAmount += NumberFormatUtil.multiplyWFP((isStockOut ? -diff : diff), unitCost);
		itemWeightedAve.setQuantity(totalQuantity);
		return NumberFormatUtil.divideWFP(totalAmount, totalQuantity);
	}
}
