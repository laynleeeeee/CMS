package eulap.eb.service.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.RPurchaseOrderDao;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.jr.EBDataSource;
import eulap.eb.service.jr.EBJRServiceHandler;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.web.dto.BmsTrackerReportDto;
import net.sf.jasperreports.engine.JRDataSource;

/**
 * Service class that will handle business logic for BMS tracker report

 */

@Service
public class BmsTrackerReportService {
	@Autowired
	private RPurchaseOrderDao rpoDao;
	@Autowired
	private WorkflowServiceHandler workflowHandler;
	private static String CHECK_STATUS = "ApPayment1";

	/**
	 * Generate the data source of BMS Tracker Report.
	 */
	public JRDataSource generateBmsTracker(Integer companyId, Integer divisionId, Integer typeId, String bmsNo,
			Date poDateFrom, Date poDateTo, Date invoiceDateFrom, Date invoiceDateTo, Integer statusId) {
		EBJRServiceHandler<BmsTrackerReportDto> handler = new BmsTrackerJRHandler(companyId, divisionId, typeId,
				bmsNo, poDateFrom, poDateTo, invoiceDateFrom, invoiceDateTo, statusId, this);
		return new EBDataSource<BmsTrackerReportDto>(handler);
	}

	private static class BmsTrackerJRHandler implements EBJRServiceHandler<BmsTrackerReportDto> {
		private Integer companyId;
		private Integer divisionId;
		private Integer typeId;
		private String bmsNo;
		private Date poDateFrom;
		private Date poDateTo;
		private Date invoiceDateFrom;
		private Date invoiceDateTo;
		private Integer statusId;
		private BmsTrackerReportService btrService;

		private BmsTrackerJRHandler(Integer companyId, Integer divisionId, Integer typeId, String bmsNo,
				Date poDateFrom, Date poDateTo, Date invoiceDateFrom, Date invoiceDateTo, Integer statusId,
				BmsTrackerReportService btrService) {
			this.companyId = companyId;
			this.divisionId=divisionId;
			this.typeId= typeId;
			this.bmsNo= bmsNo;
			this.poDateFrom = poDateFrom;
			this.poDateTo = poDateTo;
			this.invoiceDateFrom = invoiceDateFrom;
			this.invoiceDateTo = invoiceDateTo;
			this.statusId = statusId;
			this.btrService = btrService;
		}

		@Override
		public void close() throws IOException {
			btrService = null;
		}

		@Override
		public Page<BmsTrackerReportDto> nextPage(PageSetting pageSetting) {
			return btrService.rpoDao.getBmsTrackerData(companyId, divisionId, typeId, bmsNo,
					poDateFrom, poDateTo, invoiceDateFrom, invoiceDateTo, statusId, pageSetting);
		}
	}

	/**
	 * Get all enabled status for AP Payment.
	 * @param user The user object.
	 * @return The list of form statuses enabled for AP payment.
	 */
	public List<FormStatus> getFormStatuses(User user) throws ConfigurationException {
		List<FormStatus> checkStatus = workflowHandler.getAllStatuses(CHECK_STATUS, user, false);
		List<FormStatus> ret = new ArrayList<FormStatus>();
		for (FormStatus formStatus : checkStatus) {
			if (formStatus.getId() == FormStatus.STALED_ID) {
				continue;
			}
			ret.add(formStatus);
		}
		return ret;
	}
}

