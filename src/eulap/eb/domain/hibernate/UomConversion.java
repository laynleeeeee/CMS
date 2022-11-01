package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * Domain class for UOM_CONVERSION

 *
 */
@Table
@Entity(name="UOM_CONVERSION")
public class UomConversion extends BaseDomain{
	private Integer uomFrom;
	private Integer uomTo;
	private String name;
	private Double value;
	private boolean active;

	public enum FIELD {
		id, uomFrom, uomTo, name, value, active;
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "UOM_CONVERSION_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name = "UOM_FROM", columnDefinition = "int(10)")
	public Integer getUomFrom() {
		return uomFrom;
	}

	public void setUomFrom(Integer uomFrom) {
		this.uomFrom = uomFrom;
	}

	@Column(name = "UOM_TO", columnDefinition = "int(10)")
	public Integer getUomTo() {
		return uomTo;
	}

	public void setUomTo(Integer uomTo) {
		this.uomTo = uomTo;
	}

	@Column(name = "NAME", columnDefinition = "VARCHAR(50)")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "VALUE", columnDefinition = "double")
	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	@Column(name = "ACTIVE", columnDefinition = "tinyint(1)")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String toString() {
		return "UomConversion [uomFrom=" + uomFrom + ", uomTo=" + uomTo
				+ ", name=" + name + ", value=" + value + ", active=" + active
				+ "]";
	}
}
