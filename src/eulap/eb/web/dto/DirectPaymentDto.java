package eulap.eb.web.dto;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import eulap.eb.domain.hibernate.ApPayment;
import eulap.eb.domain.hibernate.DirectPayment;
import eulap.eb.domain.hibernate.ReferenceDocument;

/**
 * Data transfer object for direct payment form.

 *
 */
public class DirectPaymentDto {
	private String paymentType;
	private String termName;
	private DirectPayment directPayment;
	private ApPayment payment;
	private List<ReferenceDocument> referenceDocuments;
	private String referenceDocumentsJson;
	private String referenceDocsMessage;
	private List<ApInvoiceDto> apInvoiceDtos;

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getTermName() {
		return termName;
	}

	public void setTermName(String termName) {
		this.termName = termName;
	}

	public DirectPayment getDirectPayment() {
		return directPayment;
	}

	public void setDirectPayment(DirectPayment directPayment) {
		this.directPayment = directPayment;
	}

	public ApPayment getPayment() {
		return payment;
	}

	public void setPayment(ApPayment payment) {
		this.payment = payment;
	}

	@Override
	public String toString() {
		return "DirectPaymentDto [directPayment=" + directPayment + ", payment=" + payment + "]";
	}

	public List<ReferenceDocument> getReferenceDocuments() {
		return referenceDocuments;
	}

	public void setReferenceDocuments(List<ReferenceDocument> referenceDocuments) {
		this.referenceDocuments = referenceDocuments;
	}

	public String getReferenceDocumentsJson() {
		return referenceDocumentsJson;
	}

	public void setReferenceDocumentsJson(String referenceDocumentsJson) {
		this.referenceDocumentsJson = referenceDocumentsJson;
	}

	public String getReferenceDocsMessage() {
		return referenceDocsMessage;
	}

	public void setReferenceDocsMessage(String referenceDocsMessage) {
		this.referenceDocsMessage = referenceDocsMessage;
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

	public List<ApInvoiceDto> getApInvoiceDtos() {
		return apInvoiceDtos;
	}

	public void setApInvoiceDtos(List<ApInvoiceDto> apInvoiceDtos) {
		this.apInvoiceDtos = apInvoiceDtos;
	}

}
