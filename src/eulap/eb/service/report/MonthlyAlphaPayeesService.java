package eulap.eb.service.report;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.eb.dao.APInvoiceDao;
import eulap.eb.web.dto.MonthlyAlphalistPayeesDto;

/**
 * Class that will handle the business logic for Monthly Alphalist Payees.

 */
@Service
public class MonthlyAlphaPayeesService {
	@Autowired
	private APInvoiceDao apinvoicedao;

	private static final String SCHED_TYPE_ID = "1,2";

	/**
	 * Get the Monthly Alphalist of Payees report data
	 * @param companyId The Company Id
	 * @param divisionId The Division Id
	 * @param year The selected year
	 * @param month The Selected month
	 * @return The Monthly Alphalist of Payees data
	 */
	public List<MonthlyAlphalistPayeesDto> getMAPayees(int companyId, Integer divisionId, Integer fromMonth, Integer toMonth, Integer year ){
		return apinvoicedao.getMAPayees(companyId, divisionId, fromMonth, toMonth, year, SCHED_TYPE_ID);
	}

}