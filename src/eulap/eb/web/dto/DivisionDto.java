package eulap.eb.web.dto;

import java.util.List;

/**
 * DTO for {@code Division}

 *
 */
public class DivisionDto {
	private Integer id;
	private String number;
	private String name;
	private String description;
	private Integer pdId;
	private String pdName;
	private boolean active;
	private boolean isMainParent;
	private boolean isLastLevel;
	private DivisionDto parentDivision;
	private List<DivisionDto> childrenDivision;

	public static final int DIV_ALL = -1;
	public static final int MAIN_LEVEL = 1;
	public static final int SUB_LEVEL = 2;

	/**
	 * Create instance of {@code DivisionDto}
	 */
	public static DivisionDto getInstanceOf(Integer id, String number, String name, String description, 
		Integer pdId, String pdName, boolean active, boolean isLastLevel) {
		DivisionDto dto = new DivisionDto();
		dto.id = id;
		dto.number = number;
		dto.name = name;
		dto.description = description;
		dto.pdId = pdId;
		dto.pdName = pdName;
		dto.active = active;
		dto.isLastLevel = isLastLevel;
		return dto;
	}

	/**
	 * Get the division id.
	 */
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Get the division number.
	 */
	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	/**
	 * Get the division name.
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the division description.
	 */
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Get the division parent id.
	 */
	public Integer getPdId() {
		return pdId;
	}

	public void setPdId(Integer pdId) {
		this.pdId = pdId;
	}

	/**
	 * Get the division parent name.
	 */
	public String getPdName() {
		return pdName;
	}

	public void setPdName(String pdName) {
		this.pdName = pdName;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isMainParent() {
		return isMainParent;
	}

	public void setMainParent(boolean isMainParent) {
		this.isMainParent = isMainParent;
	}

	public boolean isLastLevel() {
		return isLastLevel;
	}

	public void setLastLevel(boolean isLastLevel) {
		this.isLastLevel = isLastLevel;
	}

	public DivisionDto getParentDivision() {
		return parentDivision;
	}

	public void setParentDivision(DivisionDto parentDivision) {
		this.parentDivision = parentDivision;
	}

	public List<DivisionDto> getChildrenDivision() {
		return childrenDivision;
	}

	public void setChildrenDivision(List<DivisionDto> childrenDivision) {
		this.childrenDivision = childrenDivision;
	}

	@Override
	public String toString() {
		return "DivisionDto [id=" + id + ", number=" + number + ", name=" + name + ", description=" + description
				+ ", pdId=" + pdId + ", pdName=" + pdName + ", active=" + active + ", isMainParent=" + isMainParent
				+ ", isLastLevel=" + isLastLevel + ", parentDivision=" + parentDivision + ", childrenDivision="
				+ childrenDivision + "]";
	}
}
