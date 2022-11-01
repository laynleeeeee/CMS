package eulap.common.util;

import java.io.Closeable;

import com.csvreader.CsvReader;

/**
 * CVS file parse data handler

 *
 */
public interface ParseDataHandler extends Closeable{
	/**
	 * Handle the row data
	 * 
	 * @param reader row data reader
	 */
	void handleData(CsvReader reader);
}