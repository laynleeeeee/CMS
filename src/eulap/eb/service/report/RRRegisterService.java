package eulap.eb.service.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.RReceivingReportDao;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.RReceivingReport;
import eulap.eb.service.jr.EBDataSource;
import eulap.eb.service.jr.EBJRServiceHandler;
import eulap.eb.web.dto.RrRegisterDto;
import net.sf.jasperreports.engine.JRDataSource;
/**
 * Receiving Report Register Service.

 *
 */
@Service
public class RRRegisterService {
	@Autowired
	private RReceivingReportDao receivingReportDao;
	@Autowired
	private InvoiceRegisterService invoiceRegisterService;
	@Autowired
	private InvoiceRegisterServiceImpl invoiceRegImpl;

	private static double CANCELLED_AMOUNT = 0.0;

	/**
	 * Get the receiving report register report data
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @param warehouseId The warehouse id
	 * @param supplierId The supplier id
	 * @param supplierAcctId The supplier account id
	 * @param dateFrom The start date
	 * @param dateTo The end date
	 * @param termId The term id
	 * @param amountFrom The start amount basis
	 * @param amountTo The end amount basis
	 * @param statusId The form status id
	 * @param paymentStatId The payment status id
	 * @return The receiving report register report data
	 */
	public JRDataSource getRrRegisterData(Integer companyId, Integer divisionId, Integer warehouseId, Integer supplierId,
			Integer supplierAcctId, Date dateFrom, Date dateTo, Integer termId, Double amountFrom, Double amountTo,
			Integer statusId, Integer paymentStatId) {
		EBJRServiceHandler<RrRegisterDto> handler = new JRRHandler(companyId, divisionId, warehouseId, supplierId,
				supplierAcctId, dateFrom, dateTo, termId, amountFrom, amountTo, statusId, paymentStatId, this);
		return new EBDataSource<RrRegisterDto>(handler);
	}

	private static class JRRHandler implements EBJRServiceHandler<RrRegisterDto> {
		private int companyId;
		private int divisionId;
		private int warehouseId;
		private int supplierId;
		private int supplierAcctId;
		private Date dateFrom;
		private Date dateTo;
		private int termId;
		private Double amountFrom;
		private Double amountTo;
		private int statusId;
		private Integer paymentStatId;
		private RRRegisterService rrRegisterService;

		private JRRHandler (Integer companyId, Integer divisionId,
				Integer warehouseId, Integer supplierId, Integer supplierAcctId,
				Date dateFrom, Date dateTo, Integer termId, Double amountFrom,
				Double amountTo, Integer statusId, Integer paymentStatId,
				RRRegisterService rrRegisterService){
			this.companyId = companyId;
			this.divisionId = divisionId;
			this.warehouseId = warehouseId;
			this.supplierId = supplierId;
			this.supplierAcctId = supplierAcctId;
			this.dateFrom = dateFrom;
			this.dateTo = dateTo;
			this.termId = termId;
			this.amountFrom = amountFrom;
			this.statusId = statusId;
			this.amountTo = amountTo;
			this.paymentStatId = paymentStatId;
			this.rrRegisterService = rrRegisterService;
		}

		@Override
		public void close() throws IOException {
			rrRegisterService = null;
		}

		@Override
		public Page<RrRegisterDto> nextPage(PageSetting pageSetting) {
			return rrRegisterService.receivingReportDao.getRrRegisterData(companyId, divisionId, warehouseId, supplierId,
					supplierAcctId, dateFrom, dateTo, termId, amountFrom, amountTo, statusId, paymentStatId, pageSetting);
		}
	}
}
