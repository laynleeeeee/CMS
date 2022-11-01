package eulap.eb.service.workflow;

import java.util.List;

/**
 * A class that hold the properties of form status

 *
 */
public class FormStatusProp {
	private final int id;
	private final int statusId;
	private final String title;
	private final int nextId;
	private final List<Integer> required;
	private final int productCode;
	private final int moduleCode;
	private final boolean isEditable;
	private final List<Integer> nextIds;

	private FormStatusProp(int id, int statusId, String title, int nextId,
			int productCode, int moduleCode, List<Integer> required,
			boolean isEditable, List<Integer> nextIds) {
		this.id = id;
		this.statusId = statusId;
		this.title = title;
		this.nextId = nextId;
		this.required = required;
		this.productCode = productCode;
		this.moduleCode = moduleCode;
		this.isEditable = isEditable;
		this.nextIds = nextIds;
	}

	protected static FormStatusProp getInstanceOf(int id, int statusId, String title,
			int nextId, int productCode, int moduleCode, List<Integer> required,
			boolean isEditable, List<Integer> nextIds) {
		return new FormStatusProp(id, statusId, title, nextId, productCode,
				moduleCode, required, isEditable, nextIds);
	}

	public int getId() {
		return id;
	}

	public int getStatusId() {
		return statusId;
	}

	public String getTitle() {
		return title;
	}

	public int getNextId() {
		return nextId;
	}

	public List<Integer> getRequired() {
		return required;
	}

	public int getProductCode() {
		return productCode;
	}

	public int getModuleCode() {
		return moduleCode;
	}

	public boolean isEditable() {
		return isEditable;
	}

	public List<Integer> getNextIds() {
		return nextIds;
	}

	@Override
	public String toString() {
		return "FormStatusProp [id=" + id + ", statusId=" + statusId
				+ ", title=" + title + ", nextId=" + nextId + ", required="
				+ required + ", productCode=" + productCode + ", moduleCode="
				+ moduleCode + ", isEditable=" + isEditable + "]";
	}
}