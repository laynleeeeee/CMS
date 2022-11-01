package eulap.eb.domain.hibernate;

import java.lang.reflect.Type;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import eulap.common.domain.BaseDomain;
import eulap.eb.web.dto.ItemCategoryAccountSetupDto;

/**
 * A class that represents ITEM_CATEGORY in the CBS database.

 *
 */
@Entity
@Table (name="ITEM_CATEGORY")
public class ItemCategory extends BaseDomain {
	public static final int SPAREPARTS = 1;
	public static final int OIL_AND_LUBRICANT = 2;
	public static final int TIRES_AND_TUBES = 3;
	public static final int SHOP_SUPPLIES = 4;
	public static final int TRANSPO_EQUIP = 5;
	public static final int BATTERY_COMPO = 6;
	public static final int SHOP_EQUIPMENT = 7;
	public static final int FUEL = 8;
	public static final int MAX_NAME = 50;

	private String name;
	private boolean active;
	private List<ItemCategoryAccountSetup> accountSetups;
	private String accountSetupsJson;
	private String errorMessage;
	List<ItemCategoryAccountSetupDto> accountSetupDtos;

	public enum FIELD {id, name, active}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "ITEM_CATEGORY_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	/**
	 * Get the name of item category.
	 * @return The name.
	 */
	@Column (name = "NAME", columnDefinition="varchar(50)")
	public String getName() {
		return name;
	}

	/**
	 * Set the name of item category.
	 * @param name The name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Check if the item category is active.
	 * @return True if active, otherwise false.
	 */
	@Column (name = "ACTIVE", columnDefinition="tinyint(1)")
	public boolean isActive() {
		return active;
	}

	/**
	 * Set the item category to either true or false.
	 * @param active True or false.
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	@OneToMany
	@JoinColumn (name = "ITEM_CATEGORY_ACCOUNT_SETUP_ID", insertable=false, updatable=false)
	public List<ItemCategoryAccountSetup> getAccountSetups() {
		return accountSetups;
	}

	public void setAccountSetups(List<ItemCategoryAccountSetup> accountSetups) {
		this.accountSetups = accountSetups;
	}

	@Transient
	public String getAccountSetupsJson() {
		return accountSetupsJson;
	}

	public void setAccountSetupsJson(String accountSetupsJson) {
		this.accountSetupsJson = accountSetupsJson;
	}

	@Transient
	public void serializeAccountSetups(){
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		accountSetupsJson = gson.toJson(accountSetups);
	}

	@Transient
	public void deSerializeAccountSetups(){
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<ItemCategoryAccountSetup>>(){}.getType();
		accountSetups = gson.fromJson(accountSetupsJson, type);
	}

	@Override
	public String toString() {
		return "ItemCategory [itemCategoryId=" +  getId() + 
				", name=" + name + ", active=" + active + "]";
	}

	@Transient
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Transient
	public List<ItemCategoryAccountSetupDto> getAccountSetupDtos() {
		return accountSetupDtos;
	}

	public void setAccountSetupDtos(List<ItemCategoryAccountSetupDto> accountSetupDtos) {
		this.accountSetupDtos = accountSetupDtos;
	}
}
