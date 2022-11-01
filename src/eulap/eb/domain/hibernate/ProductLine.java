package eulap.eb.domain.hibernate;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import eulap.common.domain.BaseDomain;

/**
 * A class that represents the PRODUCT_LINE table.

 * 
 */
@Entity
@Table (name="PRODUCT_LINE")
public class ProductLine extends BaseDomain {
	private Integer mainItemId;
	private boolean menu;
	private boolean active;
	private String mainItemName;
	List<ProductLineItem> productLineItems;
	private String errorMessage;
	private String productLineItemsJson;
	private Item mainItem;

	public enum FIELD {id, mainItemId, menu, active};

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "PRODUCT_LINE_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Override
	@Column(name = "CREATED_BY", columnDefinition = "int(10)")
	public int getCreatedBy() {
		return super.getCreatedBy();
	}

	@Override
	@Column(name = "CREATED_DATE", columnDefinition = "timestamp")
	public Date getCreatedDate() {
		return super.getCreatedDate();
	}

	@Override
	@Column(name = "UPDATED_BY", columnDefinition = "int(10)")
	public int getUpdatedBy() {
		return super.getUpdatedBy();
	}

	@Override
	@Column(name = "UPDATED_DATE", columnDefinition = "timestamp")
	public Date getUpdatedDate() {
		return super.getUpdatedDate();
	}

	@Column(name = "MAIN_ITEM_ID", columnDefinition = "INT(10)")
	public Integer getMainItemId() {
		return mainItemId;
	}

	public void setMainItemId(Integer mainItemId) {
		this.mainItemId = mainItemId;
	}

	@Column(name = "MENU", columnDefinition = "tinyint(1)")
	public boolean isMenu() {
		return menu;
	}

	public void setMenu(boolean menu) {
		this.menu = menu;
	}

	@Column(name = "ACTIVE", columnDefinition = "tinyint(1)")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Transient
	public String getMainItemName() {
		return mainItemName;
	}

	public void setMainItemName(String mainItemName) {
		this.mainItemName = mainItemName;
	}

	@Transient
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Transient
	public String getProductLineItemsJson() {
		return productLineItemsJson;
	}

	public void setProductLineItemsJson(String productLineItemsJson) {
		this.productLineItemsJson = productLineItemsJson;
	}

	@Transient
	public void serializeProductLineItems() {
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		productLineItemsJson = gson.toJson(productLineItems);
	}

	public void deSerializeProductLineItems() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<ProductLineItem>>(){}.getType();
		productLineItems = gson.fromJson(productLineItemsJson, type);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProductLine [mainItemId=").append(mainItemId).append(", active=").append(active)
				.append(", getId()=").append(getId()).append("]");
		return builder.toString();
	}

	@ManyToOne
	@JoinColumn(name = "MAIN_ITEM_ID", insertable=false, updatable=false)
	public Item getMainItem() {
		return mainItem;
	}

	public void setMainItem(Item mainItem) {
		this.mainItem = mainItem;
	}

	@OneToMany
	@JoinColumn(name = "MAIN_ITEM_ID", insertable=false, updatable=false)
	public List<ProductLineItem> getProductLineItems() {
		return productLineItems;
	}

	public void setProductLineItems(List<ProductLineItem> productLineItems) {
		this.productLineItems = productLineItems;
	}
}
