package eulap.eb.service.fsp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.RReturnToSupplierDao;
import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.RReturnToSupplier;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.ResultProperty;
/**
 * Service that will handle the business logic for searching of {@link RReturnToSupplier}

 */
@Service
public class RReturnToSupplierSearcher {
	@Autowired
	private RReturnToSupplierDao rReturnToSupplierDao;

	public List<FormSearchResult> search(int invoiceTypeId, User user, String searchCriteria) {
		Page<RReturnToSupplier> returnToSuppliers = 
				rReturnToSupplierDao.searchReturnToSupplier(invoiceTypeId, searchCriteria, new PageSetting(1));
		List<FormSearchResult> result = new ArrayList<FormSearchResult>();
		String title = null;
		String supplier = null;
		String supplierInvoice = null;
		Date invoiceDate = null;
		List<ResultProperty> properties = null;
		for(RReturnToSupplier rts : returnToSuppliers.getData()) {
			title = (rts.getFormattedRTSNumber().toString());
			properties = new ArrayList<ResultProperty>();
			supplier = rts.getApInvoice().getSupplier().getName();
			supplierInvoice = rts.getApInvoice().getInvoiceNumber();
			invoiceDate = rts.getApInvoice().getInvoiceDate();
			properties.add(ResultProperty.getInstance("Supplier", supplier));
			properties.add(ResultProperty.getInstance("RTS Date", DateUtil.formatDate(rts.getApInvoice().getGlDate())));
			if(invoiceDate != null){
				properties.add(ResultProperty.getInstance("Invoice Date", DateUtil.formatDate(invoiceDate)));
			}
			properties.add(ResultProperty.getInstance("Created By", getReturnee(rts.getApInvoice())));
			if(!supplierInvoice.isEmpty())
				properties.add(ResultProperty.getInstance("Supplier Invoice", supplierInvoice));
			properties.add(ResultProperty.getInstance("Status", 
					StringFormatUtil.formatToLowerCase(rts.getApInvoice().getFormWorkflow().getCurrentFormStatus().getDescription())));
			result.add(FormSearchResult.getInstanceOf(rts.getApInvoiceId(), title, properties));
		}
		return result;
	}
	
	private String getReturnee(APInvoice apInvoice) {
		FormWorkflow workflowLog = apInvoice.getFormWorkflow();
		String name = "";
		for(FormWorkflowLog log : workflowLog.getFormWorkflowLogs()) {
			if(log.getFormStatusId() == FormStatus.CREATED_ID) {
				name = log.getCreated().getFirstName() + " " + log.getCreated().getLastName();
			}
		}
		return name;
	}
}
