package eulap.eb.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.PettyCashVoucherDao;
import eulap.eb.web.dto.PettyCashVoucherDto;

/**
 * A class that handles the business logic of petty cash voucher register.

 */
@Service
public class PettyCashVoucherRegisterService {
	@Autowired
	private PettyCashVoucherDao pettyCashVoucherDao;

	/**
	 * Retrive the petty cash voucher register report data.
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @param custodianId The custodian id.
	 * @param requestor The requestor.
	 * @param dateFrom The start date.
	 * @param dateTo The end date.
	 * @param statusId The transaction current status id.
	 * @return The petty cash voucher register report data.
	 */
	public List<PettyCashVoucherDto> generateReport(int companyId, int divisionId, int custodianId,
			String requestor, Date dateFrom, Date dateTo, int statusId) {
		Page<PettyCashVoucherDto> dtos = pettyCashVoucherDao.getPettyCashVoucherRegisterData(companyId,
				divisionId, custodianId, requestor, dateFrom, dateTo, statusId, new PageSetting(PageSetting.START_PAGE, PageSetting.NO_PAGE_CONSTRAINT));
		return new ArrayList<PettyCashVoucherDto>(dtos.getData());
	}
}
