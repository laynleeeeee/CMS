package eulap.common.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Search status.

 * 
 */
public enum SearchStatus {
	All, Active, Inactive;
	
	/**
	 * Get list of search statuses.
	 */
	public static List<String> getSearchStatus () {
		List<String> searchStatus = new ArrayList<String>();
		for (SearchStatus status : SearchStatus.values())
			searchStatus.add(status.name());
		return searchStatus;
	}
	
	public static SearchStatus getInstanceOf(String statusName) {
		for (SearchStatus status : SearchStatus.values())
			if (statusName.equals(status.name()))
				return status;
		throw new RuntimeException ("invalid status name : " + statusName);
	}
}
