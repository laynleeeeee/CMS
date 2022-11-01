package eulap.common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


import com.csvreader.CsvReader;

/**
 * CVS (Comma Separated values) parser
 * 

 * 
 */
public class CSVParser {
	private static final String UTF_8 ="UTF-8"; 
	/**
	 * Parse the cvs file.
	 * 
	 * @param file The cvs file
	 * @throws FileNotFoundException 
	 */
	public static void parse(File file, ParseDataHandler parsedHandler) throws FileNotFoundException {
		parse(new FileInputStream(file), parsedHandler);
	}

	/**
	 * Parse the csv file.
	 * @param is The input file
	 * @param parsedHandler The parse handler
	 */
	public static void parse (InputStream is, ParseDataHandler parsedHandler){
		BufferedReader csvFile = null;
		InputStreamReader InputReader = null;
		try {
			InputReader = new InputStreamReader(is, UTF_8);
			csvFile = new BufferedReader(InputReader);
			String dataRow = csvFile.readLine(); // Read first line.
			while (dataRow != null) {
				CsvReader reader = CsvReader.parse(dataRow);
				reader.readRecord();
				parsedHandler.handleData(reader);
				dataRow = csvFile.readLine(); // Read next line of data.
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (csvFile != null)
				try {
					csvFile.close();
					parsedHandler.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
		}
	}
}
