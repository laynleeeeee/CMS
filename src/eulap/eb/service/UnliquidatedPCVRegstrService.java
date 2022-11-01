package eulap.eb.service;

import java.io.IOException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.PettyCashVoucherDao;
import eulap.eb.service.jr.EBDataSource;
import eulap.eb.service.jr.EBJRServiceHandler;
import eulap.eb.web.dto.UnliquidatedPCVAgingDto;
import net.sf.jasperreports.engine.JRDataSource;

/**
 * A class that handles the business logic of unliquidated petty cash voucher register.

 */
@Service
public class UnliquidatedPCVRegstrService {
	@Autowired
	private PettyCashVoucherDao pettyCashVoucherDao;

	/**
	 * Get the Petty Cash Voucher Register report.
	 * @param companyId The company ID
	 * @param divisionId The Division ID
	 * @param custodianId The Custodian ID
	 * @param requestor The requestor ID
	 * @param asOfDate unliquidated pcv as of date.
	 * @param statusId The invoice status ID.
	 * @return The petty cash voucher register report.
	 */
	public JRDataSource generateUnliquidatedPCVAging(int companyId, int divisionId, int custodianId, String requestor, Date asOfDate) {
		EBJRServiceHandler<UnliquidatedPCVAgingDto> handler = new JRRHandler(companyId, divisionId, custodianId, requestor, asOfDate, this);
		return new EBDataSource<UnliquidatedPCVAgingDto>(handler);
	}

	private static class JRRHandler implements EBJRServiceHandler<UnliquidatedPCVAgingDto> {
		private int companyId;
		private int divisionId;
		private int custodianId;
		private String requestor;
		private Date asOfDate;
		private UnliquidatedPCVRegstrService service;

		public JRRHandler(int companyId, int divisionId, int custodianId, String requestor, Date asOfDate,
				UnliquidatedPCVRegstrService service) {
			this.companyId = companyId;
			this.divisionId = divisionId;
			this.custodianId = custodianId;
			this.requestor = requestor;
			this.asOfDate = asOfDate;
			this.service = service;
		}

		@Override
		public void close() throws IOException {
			service = null;
		}

		@Override
		public Page<UnliquidatedPCVAgingDto> nextPage(PageSetting pageSetting) {
			return service.pettyCashVoucherDao.getUnliquidatedPCVAgingData(companyId,
					divisionId, custodianId, requestor, asOfDate, pageSetting);
		}
	}
}
