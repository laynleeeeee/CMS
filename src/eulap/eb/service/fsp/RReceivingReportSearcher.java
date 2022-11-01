package eulap.eb.service.fsp;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.RReceivingReportDao;
import eulap.eb.domain.hibernate.InvoiceType;
import eulap.eb.domain.hibernate.RReceivingReport;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.ResultProperty;
/**
 * Service that will handle the business logic for searching of {@link RReceivingReport}

 */
@Service
public class RReceivingReportSearcher {
	@Autowired
	private RReceivingReportDao receivingReportDao;

	public List<FormSearchResult> search(User user, String searchCriteria, int typeId) {
		Page<RReceivingReport> searchResult = receivingReportDao.searchReceivingReport(searchCriteria.trim(), typeId, new PageSetting(1));
		List<FormSearchResult> result = new ArrayList<FormSearchResult>();
		String title = null;
		String supplier = null;
		String drNumber = null;
		String status = null;
		String supplierInvoice = null;
		List<ResultProperty> properties = null;
		for(RReceivingReport rr : searchResult.getData()) {
			title = rr.getFormattedRRNumber();
			properties = new ArrayList<ResultProperty>();
			supplier = rr.getApInvoice().getSupplier().getName();
			supplierInvoice = rr.getApInvoice().getInvoiceNumber();
			drNumber = rr.getDeliveryReceiptNo();
			properties.add(ResultProperty.getInstance("Supplier", supplier));
			properties.add(ResultProperty.getInstance("Warehouse", rr.getWarehouse().getName()));
			properties.add(ResultProperty.getInstance("Date", DateUtil.formatDate(rr.getApInvoice().getGlDate())));
			if(rr.getApInvoice().getInvoiceDate() != null) {
				properties.add(ResultProperty.getInstance("Invoice Date", DateUtil.formatDate(rr.getApInvoice().getInvoiceDate())));
			}
			if(typeId == InvoiceType.RR_TYPE_ID || typeId  == InvoiceType.RR_RAW_MAT_TYPE_ID) {
				if(!supplierInvoice.isEmpty()) {
					properties.add(ResultProperty.getInstance("Supplier Invoice", supplierInvoice));
				}
				if(!drNumber.isEmpty()) {
					properties.add(ResultProperty.getInstance("Delivery Receipt No.", drNumber));
				}
			} else if(typeId == InvoiceType.RR_RM_NET_WEIGHT_TYPE_ID) {
				properties.add(ResultProperty.getInstance("Scale Sheet No.", supplierInvoice));
			}
			status = StringFormatUtil.formatToLowerCase(rr.getApInvoice().getFormWorkflow().getCurrentFormStatus().getDescription());
			properties.add(ResultProperty.getInstance("Status", status));
			result.add(FormSearchResult.getInstanceOf(rr.getApInvoiceId(), title, properties));
		}
		return result;
	}
}
