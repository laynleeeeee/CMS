package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.google.gson.annotations.Expose;

/**
 * Object representation of REFERENCE_DOCUMENT table.

 *
 */
@Entity
@Table(name = "REFERENCE_DOCUMENT")
public class ReferenceDocument extends BaseFormLine{
	@Expose
	private Integer referenceObjectId;
	@Expose
	private String fileName;
	@Expose
	private Double fileSize;
	@Expose
	private String description;
	@Expose
	private String file;
	@Expose
	private String filePath;
	@Expose
	private String fileExtension;

	public static final int MAX_DESCRIPTION = 100;

	public static final int OBJECT_TYPE_ID = 30;
	public static final int OR_TYPE_ID = 9;

	public enum FIELD {
		id, ebObjectId, description, fileName, fileSize, file
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "REFERENCE_DOCUMENT_ID", columnDefinition="int(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name="DESCRIPTION", columnDefinition="varchar(100)")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name="FILE_NAME", columnDefinition="varchar(200)")
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Column(name="FILE_SIZE", columnDefinition="double")
	public Double getFileSize() {
		return fileSize;
	}

	public void setFileSize(Double fileSize) {
		this.fileSize = fileSize;
	}

	@Transient
	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	@Override
	@Transient
	public Integer getRefenceObjectId() {
		return referenceObjectId;
	}

	public void setReferenceObjectId(Integer referenceObjectId) {
		this.referenceObjectId = referenceObjectId;
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return OBJECT_TYPE_ID;
	}

	@Column(name="FILE_PATH", columnDefinition="varchar(100)")
	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	@Transient
	public String getFileExtension() {
		return fileExtension;
	}

	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ReferenceDocument [referenceObjectId=").append(referenceObjectId).append(", fileName=")
				.append(fileName).append(", fileSize=").append(fileSize).append(", description=").append(description)
				.append(", filePath=").append(filePath).append("]");
		return builder.toString();
	}
}
