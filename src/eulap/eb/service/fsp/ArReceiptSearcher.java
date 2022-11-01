package eulap.eb.service.fsp;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.ArReceiptDao;
import eulap.eb.domain.hibernate.ArReceipt;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.ResultProperty;

/**
 * A service that handles the searching of AR Receipts.

 */
@Service
public class ArReceiptSearcher implements SearchableFormPlugin {
	@Autowired
	private ArReceiptDao arReceiptDao;
	
	
	@Override
	public List<FormSearchResult> search(User user, String searchCriteria) {
		// Replace comma in searchCriteria with ""
		String searchCriteriaFinal = searchCriteria.replace(",", "");
        
		Page<ArReceipt> arReceipts = arReceiptDao.searchArReceipts(searchCriteriaFinal, new PageSetting(1));
        
		List<FormSearchResult> result = new ArrayList<FormSearchResult>();
		for (ArReceipt arReceipt : arReceipts.getData()) {
			List<ResultProperty> properties = new ArrayList<ResultProperty>();
			String customer = arReceipt.getArCustomer().getName();
			String customerAccount = arReceipt.getArCustomerAccount().getName();
        	
			String title = ("Receipt#" + arReceipt.getReceiptNumber());
			properties.add(ResultProperty.getInstance("Receipt Date", DateUtil.formatDate(arReceipt.getReceiptDate())));
			properties.add(ResultProperty.getInstance("Maturity Date", DateUtil.formatDate(arReceipt.getMaturityDate())));
			properties.add(ResultProperty.getInstance("Customer", customer));
			properties.add(ResultProperty.getInstance("Customer Account", customerAccount));
			properties.add(ResultProperty.getInstance("Amount", NumberFormatUtil.format(arReceipt.getAmount())));
			properties.add(ResultProperty.getInstance("Status", 
					arReceipt.getFormWorkflow().getCurrentFormStatus().getDescription()));
			
			result.add(FormSearchResult.getInstanceOf(arReceipt.getId(), title, properties));
		}
		return result;
	}
}
