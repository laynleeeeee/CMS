package eulap.eb.web.dto;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import eulap.eb.domain.hibernate.EeEmergencyContact;
import eulap.eb.domain.hibernate.EeLicenseCertificate;
import eulap.eb.domain.hibernate.EeNationalCompetency;
import eulap.eb.domain.hibernate.EeSeminarAttended;
import eulap.eb.domain.hibernate.EmployeeProfile;

/**
 * Data transfer object for Employee Profile for GVCH.

 */

public class GvchEmployeeProfileDto {
	private EmployeeProfile employeeProfile;
	private List<EeLicenseCertificate> eeLicenseCertificates;
	private String eeLicenseCertificateJson;
	private List<EeNationalCompetency> eeNationalCompetencies;
	private String eeNationalCompetencyJson;
	private List<EeSeminarAttended> eeSeminarsAttended;
	private String eeSeminarAttendedJson;
	private List<EeEmergencyContact> eeEmergencyContacts;
	private String eeEmergencyContactJson;
	private String certificates;
	private String nationalcompetencies;
	private String lengthOfService;

	public final static int EE_LIC_CERT_OR_TYPE = 29;
	public final static int EE_SEMINAR_OR_TYPE = 30;
	public final static int EE_EMER_CONTACT_OR_TYPE = 31;
	public final static int EE_NAT_COMP_OR_TYPE = 65;

	public EmployeeProfile getEmployeeProfile() {
		return employeeProfile;
	}

	public void setEmployeeProfile(EmployeeProfile employeeProfile) {
		this.employeeProfile = employeeProfile;
	}

	public List<EeLicenseCertificate> getEeLicenseCertificates() {
		return eeLicenseCertificates;
	}

	public void setEeLicenseCertificates(List<EeLicenseCertificate> eeLicenseCertificates) {
		this.eeLicenseCertificates = eeLicenseCertificates;
	}

	public List<EeNationalCompetency> getEeNationalCompetencies() {
		return eeNationalCompetencies;
	}

	public void setEeNationalCompetencies(List<EeNationalCompetency> eeNationalCompetency) {
		this.eeNationalCompetencies = eeNationalCompetency;
	}

	public List<EeSeminarAttended> getEeSeminarsAttended() {
		return eeSeminarsAttended;
	}

	public void setEeSeminarsAttended(List<EeSeminarAttended> eeSeminarAttendeds) {
		this.eeSeminarsAttended = eeSeminarAttendeds;
	}

	public List<EeEmergencyContact> getEeEmergencyContacts() {
		return eeEmergencyContacts;
	}

	public void setEeEmergencyContacts(List<EeEmergencyContact> eeEmergencyContacts) {
		this.eeEmergencyContacts = eeEmergencyContacts;
	}

	public String getEeLicenseCertificateJson() {
		return eeLicenseCertificateJson;
	}

	public void setEeLicenseCertificateJson(String eeLicenseCertificateJson) {
		this.eeLicenseCertificateJson = eeLicenseCertificateJson;
	}

	public String getEeNationalCompetencyJson() {
		return eeNationalCompetencyJson;
	}

	public void setEeNationalCompetencyJson(String eeNationalCompetencyJson) {
		this.eeNationalCompetencyJson = eeNationalCompetencyJson;
	}

	public String getEeSeminarAttendedJson() {
		return eeSeminarAttendedJson;
	}

	public void setEeSeminarAttendedJson(String eeSeminarAttendedJson) {
		this.eeSeminarAttendedJson = eeSeminarAttendedJson;
	}

	public String getEeEmergencyContactJson() {
		return eeEmergencyContactJson;
	}

	public void setEeEmergencyContactJson(String eeEmergencyContactJson) {
		this.eeEmergencyContactJson = eeEmergencyContactJson;
	}

	public void serializeEeLicenseCertificates() {
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		eeLicenseCertificateJson = gson.toJson(eeLicenseCertificates);
	}

	public void deserializeEeLicenseCertificates() {
		Gson gson = new GsonBuilder().
				excludeFieldsWithoutExposeAnnotation().setDateFormat("MM/dd/yyyy").create();
		Type type = new TypeToken <List<EeLicenseCertificate>>(){}.getType();
		eeLicenseCertificates = gson.fromJson(eeLicenseCertificateJson, type);
	}

	public void serializeEeNationalCompetencies() {
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		eeNationalCompetencyJson = gson.toJson(eeNationalCompetencies);
	}

	public void deserializeEeNationalCompetencies() {
		Gson gson = new GsonBuilder().
				excludeFieldsWithoutExposeAnnotation().setDateFormat("MM/dd/yyyy").create();
		Type type = new TypeToken <List<EeNationalCompetency>>(){}.getType();
		eeNationalCompetencies = gson.fromJson(eeNationalCompetencyJson, type);
	}

	public void serializeEeSeminarAttended() {
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		eeSeminarAttendedJson = gson.toJson(eeSeminarsAttended);
	}

	public void deserializeEeSeminarAttended() {
		Gson gson = new GsonBuilder().
				excludeFieldsWithoutExposeAnnotation().setDateFormat("MM/dd/yyyy").create();
		Type type = new TypeToken <List<EeSeminarAttended>>(){}.getType();
		eeSeminarsAttended = gson.fromJson(eeSeminarAttendedJson, type);
	}

	public void serializeEeEmergencyContact() {
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		eeEmergencyContactJson = gson.toJson(eeEmergencyContacts);
	}

	public void deserializeEeEmergencyContact() {
		Gson gson = new GsonBuilder().
				excludeFieldsWithoutExposeAnnotation().setDateFormat("MM/dd/yyyy").create();
		Type type = new TypeToken <List<EeEmergencyContact>>(){}.getType();
		eeEmergencyContacts = gson.fromJson(eeEmergencyContactJson, type);
	}

	public String getCertificates() {
		return certificates;
	}

	public void setCertificates(String certificates) {
		this.certificates = certificates;
	}

	public String getNationalcompetencies() {
		return nationalcompetencies;
	}

	public void setNationalcompetencies(String nationalcompetencies) {
		this.nationalcompetencies = nationalcompetencies;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GvchEmployeeProfileDto [employeeProfile=").append(employeeProfile)
				.append(", eeLicenseCertificates=").append(eeLicenseCertificates)
				.append(", eeLicenseCertificateJson=").append(eeLicenseCertificateJson)
				.append(", eeNationalCompetencies=").append(eeNationalCompetencies)
				.append(", eeNationalCompetencyJson=").append(eeNationalCompetencyJson).append(", eeSeminarsAttended=")
				.append(eeSeminarsAttended).append(", eeSeminarAttendedJson=").append(eeSeminarAttendedJson)
				.append(", eeEmergencyContacts=").append(eeEmergencyContacts).append(", eeEmergencyContactJson=")
				.append(eeEmergencyContactJson).append(", certificates=").append(certificates)
				.append(", nationalcompetencies=").append(nationalcompetencies).append("]");
		return builder.toString();
	}

	public String getLengthOfService() {
		return lengthOfService;
	}

	public void setLengthOfService(String lengthOfService) {
		this.lengthOfService = lengthOfService;
	}
}
