package eulap.eb.service.report;

import java.util.Date;

import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.InvoiceRegisterDto;

/**
 * The business logic class of invoice report register. 
 * 

 *
 */
public interface InvoiceRegisterService {
	
	/**
	 * Generate the invoice register report. 
	 *  
	 * Retrieves from the AP_INVOICE TABLE given the parameter that the user selected. 
	 * 
	 * For the parameter please refer to {@link InvoiceRegisterParam}
	 * 
	 * @param user the current logged user. 
	 * @param param The parameters that the user selected.
	 * @return The list of invoice register dto. 
	 */
	Page<InvoiceRegisterDto> generateReport (User user, PageSetting pageSetting, InvoiceRegisterParam param);
	
	/**
	 * Get the total paid amount for this invoice. 
	 * @param apInvoice The invoice object. 
	 * @param asOfDate As of date. 
	 * @return The total paid amount. 
	 */
	Double getTotalPaidAmount(APInvoice apInvoice, Date asOfDate);
}
