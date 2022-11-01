package eulap.eb.web.dto;

/**
 * DTO for IS month

 *
 */
public class MonthDto {
	private Integer month;
	private String name;

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "MonthDto [month=" + month + ", name=" + name + "]";
	}
}
