package eulap.eb.web.dto;

/**
 * Container for the item buying details

 *
 */
public class RItemDetailValue {
	private Integer typeId;
	private String name;
	private Double value;

	public static RItemDetailValue getInstanceOf(Integer typeId, String name, Double value) {
		RItemDetailValue itemValue = new RItemDetailValue();
		itemValue.setTypeId(typeId);
		itemValue.setName(name);
		itemValue.setValue(value);
		return itemValue;
	}

	/**
	 * Get the id of the type of detail.
	 * @return The type id.
	 */
	public Integer getTypeId() {
		return typeId;
	}

	/**
	 * Get the type of the item's detail value:
	 * <br> 1 = Selling Discount
	 * <br> 2 = Selling Add on
	 * <br> 3 = Buying Discount
	 * <br> 4 = Buying Add on
	 * @param typeId the id of the type of detail.
	 */
	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RItemDetailValue [typeId=").append(typeId)
				.append(", name=").append(name).append(", value=")
				.append(value).append("]");
		return builder.toString();
	}

}
