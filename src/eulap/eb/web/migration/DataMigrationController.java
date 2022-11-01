package eulap.eb.web.migration;

import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eulap.eb.service.migration.MigrationService;

/**
 * Controller class to read data from an excel file then save to the database.

 *
 */
@Controller
@RequestMapping ("/migrateData")
public class DataMigrationController {
	@Autowired
	private MigrationService migrationService;

	@RequestMapping(method = RequestMethod.GET)
	public void migrateData (HttpServletResponse response) throws InvalidFormatException, IOException {
		String strFile = "/tmp/migration_template_v2.xls";
		OutputStreamWriter writer = null;
		try {
			writer = new OutputStreamWriter(response.getOutputStream());
			migrationService.migrateData(strFile, writer);
		} finally {
			if (writer != null)
				writer.close();
        }
	}
}
