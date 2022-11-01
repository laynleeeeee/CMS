package eulap.eb.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.eb.dao.ArInvoiceDao;
import eulap.eb.web.dto.COCTaxDto;

/**
 * Class that will handle the business logic for generating Certificate of Creditable Tax Withheld at Source Report.

 */
@Service
public class COCTaxService {
	@Autowired
	private ArInvoiceDao arInvoiceDao;

	/**
	 * Generate the COC Taxwitheld at source report data
	 * @param companyId The Company Id
	 * @param divisionId The Division Id
	 * @param dateFrom The transaction date from
	 * @param dateTo The transaction date to
	 * @return The COC Taxwitheld at source data
	 */
	public List<COCTaxDto> generateCOCTaxData(Integer companyId, Integer divisionId, Date dateFrom, Date dateTo ){
		return arInvoiceDao.getCOCTaxData(companyId, divisionId, dateFrom, dateTo);
	}

}