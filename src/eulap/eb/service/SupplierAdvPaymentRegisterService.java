package eulap.eb.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.FormStatusDao;
import eulap.eb.dao.SupplierAdvPaymentDao;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.jr.EBDataSource;
import eulap.eb.service.jr.EBJRServiceHandler;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.web.dto.SupplierAdvPaymentRegstrDto;
import net.sf.jasperreports.engine.JRDataSource;

/**
 * Business logic for generating the Supplier advance payment register.

 */

@Service
public class SupplierAdvPaymentRegisterService {
	@Autowired
	private FormStatusDao formStatusDao;
	@Autowired
	private WorkflowServiceHandler workflowServiceHandler;
	@Autowired
	private SupplierAdvPaymentDao sapDao;
	private static final Logger logger = Logger.getLogger(SupplierAdvPaymentRgstrHandler.class);

	/**
	 * Get the supplier advance payment register report data
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @param supplierId The supplier id
	 * @param supplierAcctId The supplier account id
	 * @param bmsNumber The BMS number
	 * @param dateFrom The start date
	 * @param dateTo The end date
	 * @param status The form status
	 * @return
	 */
	public JRDataSource generateSupplierAdvancePaymentRegister(int companyId, int divisionId, int supplierId,
			int supplierAcctId, String bmsNumber, Date dateFrom, Date dateTo, int status) {
		logger.info("Retrieve supplier advance payment register report data");
		EBJRServiceHandler<SupplierAdvPaymentRegstrDto> handler = new SupplierAdvPaymentRgstrHandler(companyId,
				divisionId, supplierId, supplierAcctId, bmsNumber, dateFrom, dateTo, status, sapDao);
		return new EBDataSource<SupplierAdvPaymentRegstrDto>(handler);
	}

	private static class SupplierAdvPaymentRgstrHandler implements EBJRServiceHandler<SupplierAdvPaymentRegstrDto> {
		private int companyId;
		private int divisionId;
		private int supplierId;
		private int supplierAcctId;
		private String bmsNumber;
		private Date dateFrom;
		private Date dateTo;
		private Integer status;
		private SupplierAdvPaymentDao sapDao;

		private SupplierAdvPaymentRgstrHandler(int companyId, int divisionId, int supplierId, int supplierAcctId,
				String bmsNumber, Date dateFrom, Date dateTo, Integer status, SupplierAdvPaymentDao sapDao) {
			this.companyId = companyId;
			this.divisionId = divisionId;
			this.supplierId = supplierId;
			this.supplierAcctId = supplierAcctId;
			this.bmsNumber = bmsNumber;
			this.dateFrom = dateFrom;
			this.dateTo = dateTo;
			this.status = status;
			this.sapDao = sapDao;
		}

		@Override
		public void close() throws IOException {
			sapDao = null;
		}

		@Override
		public Page<SupplierAdvPaymentRegstrDto> nextPage(PageSetting pageSetting) {
			Page<SupplierAdvPaymentRegstrDto>sapRegisterData = sapDao.getSupplierAdvPaymentRgstr(companyId, divisionId, supplierId,
					supplierAcctId, bmsNumber, dateFrom, dateTo, status, pageSetting);
			return sapRegisterData;
		}
	}

	/**
	 * Get the supplier advance payment statuses.
	 * @param user The user logged.
	 * @return The supplier advance payment statuses.
	 */
	public List<FormStatus> getTransactionStatuses(User user) {
		List<FormStatus> statuses = new ArrayList<FormStatus>();
		FormStatus cancelledStatus = formStatusDao.get(FormStatus.CANCELLED_ID);
		statuses = workflowServiceHandler.getAllStatuses("SupplierAdvancePayment1", user, false);
		statuses.add(cancelledStatus);
		return statuses;
	}
}
