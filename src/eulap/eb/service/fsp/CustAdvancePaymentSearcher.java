package eulap.eb.service.fsp;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.CustomerAdvancePaymentDao;
import eulap.eb.dao.FormStatusDao;
import eulap.eb.domain.hibernate.CustomerAdvancePayment;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.ResultProperty;

/**
 * Class that will handle the searching of {@link CustomerAdvancePayment}

 *
 */
@Service
public class CustAdvancePaymentSearcher {
	private Logger logger = Logger.getLogger(CustAdvancePaymentSearcher.class);
	@Autowired
	private CustomerAdvancePaymentDao capDao;
	@Autowired
	private FormStatusDao formStatusDao;

	public List<FormSearchResult> search (User user, String searchCriteria, Integer typeId) {
		logger.info("Searching Cash Sale.");
		logger.debug("Searching for: "+searchCriteria.trim());
		Page<CustomerAdvancePayment> caps = capDao.searchCaps(searchCriteria.trim(), typeId, new PageSetting(1));
		List<FormSearchResult> result = new ArrayList<FormSearchResult>();
		String title = null;
		String status = null;
		for (CustomerAdvancePayment cap : caps.getData()) {
			logger.trace("Retrieved CAP No. " + cap.getCapNumber());
			title = String.valueOf(cap.getFormattedCSNumber());
			List<ResultProperty> properties = new ArrayList<ResultProperty>();
			properties.add(ResultProperty.getInstance("Company", cap.getCompany().getName()));
			properties.add(ResultProperty.getInstance("Customer", cap.getArCustomer().getNumberAndName()));
			properties.add(ResultProperty.getInstance("Customer Account", cap.getArCustomerAccount().getName()));
			properties.add(ResultProperty.getInstance("Receipt Date", DateUtil.formatDate(cap.getReceiptDate())));
			properties.add(ResultProperty.getInstance("Maturity Date", DateUtil.formatDate(cap.getMaturityDate())));
			status = formStatusDao.get(cap.getFormWorkflow().getCurrentStatusId()).getDescription();
			properties.add(ResultProperty.getInstance("Status", StringFormatUtil.formatToLowerCase(status)));
			result.add(FormSearchResult.getInstanceOf(cap.getId(), title, properties));
		}
		return result;
	}
}
