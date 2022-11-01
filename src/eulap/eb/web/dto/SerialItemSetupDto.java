package eulap.eb.web.dto;

import eulap.eb.domain.hibernate.Item;
import eulap.eb.domain.hibernate.SerialItemSetup;

/**
 * Data transfer object for retail serial item setup.

 */
public class SerialItemSetupDto {
	private Item item;
	private SerialItemSetup serialItemSetup;

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public SerialItemSetup getSerialItemSetup() {
		return serialItemSetup;
	}

	public void setSerialItemSetup(SerialItemSetup serialItemSetup) {
		this.serialItemSetup = serialItemSetup;
	}
}