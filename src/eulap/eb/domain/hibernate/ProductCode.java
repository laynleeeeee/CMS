package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * A class that define the product and its code.
 * 

 * 
 */
@Entity
@Table(name = "PRODUCT_CODE")
public class ProductCode extends BaseDomain {
	private String productName;
	private int code;

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "PRODUCT_CODE_ID", columnDefinition = "int(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name = "PRODUCT_NAME", columnDefinition = "VARCHAR(50)")
	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	@Column(name = "CODE", columnDefinition = "INT(10)")
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return "ProductCode [productName=" + productName + ", code=" + code
				+ "]";
	}
}
