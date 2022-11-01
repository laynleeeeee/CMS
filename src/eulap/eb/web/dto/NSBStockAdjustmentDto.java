package eulap.eb.web.dto;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import eulap.eb.domain.hibernate.SerialItem;
import eulap.eb.domain.hibernate.StockAdjustment;

/**
 * Data transfer object for NSB Stock Adjustment.

 */

public class NSBStockAdjustmentDto {
	private StockAdjustment stockAdjustment;
	private List<SerialItem> serialItems;
	private String serialItemsJson;
	private String siMessage;
	public static final int SA_IN_TO_SI_OR_TYPE_ID = 56;
	public static final int SA_OUT_TO_SI_OR_TYPE_ID = 63;

	public StockAdjustment getStockAdjustment() {
		return stockAdjustment;
	}

	public void setStockAdjustment(StockAdjustment stockAdjustment) {
		this.stockAdjustment = stockAdjustment;
	}

	public List<SerialItem> getSerialItems() {
		return serialItems;
	}

	public void setSerialItems(List<SerialItem> serialItems) {
		this.serialItems = serialItems;
	}

	public String getSerialItemsJson() {
		return serialItemsJson;
	}

	public void setSerialItemsJson(String serialItemsJson) {
		this.serialItemsJson = serialItemsJson;
	}

	public void serializeSerialItems() {
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		serialItemsJson = gson.toJson(serialItems);
	}

	public void derializeSerialItems() {
		Gson gson = new GsonBuilder().
				excludeFieldsWithoutExposeAnnotation().setDateFormat("MM/dd/yyyy").create();
		Type type = new TypeToken <List<SerialItem>>(){}.getType();
		serialItems = gson.fromJson(serialItemsJson, type);
	}

	public String getSiMessage() {
		return siMessage;
	}

	public void setSiMessage(String siMessage) {
		this.siMessage = siMessage;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GvchStockAdjustmentDto [stockAdjustment=").append(stockAdjustment).append(", serialItems=")
				.append(serialItems).append(", serialItemsJson=").append(serialItemsJson).append(", siMessage=")
				.append(siMessage).append("]");
		return builder.toString();
	}
}