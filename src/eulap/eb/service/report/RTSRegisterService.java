package eulap.eb.service.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.jasperreports.engine.JRDataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.FormStatusDao;
import eulap.eb.dao.RReturnToSupplierDao;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.jr.EBDataSource;
import eulap.eb.service.jr.EBJRServiceHandler;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.web.dto.ReturnToSupplierRegisterDto;
/**
 * Return To Supplier Register Service.

 *
 */
@Service
public class RTSRegisterService {
	@Autowired
	private RReturnToSupplierDao returnToSupplierDao;
	@Autowired
	private WorkflowServiceHandler workflowServiceHandler;
	@Autowired
	private FormStatusDao formStatusDao;

	/**
	 * Get the Return to supplier register report.
	 * @param companyId The company ID
	 * @param divisionId The Division ID
	 * @param warehouseId The Warehouse ID
	 * @param supplierId The supplier ID
	 * @param supplierAcctId The supplier ID
	 * @param rtsDateFrom Rts start date.
	 * @param rtsDateTo Rts end date.
	 * @param rrDateFrom Rr start date.
	 * @param rrDateTo Rr end date.
	 * @param amountFrom Starting amount
	 * @param amountTo Ending amount
	 * @param statusId The invoice status ID. 
	 * @param paymentStatId The payment status ID.
	 * @return The Return to supplier Register report.
	 */
	public JRDataSource getReturnToSupplierRegisterData(Integer companyId, Integer divisionId, Integer warehouseId,
			Integer supplierId, Integer supplierAcountId, Date rtsDateFrom, Date rtsDateTo,  Date rrDateFrom, Date rrDateTo,
			Double amountFrom, Double amountTo, Integer rtsStatusId, Integer paymentStatId) {
		EBJRServiceHandler<ReturnToSupplierRegisterDto> handler = new JRRHandler(companyId, divisionId,warehouseId, supplierId, supplierAcountId, rtsDateFrom,rtsDateTo,
				rrDateFrom,rrDateTo, amountFrom, amountTo, rtsStatusId, paymentStatId,this);
		return new EBDataSource<ReturnToSupplierRegisterDto>(handler);
	}

	private static class JRRHandler implements EBJRServiceHandler<ReturnToSupplierRegisterDto> {
		private int companyId;
		private int divisionId;
		private int warehouseId;
		private int supplierId;
		private int supplierAcctId;
		private Date rrDateFrom;
		private Date rrDateTo;
		private Date rtsDateFrom;
		private Date rtsDateTo;
		private Double amountFrom;
		private Double amountTo;
		private int rtsStatusId;
		private Integer paymentStatId;
		private RTSRegisterService rtsRegisterService;

		private JRRHandler (Integer companyId, Integer divisionId, Integer warehouseId,
			    Integer supplierId, Integer supplierAcctId, Date rtsDateFrom, Date rtsDateTo,  Date rrDateFrom, Date rrDateTo,
			    double amountFrom, double amountTo, Integer rtsStatusId, Integer paymentStatId, RTSRegisterService rrRegisterService){
			this.companyId = companyId;
			this.divisionId = divisionId;
			this.warehouseId = warehouseId;
			this.supplierId = supplierId;
			this.supplierAcctId = supplierAcctId;
			this.rrDateFrom = rrDateFrom;
			this.rrDateTo = rrDateTo;
			this.rtsDateFrom = rtsDateFrom;
			this.rtsDateTo = rtsDateTo;
			this.amountFrom = amountFrom;
			this.rtsStatusId = rtsStatusId;
			this.amountTo = amountTo;
			this.paymentStatId = paymentStatId;
			this.rtsRegisterService = rrRegisterService;
		}

		@Override
		public void close() throws IOException {
			rtsRegisterService = null;
		}

		@Override
		public Page<ReturnToSupplierRegisterDto> nextPage(PageSetting pageSetting) {
			List<ReturnToSupplierRegisterDto> result = rtsRegisterService.returnToSupplierDao.getReturnToSupplierRegisterData(companyId, divisionId,
					warehouseId, supplierId, supplierAcctId, rtsDateFrom, rtsDateTo, rrDateFrom, rrDateTo,
					amountFrom, amountTo, rtsStatusId, paymentStatId, pageSetting);
			List<ReturnToSupplierRegisterDto> rtsRegisters =  new ArrayList<ReturnToSupplierRegisterDto>();
					for (ReturnToSupplierRegisterDto rts : result) {
						Double balance = 0.0;
						if (rts.getPaidAmount() == 0){
							balance = rts.getAmount();
						}else {
							balance = Math.abs(rts.getAmount()) - Math.abs(rts.getPaidAmount()) ;
						}

						if(rts.getRtsStatusId() == FormStatus.CANCELLED_ID) {
							rts.setAmount(0.0);
							balance = 0.0;
						}

						rts.setBalance(balance);

						if(paymentStatId == -1) {
							if(amountFrom != 0.00){
								if(rts.getAmount() != 0.00){
									rtsRegisters.add(rts);
								}
							}
							else if(amountFrom == 0.00){
								rtsRegisters.add(rts);
							}
						}
					}
			return new Page<ReturnToSupplierRegisterDto>(pageSetting, result, result.size());
		}

	}

	/**
	 * Get the RTS statuses.
	 * @param user The user logged.
	 * @return The RTS statuses.
	 */
	public List<FormStatus> getRtsStatuses(User user) {
		List<FormStatus> statuses = new ArrayList<FormStatus>();
		FormStatus cancelledStatus = formStatusDao.get(FormStatus.CANCELLED_ID);
		statuses = workflowServiceHandler.getAllStatuses("APInvoice31", user, false);
		statuses.add(cancelledStatus);
		return statuses;
	}
}
