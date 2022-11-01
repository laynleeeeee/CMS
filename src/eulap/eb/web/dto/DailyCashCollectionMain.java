package eulap.eb.web.dto;

import java.util.List;

/**
 * Daily cash collection dto.

 *
 */
public class DailyCashCollectionMain {
	private List<DailyCashCollectionDenomination> cashCollections;

	public DailyCashCollectionMain (List<DailyCashCollectionDenomination> cashCollections) {
		this.cashCollections = cashCollections;
	}

	public List<DailyCashCollectionDenomination> getCashCollections() {
		return cashCollections;
	}

	@Override
	public String toString() {
		return "DailyCashCollectionMain [cashCollections=" + cashCollections + "]";
	}
}
