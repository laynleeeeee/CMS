package eulap.eb.service.fsp;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.ArTransactionDao;
import eulap.eb.dao.FormStatusDao;
import eulap.eb.domain.hibernate.ArTransaction;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.ResultProperty;

/**
 * A service that handles the searching of AR Transactions.

 *
 */
@Service
public class ArTransactionSearcher implements SearchableFormPlugin{
	@Autowired
	private ArTransactionDao arTransactionDao;
	@Autowired
	private FormStatusDao formStatusDao;


	@Override
	public List<FormSearchResult> search(User user, String searchCriteria) {
		// Replace comma in searchCriteria with ""
		String searchCriteriaFinal = searchCriteria.replace(",", "");

		Page<ArTransaction>   arTransactions =
				arTransactionDao.searchARTransactions(null, searchCriteriaFinal, new PageSetting(1));
		List<FormSearchResult> result = new ArrayList<FormSearchResult>();

		for (ArTransaction transaction : arTransactions.getData()) {
			List<ResultProperty> properties = new ArrayList<ResultProperty>();
			String title = "Transaction#" + transaction.getTransactionNumber();
			properties.add(ResultProperty.getInstance("Sequence No", transaction.getSequenceNumber().toString()));
			properties.add(ResultProperty.getInstance("Type", transaction.getArTransactionType().getName()));
			properties.add(ResultProperty.getInstance("Customer", transaction.getArCustomer().getName()));
			properties.add(ResultProperty.getInstance("Customer Account",
					transaction.getArCustomerAccount().getName()));
			properties.add(ResultProperty.getInstance("Customer", transaction.getArCustomer().getName()));
			properties.add(ResultProperty.getInstance("Term", transaction.getTerm().getName()));
			properties.add(ResultProperty.getInstance("Date", DateUtil.formatDate(transaction.getTransactionDate())));
			if (transaction.getGlDate() != null) {
				properties.add(ResultProperty.getInstance("GL Date", DateUtil.formatDate(transaction.getGlDate())));
			}
			if (transaction.getDueDate() != null) {
				properties.add(ResultProperty.getInstance("Due Date",
						DateUtil.formatDate(transaction.getDueDate())));
			}
			properties.add(ResultProperty.getInstance("Amount",
					NumberFormatUtil.format(transaction.getAmount())));
			String status = formStatusDao.get(transaction.getFormWorkflow().getCurrentStatusId()).getDescription();
			properties.add(ResultProperty.getInstance("Status", status));
			result.add(FormSearchResult.getInstanceOf(transaction.getId(), title, properties));
		}
		return result;
	}
}
