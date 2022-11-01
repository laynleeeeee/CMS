package eulap.common.dto;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * Class that will be used in uploading data. 

 *
 */
public class UploadItem {
	private String filename;
	private CommonsMultipartFile fileData;

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public CommonsMultipartFile getFileData() {
		return fileData;
	}

	public void setFileData(CommonsMultipartFile fileData) {
		this.fileData = fileData;
	}
}
