package eulap.common.dao;

/**
 * Enum class that holds mysql special characters.

 * TODO: Consider multiple character.
 */
public enum MysqlSpecialCharacterField {
	HASH("#"),
	AMPERSAND("&"),
	UNDERSCORE("_"),
	PERCENT("%");
	
	private final String value;
	
	private MysqlSpecialCharacterField(String value){
		this.value = value;
	}
	
	/**
	 * Get the special value
	 * @return
	 */
	public String getValue () {
		return value;
	}
	
	public String toString() {
		return this.value + "";
	}
}
