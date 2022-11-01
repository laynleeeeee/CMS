package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * Domain object of KEY_CODE import eulap.common.domain.BaseDomain;
table in the database.

 *
 */
@Entity
@Table (name="KEY_CODE")
public class KeyCode extends BaseDomain {
	private String keyCode;
	private Integer userId;
	private boolean used;
	private boolean active;

	public enum FIELD {id, keyCode, userId, used, active}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "KEY_CODE_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name = "KEY_CODE", columnDefinition="varchar(25)")
	public String getKeyCode() {
		return keyCode;
	}

	public void setKeyCode(String keyCode) {
		this.keyCode = keyCode;
	}

	@Column(name = "USER_ID", columnDefinition="int(10)")
	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	@Column(name = "IS_USED", columnDefinition="tinyint(1)")
	public boolean isUsed() {
		return used;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}

	@Column(name = "ACTIVE", columnDefinition="tinyint(1)")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("KeyCode [keyCode=");
		builder.append(keyCode);
		builder.append(", userId=");
		builder.append(userId);
		builder.append(", used=");
		builder.append(used);
		builder.append(", active=");
		builder.append(active);
		builder.append("]");
		return builder.toString();
	}

}
