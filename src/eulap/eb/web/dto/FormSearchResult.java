package eulap.eb.web.dto;

import java.util.List;

/**
 * Form search result data access object.
 * 

 * 
 */
public class FormSearchResult {
	private final String title;
	private final int id;
	private final List<ResultProperty> properties;
	
	private FormSearchResult(int id, String formName, List<ResultProperty> properties) {
		this.title = formName;
		this.properties = properties;
		this.id = id;
	}

	public static FormSearchResult getInstanceOf(int id, String formName, List<ResultProperty> properties) {
		FormSearchResult result = new FormSearchResult(id, formName, properties);
		return result;
	}

	public String getTitle() {
		return title;
	}

	public List<ResultProperty> getProperties() {
		return properties;
	}

	/**
	 * The reference id of the this result.
	 */
	public int getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return "FormSearchResult [title=" + title + ", id=" + id
				+ ", properties=" + properties + "]";
	}
}
