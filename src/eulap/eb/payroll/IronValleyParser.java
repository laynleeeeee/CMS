package eulap.eb.payroll;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import eulap.common.util.DateUtil;
import eulap.eb.domain.hibernate.BiometricModel;

/**
 * The parser for Iron valley biometric.

 *
 */
public class IronValleyParser implements PayrollParser{
	private final int EMP_NAME_COL_INDEX = 1;
	private final int BIOMETRIC_COL_INDEX = 2;
	private final int DATETIME_COL_INDEX = 3;

	@Override
	public void init() {
	}

	@Override
	public int getBiometricModelId() {
		return BiometricModel.IRONVALLEY;
	}

	@Override
	public void parseData(Date dateFrom, Date dateTo, InputStream in, PayrollDataHandler dataHandler) throws IOException, ParseException {
		try {
			Workbook workBook = WorkbookFactory.create(in);
			Sheet sheet = workBook.getSheetAt(0); // The first tab.
			Iterator<Row> rowIterator = sheet.iterator();
			String cellValue = null;
			Integer biometricId = null;
			String employeeName = null;
			Date date = null;
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				if (row.getRowNum() == 0) {
					continue;
				}
				Iterator<Cell> cellIterator = row.cellIterator();
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					cellValue = ExcelParserUtil.getCellValue(cell);
					if (cell.getColumnIndex() == EMP_NAME_COL_INDEX) {
						employeeName = cellValue;
					} else if (cell.getColumnIndex() == BIOMETRIC_COL_INDEX) {
						biometricId = Integer.parseInt(cellValue);
					} else if (cell.getColumnIndex() == DATETIME_COL_INDEX) {
						date = formatter.parse(cellValue);
						Date dtNoTime = DateUtil.removeTimeFromDate(date);
						if ((dtNoTime.equals(dateFrom) || dtNoTime.after(dateFrom)) && 
								(dtNoTime.equals(dateTo) || dtNoTime.before(dateTo))) {
							EmployeeDtr dtr = EmployeeDtr.getInstanceOf(null, biometricId, employeeName, date);
							dataHandler.handleParsedData(dtr);
							date = null;
							biometricId = null;
							employeeName = null;
						}
					}
				}
			}
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		}
	}

	public static void main (String args[]) throws ParseException {
		File file = new File ("../CMS/src/eulap/eb/payroll/ironvalley.xls");
		IronValleyParser parser = new IronValleyParser();
		SimpleDateFormat sdFormat = new SimpleDateFormat("MM/dd/yyyy");
		Date startDate;
		try {
			startDate = sdFormat.parse("03/05/2018");
			Date endDate = sdFormat.parse("12/31/2018");
			PayrollDataHandler dataHandler = new PayrollDataHandler();
			InputStream is = new FileInputStream(file);
			parser.parseData(startDate, endDate, is, dataHandler);
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
	}
}
