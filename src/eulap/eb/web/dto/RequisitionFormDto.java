package eulap.eb.web.dto;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import eulap.eb.domain.hibernate.OtherChargesLine;
import eulap.eb.domain.hibernate.PurchaseRequisitionItem;
import eulap.eb.domain.hibernate.ReferenceDocument;
import eulap.eb.domain.hibernate.RequisitionForm;
import eulap.eb.domain.hibernate.RequisitionFormItem;

/**
 * Data transfer object for {@code RequisitionForm}

 *
 */
public class RequisitionFormDto {
	private RequisitionForm requisitionForm;
	private String requisitionFormItemsJson;
	private String otherChargesLineJson;
	private String referenceDocumentsJson;
	private String errItemsMsg;
	private String referenceDocsMessage;
	private Integer itemCategoryId;
	private String warehouseName;
	private String prItemsJson;
	private String requisitionTypeName;

	public static final int WS_ITEM_RF_ITEM_OR_TYPE_ID = 73;

	/**
	 * Create an instance of {@code RequisitionFormDto}
	 * @param requisitionForm
	 * @return
	 */
	public static RequisitionFormDto getInstanceOf(RequisitionForm requisitionForm) {
		RequisitionFormDto rfDto = new RequisitionFormDto();
		rfDto.requisitionForm = requisitionForm;
		return rfDto;
	}

	public RequisitionForm getRequisitionForm() {
		return requisitionForm;
	}

	public void setRequisitionForm(RequisitionForm requisitionForm) {
		this.requisitionForm = requisitionForm;
	}

	public String getRequisitionFormItemsJson() {
		return requisitionFormItemsJson;
	}

	public void setRequisitionFormItemsJson(String requisitionFormItemsJson) {
		this.requisitionFormItemsJson = requisitionFormItemsJson;
	}

	public String getOtherChargesLineJson() {
		return otherChargesLineJson;
	}

	public void setOtherChargesLineJson(String otherChargesLineJson) {
		this.otherChargesLineJson = otherChargesLineJson;
	}

	public String getReferenceDocumentsJson() {
		return referenceDocumentsJson;
	}

	public void setReferenceDocumentsJson(String referenceDocumentsJson) {
		this.referenceDocumentsJson = referenceDocumentsJson;
	}

	public String getErrItemsMsg() {
		return errItemsMsg;
	}

	public void setErrItemsMsg(String errItemsMsg) {
		this.errItemsMsg = errItemsMsg;
	}

	public String getReferenceDocsMessage() {
		return referenceDocsMessage;
	}

	public void setReferenceDocsMessage(String referenceDocsMessage) {
		this.referenceDocsMessage = referenceDocsMessage;
	}

	public void serializeRfItems (){
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		requisitionFormItemsJson = gson.toJson(requisitionForm.getRequisitionFormItems());
	}

	public void deserializeRfItems () {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<RequisitionFormItem>>(){}.getType();
		requisitionForm.setRequisitionFormItems(gson.fromJson(requisitionFormItemsJson, type));
	}

	public void serializeRefDocs (){
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		referenceDocumentsJson = gson.toJson(requisitionForm.getReferenceDocuments());
	}

	public void deserializeRefDocs () {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<ReferenceDocument>>(){}.getType();
		requisitionForm.setReferenceDocuments(gson.fromJson(referenceDocumentsJson, type));
	}

	public void serializeOclItems() {
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		otherChargesLineJson = gson.toJson(requisitionForm.getOtherChargesLines());
	}

	public void deserializeOclItems() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<OtherChargesLine>>(){}.getType();
		requisitionForm.setOtherChargesLines(gson.fromJson(otherChargesLineJson, type));
	}

	public Integer getItemCategoryId() {
		return itemCategoryId;
	}

	public void setItemCategoryId(Integer itemCategoryId) {
		this.itemCategoryId = itemCategoryId;
	}

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

	public String getPrItemsJson() {
		return prItemsJson;
	}

	public void setPrItemsJson(String prItemsJson) {
		this.prItemsJson = prItemsJson;
	}

	public void serializePrItems() {
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		prItemsJson = gson.toJson(requisitionForm.getPurchaseRequisitionItems());
	}

	public void deserializePrItems() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<PurchaseRequisitionItem>>(){}.getType();
		requisitionForm.setPurchaseRequisitionItems(gson.fromJson(prItemsJson, type));
	}

	public String getRequisitionTypeName() {
		return requisitionTypeName;
	}

	public void setRequisitionTypeName(String requisitionTypeName) {
		this.requisitionTypeName = requisitionTypeName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RequisitionFormDto [requisitionForm=").append(requisitionForm)
				.append(", requisitionFormItemsJson=").append(requisitionFormItemsJson)
				.append(", otherChargesLineJson=").append(otherChargesLineJson).append(", referenceDocumentsJson=")
				.append(referenceDocumentsJson).append(", errItemsMsg=").append(errItemsMsg)
				.append(", referenceDocsMessage=").append(referenceDocsMessage).append(", itemCategoryId=")
				.append(itemCategoryId).append("]");
		return builder.toString();
	}
}
