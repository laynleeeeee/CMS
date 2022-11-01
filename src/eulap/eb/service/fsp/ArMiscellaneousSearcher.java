package eulap.eb.service.fsp;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.ArMiscellaneousDao;
import eulap.eb.domain.hibernate.ArMiscellaneous;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.ResultProperty;

/**
 * A service that handles the searching of AR Miscellaneous.

 */
@Service
public class ArMiscellaneousSearcher implements SearchableFormPlugin{
	@Autowired
	private ArMiscellaneousDao arMiscellaneousDao;
	
	@Override
	public List<FormSearchResult> search(User user, String searchCriteria) {
		// Replace comma in searchCriteria with ""
		String searchCriteriaFinal = searchCriteria.replace(",", "");
        
		Page<ArMiscellaneous> arMiscellaneouses = arMiscellaneousDao.searchArMiscellaneous(searchCriteriaFinal, new PageSetting(1));
        
		List<FormSearchResult> result = new ArrayList<FormSearchResult>();
		for (ArMiscellaneous arMiscellaneous : arMiscellaneouses.getData()) {
			List<ResultProperty> properties = new ArrayList<ResultProperty>();
			String customer = arMiscellaneous.getArCustomer().getName();
			String customerAccount = arMiscellaneous.getArCustomerAccount().getName();
        	
			String title = ("Receipt#" + arMiscellaneous.getReceiptNumber());
			properties.add(ResultProperty.getInstance("Receipt Date", DateUtil.formatDate(arMiscellaneous.getReceiptDate())));
			properties.add(ResultProperty.getInstance("Maturity Date", DateUtil.formatDate(arMiscellaneous.getMaturityDate())));
			properties.add(ResultProperty.getInstance("Customer", customer));
			properties.add(ResultProperty.getInstance("Customer Account", customerAccount));
			properties.add(ResultProperty.getInstance("Amount", NumberFormatUtil.format(arMiscellaneous.getAmount())));
			properties.add(ResultProperty.getInstance("Status", 
					arMiscellaneous.getFormWorkflow().getCurrentFormStatus().getDescription()));
			
			result.add(FormSearchResult.getInstanceOf(arMiscellaneous.getId(), title, properties));
		}
		return result;
	}

}
