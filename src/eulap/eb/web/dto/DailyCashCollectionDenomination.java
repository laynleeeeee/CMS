package eulap.eb.web.dto;

import java.util.List;

/**
 * Daily cash collection domination dto.

 *
 */
public class DailyCashCollectionDenomination {
	private List<DailyCashCollection> cashCollections;

	public DailyCashCollectionDenomination (List<DailyCashCollection> cashCollections) {
		this.cashCollections = cashCollections;
	}

	public List<DailyCashCollection> getCashCollections() {
		return cashCollections;
	}

	@Override
	public String toString() {
		return "DailyCashCollectionMain [cashCollections=" + cashCollections + "]";
	}
}
