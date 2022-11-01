package eulap.eb.service.bir;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import eulap.common.util.StringFormatUtil;

/**
 * BIR Alphalist writer
 * 

 *
 */
public class AlphalistWriter {
	private List<AlphalistHeader> headers;

	public AlphalistWriter(List<AlphalistHeader> headers) {
		this.headers = headers;
	}

	/**
	 * Write the alphalist data then ZIP it to a file. 
	 * @return the file name. 
	 */
	public Path write2Zip () {
		ZipOutputStream zipOut = null;
		Path path = null;
		try {
			path = Files.createTempFile("zipFile", ".zip");
			zipOut = new ZipOutputStream(new FileOutputStream(path.toFile()));
			for (AlphalistHeader header : headers) {
				Path headerFileName = writerToFile(header);
				ZipEntry e = new ZipEntry("/"+header.getFileName()+".DAT");
				zipOut.putNextEntry(e);
				byte[] readBuffer = new byte[2048];
				int amountRead;
				FileInputStream inputStream = null;
				try {
					inputStream = new FileInputStream(headerFileName.toString());
					while ((amountRead = inputStream.read(readBuffer)) > 0) {
						zipOut.write(readBuffer, 0, amountRead);
					}
				}finally {
					inputStream.close();
					zipOut.closeEntry();
				}
					
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				zipOut.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return path;
	}
	
	/**
	 * Write Alphalist in the file.
	 * @return The return path of the file.
	 */
	private Path writerToFile(AlphalistHeader header) {
		FileWriter file = null;
		BufferedWriter writer = null;
		Path path = null;
		try {
			path = Files.createTempFile(header.getFileName(), ".DAT");
			System.out.println("Temp file : " + path);
			file = new FileWriter(path.toFile());
			writer = new BufferedWriter(file);
			writer.append(StringFormatUtil.stripDiacritics(header.convertHeaderToCSV()));
			for (AlphalistSchedule schedule : header.getSchedules()) {
				writeSchedule(schedule, writer);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				writer.close();
				file.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return path;
	}

	private void writeSchedule (AlphalistSchedule schedule,
			BufferedWriter writer) throws IOException {
		writer.write(System.lineSeparator());
		for (AlphalistDetail detail : schedule.getDetails()) {
			writer.append(StringFormatUtil.stripDiacritics(detail.convertDetailToCSV()));
			writer.write(System.lineSeparator());
		}
		if (schedule.getControl() != null) {
			writer.append(StringFormatUtil.stripDiacritics(schedule.getControl().convertControlToCSV()));
		}
	}
}
