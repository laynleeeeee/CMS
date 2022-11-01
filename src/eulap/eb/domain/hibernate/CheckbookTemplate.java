package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * Class that represents CHECKBOOK_TEMPLATE table of the database

 */
@Entity
@Table(name = "CHECKBOOK_TEMPLATE")
public class CheckbookTemplate extends BaseDomain {
	private String name;
	private String viewsPropName;
	private Integer maxCharAmountInWords;
	private Integer maxCharName;

	public enum FIELD {
		id, name, viewsPropName
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "CHECKBOOK_TEMPLATE_ID", columnDefinition = "int(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name = "NAME", columnDefinition="VARCHAR(50)")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "VIEWS_PROP_NAME", columnDefinition="VARCHAR(30)")
	public String getViewsPropName() {
		return viewsPropName;
	}

	public void setViewsPropName(String viewsPropName) {
		this.viewsPropName = viewsPropName;
	}

	@Column(name = "MAX_CHAR_AMOUNT_IN_WORDS", columnDefinition="int(3)")
	public Integer getMaxCharAmountInWords() {
		return maxCharAmountInWords;
	}

	public void setMaxCharAmountInWords(Integer maxCharAmountInWords) {
		this.maxCharAmountInWords = maxCharAmountInWords;
	}

	@Column(name = "MAX_CHAR_NAME", columnDefinition="int(3)")
	public Integer getMaxCharName() {
		return maxCharName;
	}

	public void setMaxCharName(Integer maxCharName) {
		this.maxCharName = maxCharName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CheckbookTemplate [name=").append(name).append(", viewsPropName=").append(viewsPropName).append(", getId()=")
				.append(getId()).append("]");
		return builder.toString();
	}
}
