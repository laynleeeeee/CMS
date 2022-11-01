package eulap.eb.service.fsp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.APInvoiceDao;
import eulap.eb.dao.AccountCombinationDao;
import eulap.eb.dao.ApPaymentInvoiceDao;
import eulap.eb.dao.SupplierAccountDao;
import eulap.eb.dao.SupplierDao;
import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.APLine;
import eulap.eb.domain.hibernate.ApPaymentInvoice;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.ResultProperty;

/**
 * Service that handles the searching of AP Invoices. 

 */
@Service
public class APInvoiceSearcher implements SearchableFormPlugin{
	@Autowired
	private APInvoiceDao apInvoiceDao;
	@Autowired
	private SupplierAccountDao supplierAccountDao;
	@Autowired
	private SupplierDao supplierDao;
	@Autowired
	private AccountCombinationDao accountCombinationDao;
	@Autowired
	private ApPaymentInvoiceDao apPaymentInvoiceDao;

	@Override
	public List<FormSearchResult> search(User user, String searchCriteria) {
		Page<APInvoice> invoices = 
				apInvoiceDao.searchAPInvoice(searchCriteria, new PageSetting(1));
		List<FormSearchResult> result = new ArrayList<FormSearchResult>();
		for (APInvoice inv : invoices.getData()) {
			String title = (inv.getInvoiceNumber());
			List<ResultProperty> properties = new ArrayList<ResultProperty>();
			String supplier = supplierDao.get(inv.getSupplierId()).getName();
			String supplierAcct = supplierAccountDao.get(inv.getSupplierAccountId()).getName();
			String status = inv.getFormWorkflow().getCurrentFormStatus().getDescription();
			properties.add(ResultProperty.getInstance("Company", getCompany(inv)));
			properties.add(ResultProperty.getInstance("Supplier", supplier));
			properties.add(ResultProperty.getInstance("Supplier Account", supplierAcct));
			properties.add(ResultProperty.getInstance("Invoice Date", DateUtil.formatDate(inv.getInvoiceDate())));
			properties.add(ResultProperty.getInstance("GL Date", DateUtil.formatDate(inv.getGlDate())));
			properties.add(ResultProperty.getInstance("Due Date", DateUtil.formatDate(inv.getDueDate())));
			properties.add(ResultProperty.getInstance("Amount", NumberFormatUtil.format(inv.getAmount())));
			String strBalance = NumberFormatUtil.format(getBalance(inv));
			properties.add(ResultProperty.getInstance("Balance",strBalance));
			properties.add(ResultProperty.getInstance("Status", status));
			result.add(FormSearchResult.getInstanceOf(inv.getId(), title, properties));
		}
		return result;
	}

	private String getCompany(APInvoice ap){
		List<APLine> apLines = ap.getaPlines();
		String companyName = "";
		for(APLine apl : apLines) {
			companyName = accountCombinationDao.get(apl.getAccountCombinationId()).getCompany().getName();
			return companyName;
		}
		return companyName;
	}

	private double getBalance(APInvoice ap){
		Collection<ApPaymentInvoice> paidInvoices = apPaymentInvoiceDao.getPaidInvoices(ap.getId());
		double balance = 0;
		if(!paidInvoices.isEmpty()) {
			double totalPaidAmount = 0;
			for(ApPaymentInvoice pi : paidInvoices)
				totalPaidAmount += pi.getPaidAmount();
				balance += ap.getAmount() - totalPaidAmount;
				return balance;
		}else{
			balance += ap.getAmount();
		}
		return balance;
	}
}
