package eulap.eb.web.dto;

import java.util.List;


/**
 * Customer Purchase Dto.

 */
public class CustomerPurchaseVolumeDto {
	private List<ItemSalesCustomer> itemSalesCustomers;

	public List<ItemSalesCustomer> getItemSalesCustomers() {
		return itemSalesCustomers;
	}

	public void setItemSalesCustomers(List<ItemSalesCustomer> itemSalesCustomers) {
		this.itemSalesCustomers = itemSalesCustomers;
	}

	@Override
	public String toString() {
		return "CustomerPurchaseVolumeDto [itemSalesCustomers="
				+ itemSalesCustomers + "]";
	}
}
