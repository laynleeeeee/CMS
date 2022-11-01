package eulap.common.util;

import java.util.Collection;
import java.util.Date;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

/**
 * The page handler for the hibernate domain.

 *
 * @param <T> The domain class
 */
public class Page<T> {
	private final PageSetting pageSetting;
	private final Collection<T> data;
	private final int totalRecords;
	private final int dataSize;
	private final int currentPage;
	private final int nextPage;
	private final int lastPage;
	private final int prevPage;
	private int sEcho;
	public Page(PageSetting pageSetting, Collection<T> data, int totalRecords) {
		this.pageSetting = pageSetting;
		this.totalRecords = totalRecords;
		this.data = data;
		this.dataSize = data.size();
		this.currentPage = pageSetting.getPageNumber();
		this.nextPage = currentPage + 1;
		this.prevPage = currentPage - 1;
		double lstPageT =  (double)totalRecords / (double)pageSetting.getMaxResult();
		this.lastPage = (int) (lstPageT % (int)lstPageT != 0 ? lstPageT + 1 : lstPageT); 
	}

	public PageSetting getPageSetting() {
		return pageSetting;
	}

	public Collection<T> getData() {
		return data;
	}

	public int getTotalRecords() {
		return totalRecords;
	}

	public int getDataSize() {
		return dataSize;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public int getLastPage() {
		return lastPage;
	}

	public int getNextPage() {
		return nextPage;
	}

	public int getPrevPage() {
		return prevPage;
	}
	
	public int getFirstItemIndex () {
		return (currentPage-1) * pageSetting.getMaxResult() + 1;
	}

	@Override
	public String toString() {
		return "Page [currentPage=" + currentPage + ", data=" + data
				+ ", dataSize=" + dataSize + ", lastPage=" + lastPage
				+ ", nextPage=" + nextPage + ", pageSetting=" + pageSetting
				+ ", prevPage=" + prevPage + ", totalRecords=" + totalRecords
				+ "]";
	}
	
	public void setsEcho(int sEcho) {
		this.sEcho = sEcho;
	}

	/**
	 * Convert the data to JSON Object.
	 * @return The formatted JSON Object.
	 */
	public String toJSONObject () {
		JsonConfig config = new JsonConfig();
		return toJSONObject(config);
	}

	/**
	 * Convert the data to JSON object.
	 * @param config The added configuration.
	 * @return The formatted JSON object.
	 */
	public String toJSONObject (JsonConfig config){
		JSONObject jsonResponse = new JSONObject();
		 jsonResponse.accumulate("sEcho", sEcho);
		 jsonResponse.accumulate("iTotalRecords", totalRecords);
		 jsonResponse.accumulate("iTotalDisplayRecords", totalRecords);
		 // setting up the different configuration. 
		 JsonValueProcessor doubleProcessor = new DoubleValueProcessor();
		 config.registerJsonValueProcessor(double.class, doubleProcessor);
		 config.registerJsonValueProcessor(Double.class, doubleProcessor);
		 JsonValueProcessor dateProcessor = new DateValueProcessor();
		 config.registerJsonValueProcessor(Date.class, dateProcessor);
		 
		 jsonResponse.accumulate("aaData", data, config);
		return jsonResponse.toString();
	}
	
	private static class DoubleValueProcessor implements JsonValueProcessor{
		@Override
		public Object processObjectValue(String arg0, Object arg1, JsonConfig arg2) {
			if (arg1 == null)
				return null;
			if (!(arg1 instanceof Double))
				throw new UnsupportedOperationException("invalid value processor");
			Double dbl = (Double) arg1;
			return NumberFormatUtil.format(dbl);
		}
		
		@Override
		public Object processArrayValue(Object arg0, JsonConfig arg1) {
			//DO Nothing
			return null;
		}
	}
	
	private static class DateValueProcessor implements JsonValueProcessor {
		@Override
		public Object processObjectValue(String arg0, Object arg1,
				JsonConfig arg2) {
			if (arg1 == null)
				return arg1;
			Date date = (Date) arg1;
			return DateUtil.formatDate(date);
		}

		@Override
		public Object processArrayValue(Object arg0, JsonConfig arg1) {
			// Do nothing
			return null;
		}
	}
}
