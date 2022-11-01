package eulap.common.util;

/**
 * Class that handles the page setting. 

 *
 */
public class PageSetting {
	public static final int NO_PAGE_CONSTRAINT = -1;
	public static final int START_PAGE = 1;
	public static final String PAGE_NUMBER_ATTRIBUTE_KEY = "pageNumber";
	public static final int MAX_RECORD_100 = 100;
	public static final int MAX_ADMIN_RECORD = 25;
	private static final int DEFAULT_MAX_PAGE = 20;
	
	private final int pageNumber;
	private final int maxPerPage;
	
	public PageSetting(int page) {
		this (page, DEFAULT_MAX_PAGE);
	}
	
	public PageSetting(int page, int maxPerPage) {
		if (page < 1)
			throw new RuntimeException("Page number must start at page number 1");
		this.pageNumber = page < 1 ? 0 : page;
		this.maxPerPage = maxPerPage;
	}

	public int getStartResult () {
		return (pageNumber - 1) * maxPerPage; 
	}
	
	public int getMaxResult () {
		return maxPerPage;
	}
	
	public int getPageNumber() {
		return pageNumber;
	}
}
