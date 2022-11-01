package eulap.eb.web.dto;

import java.util.List;

import eulap.eb.domain.hibernate.EmployeeLeaveCreditLine;
import eulap.eb.domain.hibernate.ReferenceDocument;

/**
 * Employee Leave DTO.

 *
 */
public class EmployeeLeaveCreditDto {

	private Integer elcId;
	private Integer sequenceNumber;
	private List<EmployeeLeaveCreditLine> employeeLeaveCreditLines;
	private List<ReferenceDocument> referenceDocuments;

	private EmployeeLeaveCreditDto(Integer elcId, Integer sequenceNumber,
			List<EmployeeLeaveCreditLine> employeeLeaveCreditLines, List<ReferenceDocument> referenceDocuments){
		this.elcId = elcId;
		this.sequenceNumber = sequenceNumber;
		this.employeeLeaveCreditLines = employeeLeaveCreditLines;
		this.referenceDocuments = referenceDocuments;
	}

	public static EmployeeLeaveCreditDto getInstanceOf(List<EmployeeLeaveCreditLine> employeeLeaveCreditLines,
			List<ReferenceDocument> referenceDocuments){
		return new EmployeeLeaveCreditDto(null, null, employeeLeaveCreditLines, referenceDocuments);
	}

	public Integer getElcId() {
		return elcId;
	}

	public void setElcId(Integer elcId) {
		this.elcId = elcId;
	}

	public Integer getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(Integer sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public List<EmployeeLeaveCreditLine> getEmployeeLeaveCreditLines() {
		return employeeLeaveCreditLines;
	}

	public void setEmployeeLeaveCreditLines(List<EmployeeLeaveCreditLine> employeeLeaveCreditLines) {
		this.employeeLeaveCreditLines = employeeLeaveCreditLines;
	}

	public List<ReferenceDocument> getReferenceDocuments() {
		return referenceDocuments;
	}

	public void setReferenceDocuments(List<ReferenceDocument> referenceDocuments) {
		this.referenceDocuments = referenceDocuments;
	}

	@Override
	public String toString() {
		return "EmployeeLeaveCreditDto [elcId=" + elcId + ", sequenceNumber=" + sequenceNumber
				+ ", employeeLeaveCreditLines=" + employeeLeaveCreditLines + ", referenceDocuments="
				+ referenceDocuments + "]";
	}
}
