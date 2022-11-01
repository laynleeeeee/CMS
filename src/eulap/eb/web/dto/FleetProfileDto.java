package eulap.eb.web.dto;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import eulap.eb.domain.hibernate.FleetProfile;
import eulap.eb.domain.hibernate.ReferenceDocument;

/**
 * Data transfer object for {@link FleetProfile}

 *
 */
public class FleetProfileDto {
	private Integer fpEbObjectId;
	private String referenceDocumentsJson;
	private List<ReferenceDocument> referenceDocuments;
	private String referenceDocsMessage;

	public Integer getFpEbObjectId() {
		return fpEbObjectId;
	}

	public void setFpEbObjectId(Integer fpEbObjectId) {
		this.fpEbObjectId = fpEbObjectId;
	}

	public String getReferenceDocumentsJson() {
		return referenceDocumentsJson;
	}

	public void setReferenceDocumentsJson(String referenceDocumentsJson) {
		this.referenceDocumentsJson = referenceDocumentsJson;
	}

	public List<ReferenceDocument> getReferenceDocuments() {
		return referenceDocuments;
	}

	public void setReferenceDocuments(List<ReferenceDocument> referenceDocuments) {
		this.referenceDocuments = referenceDocuments;
	}

	public void serializeReferenceDocuments (){
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		referenceDocumentsJson = gson.toJson(referenceDocuments);
	}

	public void deserializeReferenceDocuments () {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<ReferenceDocument>>(){}.getType();
		referenceDocuments = gson.fromJson(referenceDocumentsJson, type);
	}

	public String getReferenceDocsMessage() {
		return referenceDocsMessage;
	}

	public void setReferenceDocsMessage(String referenceDocsMessage) {
		this.referenceDocsMessage = referenceDocsMessage;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FleetProfileDto [fpEbObjectId=").append(fpEbObjectId).append(", referenceDocumentsJson=")
				.append(referenceDocumentsJson).append(", referenceDocuments=").append(referenceDocuments)
				.append(", refDocsMsg=").append(referenceDocsMessage).append("]");
		return builder.toString();
	}
}
