package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.google.gson.annotations.Expose;

import eulap.common.domain.BaseDomain;
/**
 * A class that represents the USER_CUSTODIAN table.

 * 
 */
@Entity
@Table (name="USER_CUSTODIAN_LINES")
public class UserCustodianLines extends BaseDomain{
	@Expose
	private Integer userCustodianId;
	@Expose
	private Integer userId;
	private User user;
	@Expose
	private String userName;

	public enum FIELD {userCustodianId, userId}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "USER_CUSTODIAN_LINES_ID", columnDefinition = "int(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name = "USER_CUSTODIAN_ID", columnDefinition = "int(10)")
	public Integer getUserCustodianId() {
		return userCustodianId;
	}
	public void setUserCustodianId(Integer userCustodianId) {
		this.userCustodianId = userCustodianId;
	}

	@Column(name = "USER_ID", columnDefinition = "int(10)")
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	@OneToOne
	@JoinColumn(name = "USER_ID", insertable=false, updatable=false)
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}

	@Transient
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public String toString() {
		return "UserCustodianLines [userCustodianId=" + userCustodianId + ", userId=" + userId + ", userName="
				+ userName + "]";
	}
}
