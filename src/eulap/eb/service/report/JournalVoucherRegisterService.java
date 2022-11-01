package eulap.eb.service.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import net.sf.jasperreports.engine.JRDataSource;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.GeneralLedgerDao;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.GeneralLedger;
import eulap.eb.domain.hibernate.GlEntry;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.fap.FormStatusService;
import eulap.eb.service.jr.EBDataSource;
import eulap.eb.service.jr.EBJRServiceHandler;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.web.dto.JournalVoucherRegisterDto;

/**
 * Class that handles the business logic for generating Journal Voucher Register report.

 */
@Service
public class JournalVoucherRegisterService {
	@Autowired
	private GeneralLedgerDao glDao;
	@Autowired
	private WorkflowServiceHandler workflowHandler;
	@Autowired
	private FormStatusService formStatusService;

	private static String GENERAL_LEDGER = "GeneralLedger1";

	public Page<JournalVoucherRegisterDto> generateReport(Integer companyId, Integer divisionId,
			String strFromGLDate, String strToGLDate, Integer status, Integer createdBy, Integer updatedBy, PageSetting pageSetting){
		Date fromGLDate = strFromGLDate == null ? null : DateUtil.parseDate(strFromGLDate);
		Date toGLDate = strFromGLDate == null ? null : DateUtil.parseDate(strToGLDate);

		Page<GeneralLedger> generalLedgers =
				glDao.searchJournalVoucherRegister(companyId, divisionId, fromGLDate, toGLDate, status, createdBy, updatedBy, pageSetting);

		Collection<JournalVoucherRegisterDto> journalVoucherRegister =
				new ArrayList<JournalVoucherRegisterDto>();

		for (GeneralLedger gl : generalLedgers.getData()) {
			JournalVoucherRegisterDto jvrDto = new JournalVoucherRegisterDto();
			jvrDto.setSequenceNumber(gl.getSequenceNo());

			// Generate the JV Number. Pattern: JV DIVISON_NAME SEQUENCE_NO
			StringBuilder jvNumber = new StringBuilder();
			jvNumber.append("JV ");
			jvNumber.append(gl.getDivision().getName());
			jvNumber.append(StringUtils.SPACE);
			jvNumber.append(gl.getSequenceNo());
			jvrDto.setJvNumber(jvNumber.toString());

			jvrDto.setGlDate(gl.getGlDate());
			Collection<GlEntry> glEntries = gl.getGlEntries();
			double totalAmount = 0.0;
			if(gl.getFormWorkflow().getCurrentStatusId() != FormStatus.CANCELLED_ID){
				for (GlEntry glEntry : glEntries) {
					if(glEntry.isDebit()){
						// Get amount of one column only == DEBIT Column only.
						totalAmount += glEntry.getAmount();
					}
				}
			} else {
				// Get the Cancellation Remarks if the current status is cancelled
				for (FormWorkflowLog log : gl.getFormWorkflow().getFormWorkflowLogs()) {
					if (log.getFormStatusId() == FormStatus.CANCELLED_ID) {
						jvrDto.setCancellationRemarks(log.getComment());
					}
				}
			}
			jvrDto.setAmount(totalAmount);
			jvrDto.setDescription(gl.getComment());
			jvrDto.setStatus(gl.getFormWorkflow().getCurrentFormStatus().getDescription());
			journalVoucherRegister.add(jvrDto);
		}
		return new Page<JournalVoucherRegisterDto>(pageSetting, journalVoucherRegister, generalLedgers.getTotalRecords());
	}

	/**
	 * Generate the data source for Journal Voucher Register Report.
	 */
	public JRDataSource generateJVRegisterDatasource(Integer companyId, Integer divisionId,
			String strFromGlDate, String strToGlDate, Integer status, Integer createdBy, Integer updatedBy) {
		EBJRServiceHandler<JournalVoucherRegisterDto> jvRegisterHandler = new JournalVoucherJRHandler(this,
				companyId, divisionId, strFromGlDate, strToGlDate, status, createdBy, updatedBy);
		return new EBDataSource<JournalVoucherRegisterDto>(jvRegisterHandler);
	}

	private static class JournalVoucherJRHandler implements EBJRServiceHandler<JournalVoucherRegisterDto> {
		private static Logger logger = Logger.getLogger(JournalVoucherJRHandler.class);
		private final JournalVoucherRegisterService jvRegisterService;
		private final Integer companyId;
		private final Integer divisionId;
		private final String fromGlDate;
		private final String toGlDate;
		private final Integer status;
		private final Integer createdBy;
		private final Integer updatedBy;

		private JournalVoucherJRHandler (JournalVoucherRegisterService jvRegisterService, Integer companyId, Integer divisionId,
				String strFromGlDate, String strToGlDate, Integer status, Integer createdBy, Integer updatedBy) {
			this.jvRegisterService = jvRegisterService;
			this.companyId = companyId;
			this.divisionId = divisionId;
			this.fromGlDate = strFromGlDate;
			this.toGlDate = strToGlDate;
			this.status = status;
			this.createdBy = createdBy;
			this.updatedBy = updatedBy;
		}

		@Override
		public void close() throws IOException {
		}

		@Override
		public Page<JournalVoucherRegisterDto> nextPage(PageSetting pageSetting) {
			logger.info("Generating page "+pageSetting.getPageNumber()+" of the report.");
			Page<JournalVoucherRegisterDto> jVoucherData = jvRegisterService.generateReport(companyId, divisionId,
					fromGlDate, toGlDate, status, createdBy, updatedBy, pageSetting);
			logger.debug("Retrieved "+jVoucherData.getDataSize()+" transactions for the report.");
			return jVoucherData;
		}
	}

	/**
	 * Get all enabled status for general ledger form.
	 * 
	 * @param user The user object.
	 * @return List of form statuses enabled for general ledger form.
	 * @throws ConfigurationException
	 */
	public List<FormStatus> getFormStatuses(User user) throws ConfigurationException {
		List<FormStatus> poStatus = workflowHandler.getAllStatuses(GENERAL_LEDGER, user, false);
		poStatus.add(formStatusService.getFormStatus(FormStatus.CANCELLED_ID));
		return poStatus;
	}
}
