package eulap.eb.service.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.SupplierAdvPaymentDao;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.jr.EBDataSource;
import eulap.eb.service.jr.EBJRServiceHandler;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.web.dto.SupplierAdvancePaymentAgingDto;
import net.sf.jasperreports.engine.JRDataSource;

/**
 * Business logic for generating report for the supplier advance payment aging.

 */

@Service
public class SupplierAdvancePaymentAgingService{
	private final Logger logger = Logger.getLogger(SupplierAdvancePaymentAgingService.class);
	@Autowired
	private SupplierAdvPaymentDao supplierAdvancePaymentDao;
	@Autowired
	private WorkflowServiceHandler workflowServiceHandler;
	private static final String SUPPLIER_ADV_PAYMENT = "ApPayment1";

	/**
	 * Generate Jasper Report for Supplier Advance Payment Aging.
	 * @param param The Supplier Advance Payment Aging parameter.
	 * @return Generated Jasper Report for Supplier Advance Payment Aging.
	 */
	public JRDataSource generateSupplierAdvancePaymentAging(SupplierAdvancePaymentAgingParam param) {
		EBJRServiceHandler<SupplierAdvancePaymentAgingDto> handler = new JRSupplierAdvancePaymentAgingHandler(param, this);
		return new EBDataSource<SupplierAdvancePaymentAgingDto>(handler);
	}

	private static class JRSupplierAdvancePaymentAgingHandler implements EBJRServiceHandler<SupplierAdvancePaymentAgingDto> {
		private SupplierAdvancePaymentAgingParam param;
		private SupplierAdvancePaymentAgingService agingService;
		private JRSupplierAdvancePaymentAgingHandler (SupplierAdvancePaymentAgingParam param,
				SupplierAdvancePaymentAgingService agingService){
			this.param = param;
			this.agingService = agingService;
		}

		@Override
		public void close() throws IOException {
			agingService = null;
		}

		@Override
		public Page<SupplierAdvancePaymentAgingDto> nextPage(PageSetting pageSetting) {
			agingService.logger.info("Generating the Supplier Advance Payment Aging Report.");
			Page<SupplierAdvancePaymentAgingDto> supplierAdvancePaymentAgingData = agingService.supplierAdvancePaymentDao
					.generateSupplierAdvancePaymentAging(param, pageSetting);
			agingService.logger.info("Successfully processed "+supplierAdvancePaymentAgingData.getTotalRecords()+" data for Supplier Advance Payment Aging.");
			return supplierAdvancePaymentAgingData;
		}
	}

	/**
	 * Get the statuses of the forms.
	 * @param user The logged in user.
	 * @return The list of {@link FormStatus}.
	 */
	public List<FormStatus> getFormStatuses(User user) {
		List<FormStatus>supplierStatus = workflowServiceHandler.getAllStatuses(SUPPLIER_ADV_PAYMENT, user , false);

		// Set is a unique collection.
		Set<FormStatus> statuses = new HashSet<FormStatus>();
		statuses.addAll(supplierStatus);

		FormStatus cancelled = new FormStatus();
		cancelled.setDescription(FormStatus.CANCELLED_LABEL);
		cancelled.setId(FormStatus.CANCELLED_ID);
		cancelled.setSelected(true);
		statuses.add(cancelled);
		return new ArrayList<FormStatus>(statuses);
	}
}