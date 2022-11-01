package eulap.eb.web.dto;

import java.util.Date;

import eulap.eb.service.inventory.InventoryItem;

/**
 * DTO for repacking item.

 *
 */
public class RepackingItemDto implements InventoryItem{
	private Integer id;
	private Integer itemId;
	private Double quantity;
	private Double unitCost;
	private Integer toItemId;
	private Double repackedQuantity;
	private Double computedUnitCost;
	private boolean isIgnore;
	private Double origQty;
	private Double toOrigQty;

	private RepackingItemDto (Integer id, Integer itemId, Double quantity,
			Integer toItemId, Double repackedQty, Double origQty, Double toOrigQty) {
		this.id = id;
		this.itemId = itemId;
		this.quantity = quantity;
		this.toItemId = toItemId;
		this.repackedQuantity = repackedQty;
		this.origQty = origQty;
		this.toOrigQty = toOrigQty;
	}

	public static RepackingItemDto getInstanceOf(Integer id, Integer itemId, Double quantity,
			Integer toItemId, Double repackedQty, Double origQty, Double toOrigQty) {
		return new RepackingItemDto(id, itemId, quantity, toItemId, repackedQty, origQty, toOrigQty);
	}

	public int getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	public Integer getToItemId() {
		return toItemId;
	}

	public void setToItemId(Integer toItemId) {
		this.toItemId = toItemId;
	}

	public Double getRepackedQuantity() {
		return repackedQuantity;
	}

	public void setRepackedQuantity(Double repackedQuantity) {
		this.repackedQuantity = repackedQuantity;
	}

	@Override
	public String getStockCode() {
		return null;
	}

	public Double getQuantity() {
		return quantity;
	}

	@Override
	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	@Override
	public Double getInventoryCost() {
		return unitCost;
	}

	@Override
	public void setInventoryCost(Double inventoryCost) {
		this.unitCost = inventoryCost;
	}

	@Override
	public Double getUnitCost() {
		return unitCost;
	}

	@Override
	public void setUnitCost(Double unitCost) {
		this.unitCost = unitCost;
	}

	public Double getComputedUnitCost() {
		return computedUnitCost;
	}

	public void setComputedUnitCost(Double computedUnitCost) {
		this.computedUnitCost = computedUnitCost;
	}

	@Override
	public boolean isIgnore() {
		return isIgnore;
	}

	@Override
	public void setIgnore(boolean isIgnore) {
		this.isIgnore = isIgnore;
	}

	@Override
	public Integer getReceivedStockId() {
		//Do nothing.
		return null;
	}

	@Override
	public void setReceivedStockId(Integer receivedStockId) {
		// Do nothing.
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public void setCreatedBy(int id) {
		
	}

	@Override
	public int getCreatedBy() {
		return 0;
	}

	@Override
	public void setCreatedDate(Date date) {
	}

	@Override
	public Date getCreatedDate() {
		return null;
	}

	@Override
	public void setUpdatedBy(int id) {
	}

	@Override
	public int getUpdatedBy() {
		return 0;
	}

	@Override
	public void setUpdatedDate(Date date) {
	}

	@Override
	public Date getUpdatedDate() {
		return null;
	}

	public Double getOrigQty() {
		return origQty;
	}

	public void setOrigQty(Double origQty) {
		this.origQty = origQty;
	}

	public Double getToOrigQty() {
		return toOrigQty;
	}

	public void setToOrigQty(Double toOrigQty) {
		this.toOrigQty = toOrigQty;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RepackingItemDto [id=").append(id).append(", itemId=").append(itemId).append(", quantity=")
				.append(quantity).append(", unitCost=").append(unitCost).append(", toItemId=").append(toItemId)
				.append(", repackedQuantity=").append(repackedQuantity).append(", computedUnitCost=")
				.append(computedUnitCost).append(", isIgnore=").append(isIgnore).append(", origQty=").append(origQty)
				.append(", toOrigQty=").append(toOrigQty).append("]");
		return builder.toString();
	}
}
