package eulap.eb.web.dto;

import java.io.Serializable;

/**
 * Reference document dto.

 */
public class ReferenceDocumentDTO  implements Serializable{

	private static final long serialVersionUID = 5092804164580417837L;
	private Integer id;
	private String fileName;
	private Double fileSize;
	private String description;
	private String file;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Double getFileSize() {
		return fileSize;
	}

	public void setFileSize(Double fileSize) {
		this.fileSize = fileSize;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ReferenceDocumentDTO [id=").append(id).append(", fileName=").append(fileName)
				.append(", fileSize=").append(fileSize).append(", description=").append(description).append(", file=")
				.append(file).append("]");
		return builder.toString();
	}
}
