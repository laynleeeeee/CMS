package eulap.eb.web.dto;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import eulap.eb.domain.hibernate.JyeiWithdrawalSlip;
import eulap.eb.domain.hibernate.ReferenceDocument;
import eulap.eb.domain.hibernate.SerialItem;
import eulap.eb.domain.hibernate.WithdrawalSlip;

/**
 * Data transfer object for JYEI Withdrawal Slip.

 */
public class WithdrawalSlipDto {
	private WithdrawalSlip withdrawalSlip;
	private JyeiWithdrawalSlip jyeiWithdrawalSlip;
	private List<ReferenceDocument> referenceDocuments;
	private String referenceDocumentJson;
	private String referenceDocsMessage;
	private List<SerialItem> serialItems;
	private String serialItemsJson;
	private String siMessage;
	private List<JyeiFormItemDto> wsItemDtos;
	private String fleetPlateNo;

	public WithdrawalSlip getWithdrawalSlip() {
		return withdrawalSlip;
	}

	public void setWithdrawalSlip(WithdrawalSlip withdrawalSlip) {
		this.withdrawalSlip = withdrawalSlip;
	}

	public JyeiWithdrawalSlip getJyeiWithdrawalSlip() {
		return jyeiWithdrawalSlip;
	}

	public void setJyeiWithdrawalSlip(JyeiWithdrawalSlip jyeiWithdrawalSlip) {
		this.jyeiWithdrawalSlip = jyeiWithdrawalSlip;
	}

	public List<ReferenceDocument> getReferenceDocuments() {
		return referenceDocuments;
	}

	public void setReferenceDocuments(List<ReferenceDocument> referenceDocuments) {
		this.referenceDocuments = referenceDocuments;
	}

	public String getReferenceDocumentJson() {
		return referenceDocumentJson;
	}

	public void setReferenceDocumentJson(String referenceDocumentJson) {
		this.referenceDocumentJson = referenceDocumentJson;
	}

	public String getReferenceDocsMessage() {
		return referenceDocsMessage;
	}

	public void setReferenceDocsMessage(String referenceDocsMessage) {
		this.referenceDocsMessage = referenceDocsMessage;
	}

	public void serializeReferenceDocuments(){
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		referenceDocumentJson = gson.toJson(referenceDocuments);
	}

	public void deserializeReferenceDocuments(){
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<ReferenceDocument>>(){}.getType();
		referenceDocuments = gson.fromJson(referenceDocumentJson, type);
	}

	public String getSerialItemsJson() {
		return serialItemsJson;
	}

	public List<SerialItem> getSerialItems() {
		return serialItems;
	}

	public void setSerialItems(List<SerialItem> serialItems) {
		this.serialItems = serialItems;
	}

	public void setSerialItemsJson(String serialItemsJson) {
		this.serialItemsJson = serialItemsJson;
	}

	public String getSiMessage() {
		return siMessage;
	}

	public void setSiMessage(String siMessage) {
		this.siMessage = siMessage;
	}

	public void serializeSerialItems() {
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		serialItemsJson = gson.toJson(serialItems);
	}

	public void deserrializeSerialItems() {
		Gson gson = new GsonBuilder().
				excludeFieldsWithoutExposeAnnotation().setDateFormat("MM/dd/yyyy").create();
		Type type = new TypeToken <List<SerialItem>>(){}.getType();
		serialItems = gson.fromJson(serialItemsJson, type);
	}

	public List<JyeiFormItemDto> getWsItemDtos() {
		return wsItemDtos;
	}

	public void setWsItemDtos(List<JyeiFormItemDto> wsItemDtos) {
		this.wsItemDtos = wsItemDtos;
	}

	public String getFleetPlateNo() {
		return fleetPlateNo;
	}

	public void setFleetPlateNo(String fleetPlateNo) {
		this.fleetPlateNo = fleetPlateNo;
	}
}
