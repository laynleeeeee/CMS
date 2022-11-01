package eulap.eb.web.dto;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.Transient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import eulap.eb.domain.hibernate.GeneralLedger;
import eulap.eb.domain.hibernate.GlEntry;
import eulap.eb.domain.hibernate.ReferenceDocument;

/**
 * General ledger dto.

 *
 */
public class GeneralLedgerDto {
	private int sequenceNo;
	private int glStatusId;
	private int glEntrySourceId;
	private Date glDate;
	private String comment;
	private String jsonData;
	private double difference;
	private int companyId;
	private int divisionId;
	private String divisionName;
	private GeneralLedger generalLedger;
	private GlEntry glEntry;
	private Collection<GlEntry> glEntries;
	private String glEntriesJson;
	private int glEntrySize;
	private boolean isPosting;
	private String gLlineMessage;
	private List<ReferenceDocument> referenceDocuments;
	private String referenceDocumentsJson;
	private String referenceDocsMessage;
	private Integer currencyRateId;
	private Double currencyRateValue;

	public GeneralLedgerDto () {
		// Do nothing. 
	}

	public GeneralLedgerDto (GeneralLedger generalLedger, GlEntry glEntry) {
		this.generalLedger = generalLedger;
		this.setGlEntry(glEntry);
	}

	public GeneralLedgerDto (GeneralLedger generalLedger, Collection<GlEntry> glEntries) {
		this.generalLedger = generalLedger;
		this.glEntries = glEntries;
	}

	public int getSequenceNo() {
		return sequenceNo;
	}

	public void setSequenceNo(int sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

	public int getGlStatusId() {
		return glStatusId;
	}

	public void setGlStatusId(int glStatusId) {
		this.glStatusId = glStatusId;
	}

	public int getGlEntrySourceId() {
		return glEntrySourceId;
	}

	public void setGlEntrySourceId(int glEntrySourceId) {
		this.glEntrySourceId = glEntrySourceId;
	}

	public Date getGlDate() {
		return glDate;
	}

	public void setGlDate(Date glDate) {
		this.glDate = glDate;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getJsonData() {
		return jsonData;
	}

	public void setJsonData(String jsonData) {
		this.jsonData = jsonData;
	}

	public double getDifference() {
		return difference;
	}

	public void setDifference(double difference) {
		this.difference = difference;
	}

	public int getCompanyId() {
		return companyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	public int getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(int divisionId) {
		this.divisionId = divisionId;
	}

	public String getDivisionName() {
		return divisionName;
	}

	public void setDivisionName(String divisionName) {
		this.divisionName = divisionName;
	}

	public GeneralLedger getGeneralLedger() {
		return generalLedger;
	}

	public void setGeneralLedger(GeneralLedger generalLedger) {
		this.generalLedger = generalLedger;
	}

	public GlEntry getGlEntry() {
		return glEntry;
	}

	public void setGlEntry(GlEntry glEntry) {
		this.glEntry = glEntry;
	}

	public Collection<GlEntry> getGlEntries() {
		return glEntries;
	}

	public void setGlEntries(Collection<GlEntry> glEntries) {
		this.glEntries = glEntries;
	}

	public String getGlEntriesJson() {
		return glEntriesJson;
	}

	public void setGlEntriesJson(String glEntriesJson) {
		this.glEntriesJson = glEntriesJson;
	}

	public int getGlEntrySize() {
		return glEntrySize;
	}

	public void setGlEntrySize(int glEntrySize) {
		this.glEntrySize = glEntrySize;
	}

	@Transient
	public boolean isPosting() {
		return isPosting;
	}

	public void setPosting(boolean isPosting) {
		this.isPosting = isPosting;
	}

	@Transient
	public String getgLlineMessage() {
		return gLlineMessage;
	}

	public void setgLlineMessage(String gLlineMessage) {
		this.gLlineMessage = gLlineMessage;
	}

	public void serializeGlLines() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		glEntriesJson = gson.toJson(glEntries);
	}

	public void deserializeGlLines() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken<List<GlEntry>>(){}.getType();
		glEntries = gson.fromJson(glEntriesJson, type);
	}

	@Transient
	public List<ReferenceDocument> getReferenceDocuments() {
		return referenceDocuments;
	}

	public void setReferenceDocuments(List<ReferenceDocument> referenceDocuments) {
		this.referenceDocuments = referenceDocuments;
	}

	@Transient
	public String getReferenceDocumentsJson() {
		return referenceDocumentsJson;
	}

	public void setReferenceDocumentsJson(String referenceDocumentsJson) {
		this.referenceDocumentsJson = referenceDocumentsJson;
	}

	@Transient
	public String getReferenceDocsMessage() {
		return referenceDocsMessage;
	}

	public void setReferenceDocsMessage(String referenceDocsMessage) {
		this.referenceDocsMessage = referenceDocsMessage;
	}

	@Transient
	public void serializeReferenceDocuments() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		referenceDocumentsJson = gson.toJson(referenceDocuments);
	}

	@Transient
	public void deserializeReferenceDocuments() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken<List<ReferenceDocument>>(){}.getType();
		referenceDocuments = gson.fromJson(referenceDocumentsJson, type);
	}

	public Integer getCurrencyRateId() {
		return currencyRateId;
	}

	public void setCurrencyRateId(Integer currencyRateId) {
		this.currencyRateId = currencyRateId;
	}

	public Double getCurrencyRateValue() {
		return currencyRateValue;
	}

	public void setCurrencyRateValue(Double currencyRateValue) {
		this.currencyRateValue = currencyRateValue;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GeneralLedgerDto [sequenceNo=").append(sequenceNo).append(", glStatusId=").append(glStatusId)
				.append(", glEntrySourceId=").append(glEntrySourceId).append(", glDate=").append(glDate)
				.append(", comment=").append(comment).append(", difference=").append(difference).append(", companyId=")
				.append(companyId).append(", divisionId=").append(divisionId).append(", divisionName=")
				.append(divisionName).append(", currencyRateId=").append(currencyRateId).append(", currencyRateValue=")
				.append(currencyRateValue).append("]");
		return builder.toString();
	}
}
