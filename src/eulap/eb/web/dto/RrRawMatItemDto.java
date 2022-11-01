package eulap.eb.web.dto;

/**
 * Container class that will hold the attributes of RR Raw Material Item.

 *
 */
public class RrRawMatItemDto {

	private Integer itemId;
	private String stockCode;
	private String description;
	private String uom;
	private Double buyingPrice;
	private Double quantity;
	private String stockCodeErrMsg;
	private Double amount;
	private Double totalWeight;
	private Double totalDiscount;
	private Double totalOtherCharges;

	public static RrRawMatItemDto getInstanceOf (Integer itemId, String stockCode, String description, String uom, Double buyingPrice,
			Double quantity, Double amount, Double totalWeight, Double totalDiscount, Double totalOtherCharges) {
		RrRawMatItemDto rrItemDto = new RrRawMatItemDto();
		rrItemDto.itemId = itemId;
		rrItemDto.stockCode = stockCode;
		rrItemDto.description = description;
		rrItemDto.uom = uom;
		rrItemDto.buyingPrice = buyingPrice;
		rrItemDto.quantity = quantity;
		rrItemDto.amount = amount;
		rrItemDto.totalWeight = totalWeight;
		rrItemDto.totalDiscount = totalDiscount;
		rrItemDto.totalOtherCharges = totalOtherCharges;
		return rrItemDto;
	}

	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	public String getStockCode() {
		return stockCode;
	}

	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	public Double getBuyingPrice() {
		return buyingPrice;
	}

	public void setBuyingPrice(Double buyingPrice) {
		this.buyingPrice = buyingPrice;
	}

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public String getStockCodeErrMsg() {
		return stockCodeErrMsg;
	}

	public void setStockCodeErrMsg(String stockCodeErrMsg) {
		this.stockCodeErrMsg = stockCodeErrMsg;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Double getTotalWeight() {
		return totalWeight;
	}

	public void setTotalWeight(Double totalWeight) {
		this.totalWeight = totalWeight;
	}

	public Double getTotalDiscount() {
		return totalDiscount;
	}

	public void setTotalDiscount(Double totalDiscount) {
		this.totalDiscount = totalDiscount;
	}

	public Double getTotalOtherCharges() {
		return totalOtherCharges;
	}

	public void setTotalOtherCharges(Double totalOtherCharges) {
		this.totalOtherCharges = totalOtherCharges;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RrRawMatItemDto [itemId=").append(itemId).append(", stockCode=").append(stockCode)
				.append(", description=").append(description).append(", uom=").append(uom).append(", buyingPrice=")
				.append(buyingPrice).append(", quantity=").append(quantity).append(", stockCodeErrMsg=")
				.append(stockCodeErrMsg).append(", amount=").append(amount).append(", totalWeight=").append(totalWeight)
				.append(", totalDiscount=").append(totalDiscount).append(", totalOtherCharges=")
				.append(totalOtherCharges).append("]");
		return builder.toString();
	}

}
