package eulap.eb.service.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.SalesOrderDao;
import eulap.eb.service.jr.EBDataSource;
import eulap.eb.service.jr.EBJRServiceHandler;
import eulap.eb.web.dto.DeliveryStatus;
import eulap.eb.web.dto.SalesOrderRegisterDto;
import net.sf.jasperreports.engine.JRDataSource;

/**
 * Sales Order Register Service.

 *
 */
@Service
public class SalesOrderRegisterService {
	@Autowired
	private SalesOrderDao soDao;
	private static Logger logger = Logger.getLogger(SalesOrderRegisterService.class);

	/**
	 * Generate the data source of SO Register Report.
	 */
	public JRDataSource generateSORegister(Integer companyId, Integer divisionId, Integer arCustomerId,
			Integer arCustomerAccountId, Integer soType, Integer soFrom, Integer soTo, String popcrNo,
			Date dateFrom, Date dateTo, Integer statusId) {
		EBJRServiceHandler<SalesOrderRegisterDto> handler = new SoRegisterJRHandler(companyId, divisionId,
				arCustomerId, arCustomerAccountId, soType, soFrom, soTo, popcrNo, dateFrom, dateTo, statusId, soDao);
		return new EBDataSource<SalesOrderRegisterDto>(handler);
	}

	private static class SoRegisterJRHandler implements EBJRServiceHandler<SalesOrderRegisterDto> {
		private static Logger logger = Logger.getLogger(SoRegisterJRHandler.class);
		private Integer companyId;
		private Integer divisionId;
		private Integer arCustomerId;
		private Integer arCustomerAccountId;
		private Integer soType;
		private Integer soFrom;
		private Integer soTo;
		private String popcrNo;
		private Date dateFrom;
		private Date dateTo;
		private Integer statusId;
		private SalesOrderDao soDao;

		private SoRegisterJRHandler(Integer companyId, Integer divisionId, Integer arCustomerId,
				Integer arCustomerAccountId, Integer soType, Integer soFrom, Integer soTo, String popcrNo,
				Date dateFrom, Date dateTo, Integer statusId, SalesOrderDao soDao) {
			this.companyId = companyId;
			this.divisionId=divisionId;
			this.arCustomerId = arCustomerId;
			this.arCustomerAccountId = arCustomerAccountId;
			this.soType= soType;
			this.soFrom=soFrom;
			this.soTo=soTo;
			this.popcrNo=popcrNo;
			this.dateFrom = dateFrom;
			this.dateTo = dateTo;
			this.statusId = statusId;
			this.soDao = soDao;

		}

		@Override
		public void close() throws IOException {
			soDao = null;
		}

		@Override
		public Page<SalesOrderRegisterDto> nextPage(PageSetting pageSetting) {
			return soDao.getSoRegisterData(companyId, divisionId, arCustomerId, arCustomerAccountId,
					soType, soFrom, soTo, popcrNo, dateFrom, dateTo, statusId, pageSetting);
		}
	}

	/**
	 * Generate the list of {@link DeliveryStatus} for SO Register Report.
	 * @return The list of Delivery Statuses: Partially Served, Fully Served and Completed With Revision.
	 */
	public List<DeliveryStatus> getDeliverytStatuses() {
		logger.info("Generating the list of payment statuses.");
		List<DeliveryStatus> statuses = new ArrayList<DeliveryStatus>();
		statuses.add(DeliveryStatus.getInstanceOf(DeliveryStatus.PARTIALLY_SERVED, "Partially Served"));
		statuses.add(DeliveryStatus.getInstanceOf(DeliveryStatus.FULLY_SERVED, "Fully Served"));
		statuses.add(DeliveryStatus.getInstanceOf(DeliveryStatus.UNSERVED, "Unserved"));
		statuses.add(DeliveryStatus.getInstanceOf(DeliveryStatus.CANCELLED, "Cancelled"));
		logger.info("Successfully generated "+statuses.size()+" delivery  statuses.");
		return statuses;
	}
}

