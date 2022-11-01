package eulap.eb.web.dto;

/**
 * The result property.

 */
public class ResultProperty {
	private String value;
	private String name;

	public static ResultProperty getInstance (String name, String value) {
		ResultProperty prop = new ResultProperty();
		prop.name = name;
		prop.value = value;
		return prop;
	}

	public String getValue() {
		return value;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "ResultProperty [value=" + value + ", name=" + name + "]";
	}
}